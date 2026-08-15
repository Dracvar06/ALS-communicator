package cat.merce.comunicador.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

    private val controller = ScanController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode in SWITCH_KEYS) {
            controller.press()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private companion object {
        val SWITCH_KEYS = setOf(
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_DPAD_CENTER,
        )
    }
}
