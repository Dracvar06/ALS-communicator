# Third-party notices

The Comunicador is licensed under the GNU General Public License v3.0 — see
[LICENSE](LICENSE). That covers the code written for this project.

It also carries other people's work. Each piece below keeps its own licence,
and those licences continue to apply no matter what happens to this one. If you
redistribute the app, in any form, you carry these with it.

---

## Atkinson Hyperlegible

**Copyright 2020 Braille Institute of America, Inc.**
Licensed under the SIL Open Font License, Version 1.1.

Files: `app/src/main/res/font/atkinson_hyperlegible_*.ttf`
Licence text: [OFL-AtkinsonHyperlegible.txt](OFL-AtkinsonHyperlegible.txt)

The typeface was drawn for readers with low vision, and is the reason the
letters on the grid stay distinct at a glance. What the licence asks:

- keep the licence text with the font, which this repository does;
- do not sell the font on its own;
- if you modify the font, rename it — "Atkinson Hyperlegible" is a Reserved
  Font Name and a changed version may not use it.

<https://github.com/googlefonts/atkinson-hyperlegible>

---

## The prediction models

Files: `app/src/main/assets/ca-model.txt`, `es-model.txt`, `en-model.txt`

These are not written by hand. They are word counts and word-pair counts
computed from the **OpenSubtitles** corpora distributed by **OPUS**, using
[`tools/build_model.py`](tools/build_model.py). They contain no sentences from
the corpus, only how often words appear and what tends to follow what.

Please credit OPUS and OpenSubtitles when you redistribute, and cite:

> P. Lison and J. Tiedemann (2016). *OpenSubtitles2016: Extracting Large
> Parallel Corpora from Movie and TV Subtitles.* Proceedings of LREC 2016.

<https://opus.nlpl.eu/OpenSubtitles/> · <https://www.opensubtitles.org/>

**An honest caveat.** The licensing of subtitle-derived corpora is not as crisp
as a software licence. OPUS provides the data for research, the widely used
frequency lists built from it are published under CC BY-SA 4.0, and the
underlying subtitles were written by many people. Counting word frequencies is
ordinarily well clear of anything protectable, and no subtitle text is
reproduced here — but if this is ever sold, bundled commercially, or shipped by
an organisation that needs certainty, get the question answered properly first
rather than relying on this paragraph.

Rebuilding the models from a corpus whose terms suit you is straightforward:
`build_model.py` takes any plain text on standard input.

---

## Libraries

Used at build time and bundled into the app:

| Library | Licence |
| --- | --- |
| Kotlin standard library, JetBrains | Apache License 2.0 |
| AndroidX / Jetpack Compose, Google | Apache License 2.0 |
| AndroidX Lifecycle, Core, Activity | Apache License 2.0 |

Used only to run the tests, never shipped inside the app:

| Library | Licence |
| --- | --- |
| JUnit 4 | Eclipse Public License 1.0 |

---

## Why GPL-3.0 for this project

It is a deliberate choice, not a default. This is a tool people may come to
depend on in order to speak. The GPL means anyone may use it, study it, adapt it
for their own family member, and share it — but a modified version that is given
out must come with its source, so an improvement made for one person cannot be
locked away from the next.

If you would rather it were permissive, now is the cheap moment to change it,
while the only author is you. Once other people have contributed under this
licence, changing it means asking all of them.
