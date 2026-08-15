// Comunicador — two-switch Bluetooth interface for an ESP32.
//
// The ESP32 pretends to be a Bluetooth keyboard. Two accessibility switches are
// wired to it; pressing one sends SPACE (write), the other sends ENTER (undo).
// Those are the two keys the app already listens for, so nothing in the app has
// to change: pair this like any Bluetooth keyboard and it works.
//
// ---------------------------------------------------------------------------
// What you need
//   - An ESP32 dev board. A plain ESP32-WROOM-32 ("ESP32 Dev Module") is the
//     safe choice; it is what the keyboard library below is best tested on.
//   - Two accessibility switches (the big round buttons), each on a 3.5mm mono
//     jack, which is the standard AAC connector.
//   - Two panel-mount 3.5mm mono jack sockets, so a switch can be swapped for a
//     different one as her movement changes, without touching the electronics.
//
// Wiring, per switch
//   jack TIP    -> a GPIO pin (25 for write, 26 for undo, below)
//   jack SLEEVE -> GND
//   Nothing else. The ESP32's internal pull-up resistors do the rest, so the
//   pin reads HIGH when the switch is open and LOW when it is pressed.
//
// ---------------------------------------------------------------------------
// Flashing it, first time
//   1. Install the Arduino IDE, and add ESP32 board support:
//      Preferences -> Additional Boards Manager URLs, paste
//      https://raw.githubusercontent.com/espressif/arduino-esp32/gh-pages/package_esp32_index.json
//      then Tools -> Board -> Boards Manager -> install "esp32".
//   2. Install the keyboard library: download the ZIP from
//      https://github.com/T-vK/ESP32-BLE-Keyboard/releases (the file
//      ESP32-BLE-Keyboard.zip), then in the IDE:
//      Sketch -> Include Library -> Add .ZIP Library.
//   3. Tools -> Board -> "ESP32 Dev Module". Plug the board in, pick its port
//      under Tools -> Port, and press Upload.
//   4. On the phone or tablet, Bluetooth settings -> pair "Comunicador". Then
//      open the app's COMPROVA ELS POLSADORS screen and press each switch to
//      confirm it shows SPACE and ENTER.
// ---------------------------------------------------------------------------

#include <BleKeyboard.h>

// Pins with an internal pull-up. Avoid 34–39: those are input-only and have no
// pull-up, so a switch on them would float and fire at random.
const int WRITE_PIN = 25;  // SPACE — choose a letter, enter a row
const int UNDO_PIN  = 26;  // ENTER — undo

// A mechanical switch does not close cleanly; the contacts bounce for a few
// milliseconds. The app debounces too, but doing it here as well keeps a noisy
// switch from ever putting a burst of key events on the air.
const unsigned long DEBOUNCE_MS = 25;

BleKeyboard bleKeyboard("Comunicador", "MerceV2", 100);

struct Switch {
  int pin;
  uint8_t key;
  bool wasClosed;              // debounced state: is it currently pressed?
  unsigned long lastChangeMs;  // when the raw reading last flipped
  bool lastRaw;                // last raw reading, for edge timing
};

Switch switches[] = {
  { WRITE_PIN, ' ',      false, 0, false },
  { UNDO_PIN,  KEY_RETURN, false, 0, false },
};

void setup() {
  for (Switch &s : switches) {
    pinMode(s.pin, INPUT_PULLUP);
    s.lastRaw = (digitalRead(s.pin) == LOW);
  }
  bleKeyboard.begin();
}

void loop() {
  // No point reading switches until the phone is actually connected.
  if (!bleKeyboard.isConnected()) {
    delay(50);
    return;
  }

  unsigned long now = millis();
  for (Switch &s : switches) {
    // LOW means pressed, because the pull-up holds an open switch HIGH.
    bool raw = (digitalRead(s.pin) == LOW);

    if (raw != s.lastRaw) {
      s.lastRaw = raw;
      s.lastChangeMs = now;  // start the settle timer on any change
    }

    // Only trust the reading once it has held steady past the bounce window.
    if (now - s.lastChangeMs >= DEBOUNCE_MS && raw != s.wasClosed) {
      s.wasClosed = raw;
      if (raw) {
        // Send the key only on the press, not the release, so one push is one
        // action. write() taps the key: press then release.
        bleKeyboard.write(s.key);
      }
    }
  }
}
