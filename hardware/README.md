# The two-switch box

The app is driven by two switches. It listens for two ordinary keyboard keys —
**space** to write and **enter** to undo — so anything that can send those keys
over Bluetooth will drive it. This folder builds that "anything" from an ESP32.

## The idea

An ESP32 can present itself to the phone as a **Bluetooth keyboard**. You wire
two buttons to it; pressing one makes it send *space*, the other *enter*. The
phone sees a keyboard, the app sees its two switches, and nothing in the app has
to know a switch was involved at all.

## Which ESP32, exactly

There are many. You want the **original ESP32** — the module marked
**ESP32-WROOM-32** — on a dev board with a USB port. That chip has the older
"Bluetooth Classic + BLE" radio the keyboard library is best tested against, so
it just works.

Boards that are the right chip, sold under different names — any of these is fine:

- **ESP32 DevKitC** (V4)
- **NodeMCU-32S**
- **ESP32 DevKit v1** (the common 30- or 38-pin board)
- **WEMOS / LOLIN D32**

When reading a listing, check three things:

- It says **ESP32-WROOM-32** (the suffixes 32D / 32E / 32U are all fine).
- It has a **USB port** (micro-USB or USB-C) and a **CP2102 or CH340** chip on
  board — that is what lets you flash it over the cable. A bare module with no
  USB is the wrong thing.
- It does **not** say S2, S3, C3, C6, or H2 (see below).

**Put it back if the name contains:**

| Says | Why not |
| --- | --- |
| ESP32-**S2** | No Bluetooth at all. Will never work. |
| ESP32-**S3** | BLE works but needs extra library setup; avoid the hassle. |
| ESP32-**C3** / **C6** / **H2** | Newer BLE-only chips; fiddlier with this library. |
| "bare module", "no USB" | Nothing to plug the cable into to flash it. |

Buy **two** — they cost a few euros and it is good to have a spare.

## Shopping list

| Part | Notes | Rough cost |
| --- | --- | --- |
| ESP32-WROOM-32 dev board | The board picked above. | €5–8 |
| 2 × accessibility switches | The big round buttons, each ending in a 3.5mm mono jack. This is the AAC standard. | varies |
| 2 × panel-mount 3.5mm mono jack sockets | So a switch can be unplugged and swapped without touching the board. | ~€2 |
| A small box, and USB power | A phone power bank runs it for days; a LiPo makes it tidy later. | — |

Total for the electronics is well under €15. The switches are the part worth
spending on, since they are what her hand actually meets.

## Wiring

Per switch, only two connections:

```
  3.5mm jack TIP    ──▶  GPIO pin   (25 = write, 26 = undo)
  3.5mm jack SLEEVE ──▶  GND
```

No resistors. The ESP32's internal pull-ups hold each pin HIGH until the switch
closes and pulls it to GND. Pins 25 and 26 are used because they are ordinary
GPIOs with a pull-up; **do not** use 34–39, which are input-only and would float.

## Flashing

Full step-by-step is in the header comment of
[`comunicador_switches/comunicador_switches.ino`](comunicador_switches/comunicador_switches.ino).
In short: install the Arduino IDE and ESP32 board support, add the
[ESP32-BLE-Keyboard](https://github.com/T-vK/ESP32-BLE-Keyboard) library, select
"ESP32 Dev Module", and upload.

## Confirming it works

1. Pair **Comunicador** in the phone's Bluetooth settings.
2. In the app, open settings (the corner ⚙) and tap **COMPROVA ELS POLSADORS**.
3. Press each switch. The write switch should read **SPACE**, the undo switch
   **ENTER**. If a single press shows two rows a few milliseconds apart, that is
   contact bounce — raise the debounce in the app, or in the firmware.

## If you would rather not solder

A commercial AAC switch interface (for example an AbleNet Blue2) does the same
job with no electronics: it pairs as a Bluetooth keyboard and sends key presses.
Check which keys it sends using the **COMPROVA ELS POLSADORS** screen, and if
they are not space and enter, tell me and I will add them to the app.
