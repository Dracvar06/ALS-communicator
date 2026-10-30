"""Ways of combining the n-gram list with the AI model's scores, tuned on the
odd sentences and reported on the even ones, so a rule that only fits these
particular sentences shows up as not working on the others.

    .venv/bin/python -I compare.py
"""
import json
import math
import sys
from pathlib import Path

HERE = Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))  # run with -I, which leaves the script's folder off the path
import eval as ev  # noqa: E402

CACHE = HERE / f"scores-{ev.MODEL_NAME}.json"


def collect():
    if CACHE.exists():
        return json.loads(CACHE.read_text())
    sentences = [l.split() for l in (HERE / "sentences_ca.txt").read_text().splitlines() if l.strip()]
    rows = []
    for si, ws in enumerate(sentences):
        for i, w in enumerate(ws):
            fw = ev.fold(w)
            queries = []
            for k in range(len(fw)):
                context = " ".join(ws[:i]) + (" " if i else "") + fw[:k]
                queries.append({"k": k, "slots": 3 if context == "" else 5, "pool": ev.ngram(context, ev.POOL)})
            pooled = sorted({c for q in queries for c in q["pool"]})
            sc = {c: float(v) for c, v in ev.score_words(ws[:i], pooled).items()} if pooled else {}
            rows.append({"s": si, "i": i, "w": w, "fw": fw, "queries": queries, "scores": sc})
    CACHE.write_text(json.dumps(rows, ensure_ascii=False))
    return rows


def presses(rows, order):
    plain = used = 0
    for r in rows:
        plain += len(r["fw"]) + 1
        cost = len(r["fw"]) + 1
        for q in r["queries"]:
            if r["w"] in order(r, q)[: q["slots"]]:
                cost = q["k"] + 1
                break
        used += cost
    return 1 - used / plain


def ngram(r, q):
    return q["pool"]


def rerank(r, q):
    return sorted(q["pool"], key=lambda c: -r["scores"][c])


def blend(beta):
    # AI score plus a prior from the n-gram rank: the list's own order counts too
    def order(r, q):
        return sorted(q["pool"], key=lambda c: -(r["scores"][c] - beta * math.log(q["pool"].index(c) + 1)))
    return order


def keep_top(n, beta=0.0):
    # the n-gram's first n words stay where they are; the AI only fills the rest
    inner = blend(beta)

    def order(r, q):
        head = q["pool"][:n]
        return head + [c for c in inner(r, q) if c not in head]
    return order


def context_only(inner):
    # at the very start of a message the n-gram's openers are left alone
    def order(r, q):
        return ngram(r, q) if r["i"] == 0 else inner(r, q)
    return order


def main():
    rows = collect()
    tune = [r for r in rows if r["s"] % 2 == 1]
    test = [r for r in rows if r["s"] % 2 == 0]
    strategies = {"n-gram (today)": ngram, "AI reorders all": rerank}
    for b in (0.5, 1, 2, 3, 4, 6):
        strategies[f"blend b={b}"] = blend(b)
        strategies[f"blend b={b}, not at start"] = context_only(blend(b))
    for n in (1, 2):
        for b in (0, 1, 2):
            strategies[f"keep top {n}, blend b={b}, not at start"] = context_only(keep_top(n, b))
    results = {name: (presses(tune, f), presses(test, f)) for name, f in strategies.items()}
    best = max((n for n in results if n != "n-gram (today)"), key=lambda n: results[n][0])
    print(f"{'strategy':45} tune   test")
    for name, (a, b) in results.items():
        print(f"{name:45} {a:.1%}  {b:.1%}{'   <- best on tune' if name == best else ''}")
    out = {"tune_sentences": len({r['s'] for r in tune}), "test_sentences": len({r['s'] for r in test}),
           "best": best, "results": {n: {"tune": round(a, 3), "test": round(b, 3)} for n, (a, b) in results.items()}}
    (HERE / f"compare-{ev.MODEL_NAME}.json").write_text(json.dumps(out, indent=1))


if __name__ == "__main__":
    main()
