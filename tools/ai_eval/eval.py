"""How many presses does word prediction save her? N-gram (what the app does
today) against the same n-gram list reordered by a small on-device AI model.

Simulates a perfect user: for each word she types letters one at a time and
takes the word as soon as it is among the suggestions shown (3 on an empty
screen, 5 otherwise, as in the app). Each letter, each suggestion taken and
each space is one press (one selection on the grid).

    tools/fetch_ai_model.sh   (once: makes .venv and models/)
    .venv/bin/python -I eval.py [sentences_ca.txt]
"""
import json
import os
import sys
import time
import unicodedata
from pathlib import Path

import numpy as np
import onnxruntime as ort
from tokenizers import Tokenizer

HERE = Path(__file__).resolve().parent
ASSET = HERE.parent.parent / "app/src/main/assets/ca-model.txt"
MODEL_NAME = os.environ.get("AI_MODEL", "gemma3-270m")  # a folder in models/
MODEL = HERE / "models" / MODEL_NAME
POOL = int(os.environ.get("AI_POOL", "30"))  # n-gram candidates the AI model may reorder
OPENERS = ["vull", "necessito", "ajuda", "gràcies", "si us plau", "aigua", "dolor", "lavabo",
           "menjar", "fred", "calor", "cansada", "espera", "para", "hola", "estic", "tinc",
           "puc", "on", "què"]
PROMPT = "Missatges que una dona amb ELA escriu a la seva família i cuidadores:\n\n- "


def fold(s):
    return "".join(c for c in unicodedata.normalize("NFD", s.lower()) if unicodedata.category(c) != "Mn")


# ---- the app's n-gram predictor, ported from NgramPredictor.kt (no personal
# model yet, as on a fresh install; no forgiving, since this user never mistypes)

words, successors, buckets = [], {}, {}
for line in ASSET.read_text().splitlines():
    p = line.split("\t")
    if len(p) >= 2 and p[0] == "u":
        words.append(p[1])
    elif len(p) >= 4 and p[0] == "b":
        successors.setdefault(fold(p[1]), [p[i] for i in range(2, len(p), 2)])
folded = [fold(w) for w in words]
for i, f in enumerate(folded):
    if f:
        buckets.setdefault(f[0], []).append(i)


def ngram(context, limit):
    prefix = fold(context.rsplit(" ", 1)[-1])
    previous = fold(context.rsplit(" ", 1)[0].rsplit(" ", 1)[-1]) if " " in context else ""
    chosen = []

    def take(cands):
        for c in cands:
            if len(chosen) >= limit:
                return
            if (not prefix or fold(c).startswith(prefix)) and c not in chosen:
                chosen.append(c)

    take(successors.get(previous, []) if previous else [])
    if not prefix:
        take(OPENERS)
    if len(chosen) < limit:
        if not prefix:
            take(words[: limit + len(chosen)])
        else:
            found = []
            for i in buckets.get(prefix[0], []):
                if folded[i].startswith(prefix):
                    found.append(words[i])
                    if len(found) == limit + len(chosen):
                        break
            take(found)
    return chosen


# ---- the AI model: scores how likely each candidate word is to come next

tok = Tokenizer.from_file(str(MODEL / "tokenizer.json"))
config = json.loads((MODEL / "config.json").read_text())
opts = ort.SessionOptions()
# 2 is roughly what an old tablet can spare; for measuring quality only,
# AI_THREADS=0 uses every core (speed then says nothing about a tablet)
opts.intra_op_num_threads = int(os.environ.get("AI_THREADS", "2"))
onnx_file = next(MODEL / "onnx" / f for f in ("model_int8.onnx", "model_q4.onnx") if (MODEL / "onnx" / f).exists())
sess = ort.InferenceSession(str(onnx_file), opts)
inputs = {i.name: i for i in sess.get_inputs()}
kv_names = [n for n in inputs if n.startswith("past_key_values")]
kv_heads, head_dim = inputs[kv_names[0]].shape[1], inputs[kv_names[0]].shape[3]
# GroupQueryAttention exports refuse batch > 1 with a cache (Gemma 3 1B)
ONE_ROW_WITH_CACHE = "GQA" in MODEL_NAME or MODEL_NAME == "gemma3-1b"
V = sess.get_outputs()[0].shape[-1]
if not isinstance(V, int):
    V = config["vocab_size"]
# Gemma expects <bos> first; Qwen's base model was trained without one
BOS = [config["bos_token_id"]] if config.get("model_type", "").startswith("gemma") else []
# a token that starts a new word: so the word before it is finished
eos = config.get("eos_token_id")
eos = set(eos if isinstance(eos, list) else [eos])
boundary = np.zeros(V, bool)
for i in range(min(V, tok.get_vocab_size())):
    t = tok.decode([i], skip_special_tokens=False)
    boundary[i] = t.startswith(" ") or t[:1] in ".,!?;:\n" or i in eos


