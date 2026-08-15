# The two-switch box

The app is driven by two switches. It listens for two ordinary keyboard keys —
**space** to write and **enter** to undo — so anything that can send those keys
over Bluetooth will drive it. This folder builds that "anything" from an ESP32.

## The idea

An ESP32 can present itself to the phone as a **Bluetooth keyboard**. You wire
two buttons to it; pressing one makes it send *space*, the other *enter*. The
phone sees a keyboard, the app sees its two switches, and nothing in the app has
to know a switch was involved at all.

## Shopping list

| Part | Notes | Rough cost |
| --- | --- | --- |
| ESP32-WROOM-32 dev board | Sold as "ESP32 Dev Module". The plain classic ESP32 — not an S2 (no Bluetooth), and simpler than S3/C3 for this. | €5–8 |
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
