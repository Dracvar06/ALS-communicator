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