def run(ids, mask, past):
    feeds = {"input_ids": ids, "attention_mask": mask}
    if "position_ids" in inputs:
        start = next(iter(past.values())).shape[2]
        feeds["position_ids"] = np.tile(np.arange(start, start + ids.shape[1], dtype=np.int64), (ids.shape[0], 1))
    feeds.update(past)
    out = sess.run(None, feeds)
    present = {n: out[1 + k] for k, n in enumerate(kv_names)}
    return out[0], present


def logsoftmax(x):
    m = x.max(-1, keepdims=True)
    return x - m - np.log(np.exp(x - m).sum(-1, keepdims=True))


def score_words(context_words, cands):
    """log P(word, then a word boundary | context) for every candidate, using
    the context's cache once and all candidates in one batch."""
    text = PROMPT + " ".join(context_words)
    ids = BOS + tok.encode(text, add_special_tokens=False).ids
    empty = {n: np.zeros((1, kv_heads, 0, head_dim), np.float32) for n in kv_names}
    logits, past = run(np.array([ids], np.int64), np.ones((1, len(ids)), np.int64), empty)
    first = logsoftmax(logits[0, -1])
    sep = "" if text.endswith(" ") else " "
    seqs = [tok.encode(text + sep + c, add_special_tokens=False).ids[len(ids) - len(BOS):] for c in cands]
    # the joint encoding can merge across the boundary; fall back to the word alone
    seqs = [s if s else tok.encode(sep + c, add_special_tokens=False).ids for s, c in zip(seqs, cands)]
    n, L = len(cands), max(len(s) for s in seqs)
    batch = np.zeros((n, L), np.int64)
    mask = np.zeros((n, len(ids) + L), np.int64)
    mask[:, : len(ids)] = 1
    for j, s in enumerate(seqs):
        batch[j, : len(s)] = s
        mask[j, len(ids): len(ids) + len(s)] = 1
    if ONE_ROW_WITH_CACHE:
        # this export can't batch on top of a cache, so one candidate at a
        # time, each on top of the context's cache, scored as it comes
        scores = []
        for sq in seqs:
            out, _ = run(np.array([sq], np.int64), np.ones((1, len(ids) + len(sq)), np.int64), past)
            lpj = logsoftmax(out[0])
            total = first[sq[0]] + sum(lpj[k - 1, sq[k]] for k in range(1, len(sq)))
            total += np.log(np.exp(lpj[len(sq) - 1])[boundary].sum() + 1e-12)
            scores.append(total)
        return dict(zip(cands, scores))
    rep = {k: np.repeat(v, n, axis=0) for k, v in past.items()}
    lg, _ = run(batch, mask, rep)
    lp = logsoftmax(lg)
    scores = []
    for j, s in enumerate(seqs):
        total = first[s[0]] + sum(lp[j, k - 1, s[k]] for k in range(1, len(s)))
        total += np.log(np.exp(lp[j, len(s) - 1])[boundary].sum() + 1e-12)
        scores.append(total)
    return dict(zip(cands, scores))


# ---- simulation

def main():
    sentences = [l.split() for l in (HERE / (sys.argv[1] if len(sys.argv) > 1 else "sentences_ca.txt")).read_text().splitlines() if l.strip()]
    plain = ng_cost = ai_cost = 0
    misses_pool = total_words = 0
    lm_time, lm_calls = 0.0, 0
    examples = []
    for words_ in sentences:
        for i, w in enumerate(words_):
            total_words += 1
            fw = fold(w)
            plain += len(fw) + 1
            ctx_words = words_[:i]
            queries = []
            for k in range(len(fw)):
                context = " ".join(ctx_words) + (" " if ctx_words else "") + fw[:k]
                slots = 3 if context == "" else 5
                queries.append((k, slots, ngram(context, POOL)))
            pooled = sorted({c for _, _, pool in queries for c in pool})
            t = time.time()
            sc = score_words(ctx_words, pooled) if pooled else {}
            lm_time += time.time() - t
            lm_calls += 1
            if w not in pooled:
                misses_pool += 1

            def cost(order):
                for k, slots, pool in queries:
                    if w in order(pool)[:slots]:
                        return k + 1
                return len(fw) + 1

            c_ng = cost(lambda pool: pool)
            c_ai = cost(lambda pool: sorted(pool, key=lambda c: -sc[c]))
            ng_cost += c_ng
            ai_cost += c_ai
            if c_ai != c_ng and len(examples) < 12:
                examples.append(f"{' '.join(ctx_words)} [{w}]: n-gram {c_ng}, AI {c_ai} presses")
    res = {
        "sentences": len(sentences), "words": total_words,
        "presses_no_prediction": plain, "presses_ngram": ng_cost, "presses_ai": ai_cost,
        "saved_ngram": round(1 - ng_cost / plain, 3), "saved_ai": round(1 - ai_cost / plain, 3),
        "word_not_in_ngram_pool": misses_pool,
        "ai_seconds_per_word_2_threads": round(lm_time / lm_calls, 3),
    }
    print(json.dumps(res, indent=1))
    print("\n".join(examples))
    (HERE / "results.json").write_text(json.dumps(res, indent=1))


if __name__ == "__main__":
    main()
