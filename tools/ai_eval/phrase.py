"""Does offering a short phrase help? At the start of each word, one of the
suggestion slots shows the AI model's guess at the next few words, written in
one press. Everything else is the word strategy chosen in compare.py.

The phrase takes the last slot, so the experiment also pays for what it costs:
a word that would have been in that slot is no longer offered.

    AI_MODEL=gemma3-270m .venv/bin/python -I phrase.py
"""
import json
import math
import sys
from pathlib import Path

import numpy as np

HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import compare as cmp  # noqa: E402
import eval as ev  # noqa: E402

MAX_TOKENS = 12


def continuation(context_words, n_words):
    """Greedy: the model's likeliest next n_words, or fewer if it ends the message."""
    text = ev.PROMPT + " ".join(context_words)
    ids = ev.BOS + ev.tok.encode(text, add_special_tokens=False).ids
    past = {n: np.zeros((1, ev.kv_heads, 0, ev.head_dim), np.float32) for n in ev.kv_names}
    feed, out_ids, total = ids, [], 0
    for _ in range(MAX_TOKENS):
        logits, past = ev.run(np.array([feed], np.int64), np.ones((1, total + len(feed)), np.int64), past)
        total += len(feed)
        nxt = int(np.argmax(logits[0, -1]))
        piece = ev.tok.decode(out_ids + [nxt])
        if nxt in ev.eos or "\n" in piece or any(p in piece for p in ".,!?;:"):
            break
        out_ids.append(nxt)
        feed = [nxt]
        if len(piece.split()) > n_words:  # a further word has started, so n are done
            out_ids.pop()
            break
    words = ev.tok.decode(out_ids).split()
    return words[:n_words]


def main():
    rows = cmp.collect()
    by_sentence = {}
    for r in rows:
        by_sentence.setdefault(r["s"], []).append(r)
    best_word = cmp.context_only(cmp.blend(3))
    sentences = [l.split() for l in (HERE / "sentences_ca.txt").read_text().splitlines() if l.strip()]

    for n_words in (2, 3):
        cache = HERE / f"phrases-{ev.MODEL_NAME}-{n_words}.json"
        phrases = json.loads(cache.read_text()) if cache.exists() else {}
        for si, ws in enumerate(sentences):
            for i in range(len(ws)):
                key = f"{si}:{i}"
                if key not in phrases:
                    phrases[key] = continuation(ws[:i], n_words)
        cache.write_text(json.dumps(phrases, ensure_ascii=False))

        for split, keep in (("tune", 1), ("test", 0)):
            plain = used = taken = 0
            for si, srows in by_sentence.items():
                if si % 2 != keep:
                    continue
                ws = sentences[si]
                i = 0
                while i < len(ws):
                    r = srows[i]
                    phrase = phrases[f"{si}:{i}"]
                    if len(phrase) >= 2 and ws[i:i + len(phrase)] == phrase:
                        # one press for the whole phrase, plus nothing: it ends in a space
                        for w in phrase:
                            plain += len(ev.fold(w)) + 1
                        used += 1
                        taken += 1
                        i += len(phrase)
                        continue
                    plain += len(r["fw"]) + 1
                    cost = len(r["fw"]) + 1
                    for q in r["queries"]:
                        slots = q["slots"] - (1 if q["k"] == 0 and len(phrase) >= 2 else 0)
                        if r["w"] in best_word(r, q)[:slots]:
                            cost = q["k"] + 1
                            break
                    used += cost
                    i += 1
            print(f"{ev.MODEL_NAME} phrases of {n_words} words, {split}: saved {1 - used / plain:.1%}, phrases taken {taken}")


if __name__ == "__main__":
    main()
