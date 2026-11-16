#!/usr/bin/env python3
"""Builds what the AI build needs next to the model, so the tablet never has to
run a tokenizer:

    python3 tools/build_ai_assets.py MODEL_DIR app/src/ai/assets/ai-model

MODEL_DIR holds an ONNX export of the model (onnx/model_int8.onnx) and its
tokenizer.json, e.g. onnx-community/gemma-3-270m-ONNX. tools/fetch_ai_model.sh
downloads it and runs this.

Output, all read once when the AI is turned on:

    model.onnx         the model itself, copied
    meta.json          the numbers the app needs to feed the model, and the
                       prompt for each language as token ids
    words-ca.tsv ...   every word in that language's n-gram model, with the
                       token ids of " word" (a space then the word)
    pieces.tsv         token text to id, for the few words not in the table
                       (names, words she made up); split greedily on device
    boundary.bin       one bit per token: does this token start a new word?

Why a table works: this tokenizer splits on spaces before anything else, so a
message's tokens are exactly its words' tokens one after another. Checked on
the evaluation sentences (60 of 60 identical). Run this on a laptop; nothing
here runs on the tablet.

Needs: pip install tokenizers onnx
"""

import json
import shutil
import sys
from pathlib import Path

from tokenizers import Tokenizer

ASSETS = Path(__file__).resolve().parent.parent / "app/src/main/assets"

# The line the model continues. Each ends without a trailing space, since the
# words that follow bring their own. The Catalan one is the one measured.
PROMPTS = {
    "ca": "Missatges que una dona amb ELA escriu a la seva família i cuidadores:\n\n-",
    "es": "Mensajes que una mujer con ELA escribe a su familia y cuidadoras:\n\n-",
    "en": "Messages a woman with ALS types to her family and carers:\n\n-",
}


def main(model_dir, out_dir):
    model_dir, out = Path(model_dir), Path(out_dir)
    out.mkdir(parents=True, exist_ok=True)
    tok = Tokenizer.from_file(str(model_dir / "tokenizer.json"))
    config = json.loads((model_dir / "config.json").read_text())

    def ids(text):
        return tok.encode(text, add_special_tokens=False).ids

    onnx_in = model_dir / "onnx" / "model_int8.onnx"
    shutil.copyfile(onnx_in, out / "model.onnx")

    import onnx  # only to read the input names and shapes
    graph = onnx.load(str(onnx_in), load_external_data=False).graph
    kv = [i for i in graph.input if i.name.startswith("past_key_values")]
    dims = kv[0].type.tensor_type.shape.dim

    vocab_size = config["vocab_size"]
    eos = config["eos_token_id"]
    eos = eos if isinstance(eos, list) else [eos]
    meta = {
        "vocabSize": vocab_size,
        "bos": config["bos_token_id"],
        "layers": len(kv) // 2,
        "kvHeads": dims[1].dim_value,
        "headDim": dims[3].dim_value,
        "prompts": {lang: [config["bos_token_id"]] + ids(p) for lang, p in PROMPTS.items()},
    }
    (out / "meta.json").write_text(json.dumps(meta))

    for lang in PROMPTS:
        lines = []
        for line in (ASSETS / f"{lang}-model.txt").read_text().splitlines():
            parts = line.split("\t")
            if len(parts) >= 2 and parts[0] == "u":
                word = parts[1].lower()
                lines.append(word + "\t" + " ".join(map(str, ids(" " + word))))
        (out / f"words-{lang}.tsv").write_text("\n".join(lines) + "\n")

    bits = bytearray((vocab_size + 7) // 8)
    pieces = []
    for i in range(min(vocab_size, tok.get_vocab_size())):
        text = tok.decode([i], skip_special_tokens=False)
        if text.startswith(" ") or text[:1] in ".,!?;:\n" or i in eos:
            bits[i // 8] |= 1 << (i % 8)
        piece = tok.id_to_token(i) or ""
        # Only letter pieces are any use for splitting a word she typed.
        core = piece.replace("▁", "")
        if core and all(c.isalpha() or c in "'·" for c in core):
            pieces.append(piece.replace("▁", " ") + "\t" + str(i))
    (out / "boundary.bin").write_bytes(bytes(bits))
    (out / "pieces.tsv").write_text("\n".join(pieces) + "\n")

    for f in sorted(out.iterdir()):
        print(f"{f.stat().st_size / 1e6:8.1f} MB  {f.name}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    main(sys.argv[1], sys.argv[2])
