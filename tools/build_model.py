#!/usr/bin/env python3
"""Builds the Catalan prediction model that ships inside the app.

    python3 tools/build_model.py ca.txt.gz app/src/main/assets/ca-model.txt

Input is one sentence per line, plain or gzipped. The source used so far is the
Catalan side of OpenSubtitles from OPUS, which is film and television dialogue
and so matches the way someone actually speaks far better than news or
Wikipedia would.

    https://opus.nlpl.eu/OpenSubtitles  (CC BY-SA 4.0)

Output is a text file the app reads once at startup:

    u <word> <count>                        a word and how often it appears
    b <prev> <next> <count> <next> <count>  what tends to follow <prev>

Run this on a laptop. Nothing here ever runs on the tablet.
"""

import gzip
import re
import sys
from collections import Counter, defaultdict

# How much of the model to keep. These are the knobs that trade file size and
# start-up time against how often a useful word is on offer.
VOCAB_SIZE = 30_000
BIGRAM_HEADS = 15_000
SUCCESSORS_PER_HEAD = 8
MIN_WORD_COUNT = 3
MIN_BIGRAM_COUNT = 2
MAX_WORD_LENGTH = 20

# Catalan letters, plus the apostrophe and the middle dot of l·l.
TOKEN = re.compile(r"[a-zàèéíïòóúüç]+(?:['·][a-zàèéíïòóúüç]+)*'?", re.IGNORECASE)

# l', d', s', m', t', n' attach to the front of the next word. Split them off so
# 'l'home' contributes the word 'home' rather than a form nothing can match.
# Enclitics such as deixa'ls are left whole: they belong to the verb.
PROCLITIC = re.compile(r"^([ldsmtn])'(.+)$")


def tokenise(line):
    words = []
    for raw in TOKEN.findall(line.lower()):
        head = PROCLITIC.match(raw)
        if head:
            words.append(head.group(1) + "'")
            raw = head.group(2)
        if len(raw) <= MAX_WORD_LENGTH:
            words.append(raw)
    return words


def read_lines(path):
    opener = gzip.open if path.endswith(".gz") else open
    with opener(path, "rt", encoding="utf-8", errors="replace") as handle:
        for line in handle:
            yield line


def main():
    if len(sys.argv) != 3:
        print(__doc__)
        return 1

    corpus_path, out_path = sys.argv[1], sys.argv[2]

    unigrams = Counter()
    bigrams = defaultdict(Counter)

    sentences = 0
    for line in read_lines(corpus_path):
        words = tokenise(line)
        if not words:
            continue
        sentences += 1
        if sentences % 200_000 == 0:
            print(f"  {sentences:,} sentences", file=sys.stderr)

        unigrams.update(words)
        for first, second in zip(words, words[1:]):
            bigrams[first][second] += 1

    print(f"read {sentences:,} sentences, {len(unigrams):,} distinct words", file=sys.stderr)

    # Rare words are mostly typos, names and the run-together wreckage that
    # subtitle files are full of. Cutting them costs almost no coverage.
    vocab = [w for w, c in unigrams.most_common(VOCAB_SIZE) if c >= MIN_WORD_COUNT]
    allowed = set(vocab)
    print(f"keeping {len(vocab):,} words", file=sys.stderr)

    heads = [w for w in vocab[:BIGRAM_HEADS] if w in bigrams]

    written_bigrams = 0
    with open(out_path, "w", encoding="utf-8") as out:
        for word in vocab:
            out.write(f"u\t{word}\t{unigrams[word]}\n")

        for head in heads:
            # A successor outside the vocabulary can never be shown, so there is
            # no point carrying it.
            best = [
                (nxt, c)
                for nxt, c in bigrams[head].most_common()
                if c >= MIN_BIGRAM_COUNT and nxt in allowed
            ][:SUCCESSORS_PER_HEAD]
            if not best:
                continue
            tail = "\t".join(f"{nxt}\t{c}" for nxt, c in best)
            out.write(f"b\t{head}\t{tail}\n")
            written_bigrams += len(best)

    print(f"wrote {len(vocab):,} words and {written_bigrams:,} follow-on pairs", file=sys.stderr)
    print(f"-> {out_path}", file=sys.stderr)
    return 0


if __name__ == "__main__":
    sys.exit(main())
