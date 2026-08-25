# Roadmap and ideas

Where the project could go next, roughly in order of how much it would help a
real user. Nothing here is committed; it is a place to think out loud so ideas
are not lost. What is already built is in the git history and `CLAUDE.md`.

## What the first session with her actually changed (2026-08-25)

Worth recording, because almost none of it was what the plan expected.

- **The switch was not wanted.** Bluetooth switches, the ESP32 box, the whole
  hardware thread: she would rather touch the screen. The hardware work is not
  wasted — it is still the right answer for someone who cannot reach a screen —
  but it is not the default path any more.
- **Scanning was the wrong shape for her.** Waiting for a highlight to arrive
  and pressing at the right moment is a real skill, and it is not free. She
  wanted to *steer*. Hence arrow mode: four arrows and a choose button, no
  timing at all. Both modes are kept, since which one suits a person cannot be
  decided in advance and may change.
- **A device that sleeps is a person who cannot speak.** The screen going off
  and coming back to a lock screen left her with nothing until somebody else
  picked the tablet up.
- **The explanation failed, not the app.** It was taken to her by somebody who
  had only had it described to them second hand, and who therefore explained it
  wrong; what was judged not to work was the explanation. Hence the walkthrough
  in settings, written for the helper rather than for her, and shown once
  unasked on a fresh install. A tool only one person understands stops working
  the first day that person is not in the room.
- **Nobody could see the battery.** Locked mode hides the system bars, which
  makes the charge invisible right up until the device dies.

### The invisible halves

Reported 2026-08-25: people struggle with the fact that **the letters are not
buttons**, and that in scanning the two real buttons are the unmarked halves of
the screen. It looks like a grid of buttons, so they tap a letter, nothing
happens, and they conclude the app is broken.

Addressed for now with its own walkthrough page and a moving picture of the two
halves washing over the grid. Not yet tried on anyone who had the
misunderstanding, so it is unproven.

If words and a picture turn out not to be enough, the next step is to make the
halves briefly visible: a faint wash on whichever half was last touched, or a
one-off outline the first few times the app is opened. Deliberately not done
yet — anything drawn over the grid covers letters, and the grid is what she
reads all day. It is a cost paid forever to fix a misunderstanding that lasts a
minute, so it should only be paid if teaching genuinely fails.

### Still open in arrow mode

- **Press and hold to repeat.** Left out deliberately: both axes wrap, so
  nothing is more than three presses away, and a hand resting too long on a
  button would pay for it. Worth revisiting if she asks for it.
- **Tapping a letter directly.** Obvious and tempting, and dangerous: a hand
  resting on the screen would type. Would need to be a separate setting.
- **Arrow size and spacing.** The pad takes about a quarter of the width. If
  the buttons turn out to be too small or too close together for her, that
  fraction is the thing to change.

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
