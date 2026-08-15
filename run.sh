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

if ! "$ADB" devices | grep -q "emulator.*device$"; then
    echo "Starting the tablet (takes about half a minute)..."
    "$SDK/emulator/emulator" -avd "$AVD" -no-boot-anim >/dev/null 2>&1 &
    "$ADB" wait-for-device
    while [ "$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" != "1" ]; do
        sleep 2
    done
fi

echo "Installing..."
"$ADB" install -r app/build/outputs/apk/debug/app-debug.apk >/dev/null
"$ADB" shell am start -n cat.merce.comunicador/.ui.MainActivity >/dev/null

echo "Running. Click the tablet window, then press the spacebar."
