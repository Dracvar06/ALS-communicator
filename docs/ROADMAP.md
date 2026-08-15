# Roadmap and ideas

Where the project could go next, roughly in order of how much it would help a
real user. Nothing here is committed; it is a place to think out loud so ideas
are not lost. What is already built is in the git history and `CLAUDE.md`.

## Known gaps, deliberately left for now

- **Screen pinning / kiosk mode.** With two switches she cannot get back into
  the app if she lands on the home screen. Locking the tablet into the app
  closes that trap. (Brief milestone 2.)
- **The sentence in progress does not survive a reboot.** Speed, debounce and
  her learned words persist; the text does not. A restart mid-sentence loses it.
- **Safe-area padding for notched screens.** The app draws edge to edge, so on a
  device with a camera cutout an edge letter could be hidden. Fine on plain
  tablets.
- **Adjustable margins in settings** (top / bottom / left / right). Requested
  2026-08-15. A simple way to nudge the whole grid inward on an awkward screen
  or mount, and a lightweight substitute for full cutout handling. Low priority.

## Ideas that would help her communicate

- **Speak the sentence aloud (text to speech).** The biggest single change. Right
  now she writes and someone reads over her shoulder; with a SPEAK action the
  tablet becomes a *voice* she can use across a room or with someone not looking.
  Android has built-in TTS; a Catalan voice can be installed. This is what turns
  a writing aid into an actual communicator.
- **Quick phrases.** A few pinned sentences she says often — "tinc dolor",
  "gira'm si us plau", "truca a la infermera", "t'estimo" — reachable in one or
  two presses instead of spelling them out. Enormous effort saved on the things
  said most.
- **An attention signal.** A cell that plays a loud sound to call someone when
  she needs help and nobody is looking. For a person who cannot call out, this
  is closer to safety than convenience.
- **Auditory scanning.** Announce the highlighted row or letter through an
  earbud, so she can select by sound when watching the screen tires her eyes —
  common as ALS progresses. Would let her rest her eyes and still communicate.

## Languages and customization (making it other people's, not just hers)

The goal: a relative who helps a new user can set it up in their language and
with their own words, without a developer and without recompiling.

What is already swappable:

- **The prediction model.** `tools/build_model.py` builds the offline model
  from any corpus, so a new language is a new run of that script over text in
  that language. The predictor layer does not care which language it holds.

What needs externalizing so a helper can edit it:

- **The phrases.** Store them in an editable file the app reads at startup
  (a plain list, one phrase per line), kept in the app's own storage, plus a
  simple carer-facing edit screen reached by touch — so no developer is needed
  and, as noted, a relative can do it. This is the first and most requested
  piece.
- **The on-screen labels** (SÍ, NO, espai, TANCA, TORNA, …) become Android
  string resources per language, which is the standard way and gives every
  language its own wording for free.
- **The grid itself** — which letters, and their frequency order — differs per
  language. Make the layout a small data file per "language pack" (letters +
  their order + opener list + the model file) rather than code.

A "language pack" is then: a model file, a letter layout, an opener list, and a
set of labels. Ship a few (Catalan, Spanish, English) and document the format so
others can add their own. The phrases stay per-user, edited by their helper.

## Her own voice — voice banking and cloning

Short answer: **yes, and there is a free route for exactly this case.**

- **ElevenLabs runs a free program for people with ALS/MND.** A US-based patient
  can get a free Pro voice-clone licence (otherwise ~$1,200/year), applied for
  directly on their site or via Bridging Voice / the Scott-Morgan Foundation.
  Verify current eligibility and terms, since these change.
- **Audio needed:** 30 minutes is the floor; 2–3 hours of clean, quiet,
  naturally varied speech gives the best clone. One speaker, good mic, no
  background noise. Existing recordings (videos, voicemails) can be used if her
  live voice has already declined.
- **Time matters.** If her voice is still clear, record as much as possible
  *soon*. This is voice banking, and it is easier now than later.
- **How it fits this app, offline.** The phrases screen has a *fixed* set of
  phrases, so we can pre-generate her cloned voice saying each one and bundle
  those audio clips as assets. The buttons then speak in *her real voice* with
  no network at all — which keeps the app's offline promise intact. Free-typed
  sentences would need either the online voice API or the device's built-in
  voice; the phrase soundboard is where her own voice lands most naturally.

## Ideas that help it scale to more people

- **Backup and restore** of her personal dictionary and phrases, so moving to a
  new tablet does not lose everything the app has learned about her.
- **A signed release build** and a simple install / update path, so a carer can
  set it up without a developer.
- **Other languages.** The prediction model and the on-screen labels are already
  swappable; the same app could serve Spanish, English, and beyond, each with
  its own offline model built by `tools/build_model.py`.
- **A shared, cleaned opener and phrase list** per language, so each new user
  starts from something sensible rather than a blank slate.
