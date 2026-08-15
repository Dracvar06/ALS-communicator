# Project brief: single-switch scanning communication app

## What this is

An Android tablet app that lets a person who cannot speak or type compose text
using a single physical switch. A cursor scans automatically through a grid of
letters. The user presses the switch once to select whatever the cursor is on.

The end user is a woman with a severe motor impairment. She can operate one
switch reliably. She used an earlier device (built in MIT App Inventor, a
keyboard she typed on directly) for about five years, and can no longer use it.
This app replaces it.

The tablet will be arm-mounted on her wheelchair and travel with her all day.

This is not a demo or a portfolio project. Someone will depend on it to speak.
When in doubt, choose the boring reliable option.

## Hard constraints

These are not preferences. Violating any of them is a bug.

1. **The scan must never stutter.** The cursor moves at a fixed interval. No
   prediction work, disk I/O, or allocation-heavy code may run on the thread
   that drives it. If something might take longer than a few milliseconds, it
   runs elsewhere and its result is picked up on the next cycle.
2. **One input.** The entire app must be operable with a single key event.
   Never require a second button, a swipe, a long press, or a timed double
   press.
3. **No dead ends.** Every screen, menu, and dialog must be exitable with the
   switch alone. If the app can reach a state where she cannot get out, it has
   failed completely.
4. **No network.** Version one works fully offline. No analytics, no crash
   reporting to a server, no cloud calls.
5. **Screen stays on.** Wake lock held while the app is in the foreground.
6. **Fullscreen and locked.** No system bars, no way to accidentally leave the
   app. Use screen pinning.
7. **Text survives.** If the app is killed or the tablet reboots, the text she
   was composing must still be there when it comes back.
8. **Everything is configurable without a rebuild.** Scan speed above all.
   Config lives in a file a carer can edit, not hardcoded.

## Architecture

Four layers, kept strictly separate. The first three must have zero Android
imports so they can be unit tested on a laptop with no emulator.

    scan/        Pure Kotlin. Owns the scan state machine. Given a layout and a
                 tick, emits which cell is currently highlighted. Knows nothing
                 about screens, switches, or Android.

    input/       Turns a raw key event into a single Select action. Handles
                 debounce and an optional ignore window after a press.

    prediction/  A single interface:
                   fun predict(context: String, limit: Int): List<String>
                 Version one returns an empty list. The n-gram implementation
                 comes later. This seam exists so the engine can be swapped
                 without touching anything else.

    ui/          Compose. Draws the grid, highlights the current cell, shows the
                 composed text. Contains no scanning logic whatsoever.

## Stack

- Kotlin, Jetpack Compose
- Minimum SDK 26
- No third-party dependencies unless there is a clear reason. Justify each one.
- JUnit for the pure layers

## Coding rules

- Write tests for `scan/` and `input/` before the UI exists. Scan order, timing,
  row entry and exit, and mis-press recovery are all testable without a device.
- Small commits. One behaviour per commit.
- No clever code. This will be read by someone who does not write Kotlin daily.
- Comment the *why*, not the *what*, especially anywhere timing matters.
- Do not add features that were not asked for. Extra buttons cost scan time,
  and scan time is the scarcest resource in the whole system.

## Roadmap

**Milestone 1: it exists.** Fullscreen grid of Catalan letters, large and high
contrast. Row-column scanning: cursor steps down the rows, switch press enters a
row, cursor then steps across that row, switch press selects the letter. Text
appears at the top. Grid includes space, delete, and clear. Scan interval read
from config. Nothing else. No prediction.

**Milestone 2: it survives real use.** Wake lock, screen pinning, text
persistence across restart, a way to recover from entering the wrong row, and a
settings screen reachable only by a deliberate gesture a carer knows.

**Milestone 3: prediction.** Catalan n-gram model built offline by a separate
Python script and shipped as an app asset. Word suggestions appear as a row in
the grid. Letters are reordered by likelihood on each keystroke, which is
probably the bigger speed win of the two.

**Milestone 4: personalisation.** Weight her own past writing heavily in the
model. Keep this data on the device only.

**Later, not now:** keyword expansion using a language model, text to speech,
and any form of syncing.

## Input hardware

A commercial or DIY Bluetooth switch interface presents itself as a keyboard and
sends a single key (assume Space) when the switch closes. The app listens for
that key. Keep a 3.5mm mono jack on the switch side so the physical switch itself
can be swapped as her movement changes, without touching the app.

## Things to check before building, not after

- Turn on Android's built-in Switch Access on a tablet and use it for an hour.
  It solves this same problem and will reveal interaction cases not listed here,
  particularly around mis-presses and getting out of a wrongly entered row.
- Confirm the Bluetooth switch interface actually delivers key events to a
  foreground app while screen pinning is active.

## How to work with me on this

Ask before assuming. If a requirement here is ambiguous, say so rather than
picking an interpretation quietly. If you think one of the hard constraints is
wrong, argue the case; do not silently work around it.
