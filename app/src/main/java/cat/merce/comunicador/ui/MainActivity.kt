package cat.merce.comunicador.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * The only screen.
 *
 * A Bluetooth switch interface presents itself as a keyboard, so the switch
 * arrives here as an ordinary key event. Space is the usual key; the others are
 * accepted so a plain keyboard can stand in during development.
 *
 * There is no debounce yet. A twitchy switch will register twice, and that
 * belongs in the input/ layer, which does not exist.
 */
class MainActivity : ComponentActivity() {

    private lateinit var settings: SharedPreferences
    private lateinit var controller: ScanController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE)
        controller = ScanController(
            initialIntervalMs = settings.getLong(
                KEY_SCAN_INTERVAL,
                ScanController.DEFAULT_SCAN_INTERVAL_MS
            )
        )
        // apply() writes on a background thread, so tuning the speed never
        // blocks the cursor.
        controller.onIntervalChanged = { millis ->
            settings.edit { putLong(KEY_SCAN_INTERVAL, millis) }
        }

        // She cannot wake a sleeping tablet, so it must not sleep.
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        setContent {
            ScanScreen(controller)
        }
    }

    /**
     * The switch is taken here, before the key reaches anything on screen.
     *
     * onKeyDown is too late: a focused view gets first refusal, and Compose
     * treats space on a focused clickable as a click. That let the switch press
     * the settings button. Claiming the key at the window means one switch can
     * only ever do one thing, which is what the brief asks for.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode !in SWITCH_KEYS) return super.dispatchKeyEvent(event)

        // Act on the press, and swallow the matching release so nothing else
        // sees it. repeatCount filters the auto-repeat that arrives when a
        // switch is held down, which would otherwise pour selections in.
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            controller.press()
        }
        return true
    }

    private companion object {
        const val SETTINGS_FILE = "comunicador"
        const val KEY_SCAN_INTERVAL = "scan_interval_ms"

        val SWITCH_KEYS = setOf(
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_DPAD_CENTER,
        )
    }
}
