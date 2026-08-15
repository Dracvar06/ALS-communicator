# The two-switch box

The app is driven by two switches. It listens for two ordinary keyboard keys —
**space** to write and **enter** to undo — so anything that can send those keys
over Bluetooth will drive it. This folder builds that "anything" from an ESP32.

## The idea

An ESP32 can present itself to the phone as a **Bluetooth keyboard**. You wire
two buttons to it; pressing one makes it send *space*, the other *enter*. The
phone sees a keyboard, the app sees its two switches, and nothing in the app has
to know a switch was involved at all.

Nothing plugs into the phone. It connects to the ESP32 over Bluetooth, through
the air, like any wireless keyboard:

```
  [ switch ] --wires soldered--> [ ESP32 ] ...Bluetooth... [ phone / tablet ]
```

This build solders the switches straight to the board. A 3.5mm jack socket per
switch is an optional extra that would let a switch be unplugged and swapped
without a soldering iron — worth it if her movement is likely to change and you
want tool-free swaps, skippable otherwise.

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
| 2 × switches | A snap-action microswitch (e.g. KW11-3Z) works well, especially with a 3D-printed lever shaped to her movement. Any momentary switch will do. | ~€2 |
| Thin hookup wire | 4 short lengths, two per switch. | ~€1 |
| USB power bank + USB cable | **Easy to forget:** the board needs power the whole time. A power bank runs it for days; the flashing cable can double as the power cable. | — |
| A box | 3D-printed or otherwise. | — |

Optional: heat-shrink or tape over the joints, and a dab of hot glue to anchor
the switch cables so a tug cannot break a solder joint. A LiPo battery with a
charging board is the tidy, self-contained power source once it all works.

Total for the electronics is well under €15. The switches are the part worth
spending on, since they are what her hand actually meets.

## Wiring

Per switch, two connections. A snap-action microswitch has three pins — use
**COM** and **NO**, and leave **NC** unconnected:

```
  switch COM ──▶  GND
  switch NO  ──▶  GPIO pin   (25 = write, 26 = undo)
```

No resistors. The ESP32's internal pull-ups hold each pin HIGH until the switch
closes and pulls it to GND. Pins 25 and 26 are used because they are ordinary
GPIOs with a pull-up; **do not** use 34–39, which are input-only and would float.

If a switch reads as permanently pressed on the check screen, you have wired
**NC** instead of **NO** — move that wire to the other outer pin. (With a
multimeter: press the switch; the two pins that gain continuity are COM and NO.)

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
