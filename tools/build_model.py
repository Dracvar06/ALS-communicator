#!/usr/bin/env python3
"""Builds a prediction model that ships inside the app.

    python3 tools/build_model.py ca.txt.gz app/src/main/assets/ca-model.txt
    python3 tools/build_model.py --lang fr fr.txt.gz app/src/main/assets/fr-model.txt

--lang picks which letters count as part of a word and which short words
are split off the front (l'home, j'ai, dell'acqua). It defaults to ca.

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

# The letters each language writes words with. A letter missing from here
# splits a word in two, so "año" would be learnt as "a" and "o". Only the
# lower case forms are needed, since text is lower-cased first.
LETTERS = {
    "ca": "a-zàèéíïòóúüç",
    "es": "a-zñáéíóúü",
    "en": "a-z",
    "fr": "a-zàâæçéèêëîïôœùûüÿ",
    "it": "a-zàèéìíîòóùú",
    "pt": "a-záâãàçéêíóôõú",
    "de": "a-zäöüß",
    "gl": "a-zñáéíóúü",
    "eu": "a-zñ",
    "ro": "a-zăâîșț",
    # The Arabic letters proper, without the vowel marks (stripped below) or
    # the tatweel stretching stroke, which are decoration rather than spelling.
    "ar": "\u0621-\u063a\u0641-\u064a",
}

# Short words that attach to the front of the next one with an apostrophe.
# They are split off so "l'home" contributes "home" rather than a form nothing
# can match. Enclitics such as deixa'ls are left whole: they belong to the verb.
# Languages missing here never split: English "don't" is one word.
PROCLITICS = {
    "ca": r"[ldsmtn]",
    "fr": r"[ldjmnstc]|qu|jusqu|lorsqu|puisqu",
    "it": r"[ldcmnstv]|un|dell|all|nell|sull|dall|coll|quest|quell|tutt",
}

# Vowel marks and other combining accents in Arabic script are almost never
# written in everyday text; where they are, they would make the same word
# count as several different ones.
ARABIC_MARKS = re.compile("[\u064b-\u0652\u0670\u0640]")

# Romanian is often typed with the older cedilla letters ş ţ instead of the
# correct comma-below ș ț. Count them as the same word.
ROMANIAN_FIXES = str.maketrans({"ş": "ș", "ţ": "ț"})

LANG = "ca"
TOKEN = None
PROCLITIC = None


def set_language(code):
    global LANG, TOKEN, PROCLITIC
    if code not in LETTERS:
        raise SystemExit(f"unknown language {code!r}; known: {', '.join(LETTERS)}")
    LANG = code
    letters = LETTERS[code]
    if code == "ca":
        # Catalan also joins words with the middle dot of l·l.
        TOKEN = re.compile(rf"[{letters}]+(?:['·][{letters}]+)*'?", re.IGNORECASE)
    else:
        TOKEN = re.compile(rf"[{letters}]+(?:'[{letters}]+)*'?", re.IGNORECASE)
    proclitics = PROCLITICS.get(code)
    PROCLITIC = re.compile(rf"^({proclitics})'(.+)$") if proclitics else None


def tokenise(line):
    line = line.lower().replace("\u2019", "'")
    if LANG == "ar":
        line = ARABIC_MARKS.sub("", line)
    elif LANG == "ro":
        line = line.translate(ROMANIAN_FIXES)
    words = []
    for raw in TOKEN.findall(line):
        head = PROCLITIC.match(raw) if PROCLITIC else None
        if head:
            words.append(head.group(1) + "'")
            raw = head.group(2)
        if len(raw) <= MAX_WORD_LENGTH:
            words.append(raw)
    return words


def read_lines(path):
    # "-" reads plain text from standard input, so a huge corpus can be
    # streamed and cut short rather than downloaded whole. The English
    # subtitle dump is 9.5GB; a couple of million sentences is plenty.
    if path == "-":
        for line in sys.stdin:
            yield line
        return

    opener = gzip.open if path.endswith(".gz") else open
    with opener(path, "rt", encoding="utf-8", errors="replace") as handle:
        for line in handle:
            yield line


def main():
    args = sys.argv[1:]
    language = "ca"
    if len(args) >= 2 and args[0] == "--lang":
        language, args = args[1], args[2:]
    if len(args) != 2:
        print(__doc__)
        return 1

    set_language(language)
    corpus_path, out_path = args

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
