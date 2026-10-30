"""Cheaper scoring: run the model once per finished word, and score each
candidate by its first token only. Every keystroke after that is a lookup.
    AI_MODEL=gemma3-1b .venv/bin/python -I firsttoken.py
"""
import json, sys, time
from pathlib import Path
import numpy as np
HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import compare as cmp  # noqa: E402
import eval as ev  # noqa: E402

rows = cmp.collect()
cache, t0 = {}, time.time()
times = []
for r in rows:
    key = (r["s"], r["i"])
    sentence = [l.split() for l in (HERE / "sentences_ca.txt").read_text().splitlines() if l.strip()][r["s"]]
    text = ev.PROMPT + " ".join(sentence[: r["i"]])
    ids = ev.BOS + ev.tok.encode(text, add_special_tokens=False).ids
    empty = {n: np.zeros((1, ev.kv_heads, 0, ev.head_dim), np.float32) for n in ev.kv_names}
    t = time.time()
    logits, _ = ev.run(np.array([ids], np.int64), np.ones((1, len(ids)), np.int64), empty)
    times.append(time.time() - t)
    first = ev.logsoftmax(logits[0, -1])
    sep = "" if text.endswith(" ") else " "
    for c in r["scores"]:
        sq = ev.tok.encode(text + sep + c, add_special_tokens=False).ids[len(ids) - len(ev.BOS):]
        r["scores"][c] = float(first[sq[0]]) if sq else -99.0
tune = [r for r in rows if r["s"] % 2 == 1]; test = [r for r in rows if r["s"] % 2 == 0]
for b in (1, 2, 3, 4):
    f = cmp.context_only(cmp.blend(b))
    print(f"{ev.MODEL_NAME} first-token only, blend b={b}, not at start: tune {cmp.presses(tune, f):.1%}  test {cmp.presses(test, f):.1%}")
print(f"one context run per word, {ev.opts.intra_op_num_threads} threads: median {1000*sorted(times)[len(times)//2]:.0f} ms")
