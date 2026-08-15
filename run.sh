#!/bin/sh
# Builds the app, starts the virtual tablet if it is not already running,
# installs the app and opens it.
#
#   ./run.sh
#
# Java lives inside Android Studio rather than on the system path, which is why
# JAVA_HOME is set here.
set -e

export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
SDK="$HOME/Library/Android/sdk"
ADB="$SDK/platform-tools/adb"
AVD="merce_tablet"

cd "$(dirname "$0")"

echo "Building..."
./gradlew :app:assembleDebug -q

# Prefer a real device (phone or tablet) plugged in over the emulator.
DEVICE=$("$ADB" devices | awk '$2=="device" && $1!~/emulator/ {print $1; exit}')
if [ -z "$DEVICE" ]; then
    if ! "$ADB" devices | grep -q "emulator.*device$"; then
        echo "No device plugged in; starting the emulator (about half a minute)..."
        "$SDK/emulator/emulator" -avd "$AVD" -no-boot-anim >/dev/null 2>&1 &
        "$ADB" wait-for-device
        while [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
            sleep 2
        done
    fi
    DEVICE=$("$ADB" devices | awk '$2=="device" {print $1; exit}')
fi
echo "Using device: $DEVICE"

echo "Installing..."
"$ADB" -s "$DEVICE" install -r app/build/outputs/apk/debug/app-debug.apk >/dev/null
"$ADB" -s "$DEVICE" shell am start -n cat.merce.comunicador/.ui.MainActivity >/dev/null

echo "Running. Click the tablet window, then press the spacebar."
