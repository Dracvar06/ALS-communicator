package cat.merce.comunicador.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import cat.merce.comunicador.input.Switch
import cat.merce.comunicador.input.SwitchFilter
import cat.merce.comunicador.prediction.NgramModel
import cat.merce.comunicador.prediction.NgramPredictor
import cat.merce.comunicador.prediction.PersonalModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * The only activity.
 *
 * A Bluetooth switch interface presents itself as a keyboard, so each switch
 * arrives here as an ordinary key event. Which key each switch sends depends on
 * the box: see [WRITE_KEYS] and [UNDO_KEYS], and the diagnostics screen for
 * finding out what a particular one actually sends.
 *
 * Presses pass through [SwitchFilter] before they count, because a physical
 * switch bounces and a hand that cannot be held still repeats.
 */
class MainActivity : ComponentActivity() {

    private lateinit var settings: SharedPreferences
    private lateinit var controller: ScanController

    /** Null until the shipped Catalan model has finished loading. */
    private var predictor: NgramPredictor? = null

    /** Turns a bouncing physical switch into single presses. */
    private lateinit var switches: SwitchFilter

    /** Speaks phrases aloud. Null until it has finished starting up. */
    private var tts: TextToSpeech? = null

    /** The editable phrase list. A helper can change this file. */
    private val phrasesFile: File by lazy { File(filesDir, PHRASES_FILE) }

    /** A separate copy, so testing switches cannot disturb the real one. */
    private val diagnosticFilter by lazy { SwitchFilter(debounceMs = controller.debounceMs) }

    private var lastDiagnosticKeyAt: Long? = null

    private val personalFile: File by lazy { File(filesDir, PERSONAL_FILE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings = getSharedPreferences(SETTINGS_FILE, MODE_PRIVATE)
        controller = ScanController(
            initialIntervalMs = settings.getLong(
                KEY_SCAN_INTERVAL,
                ScanController.DEFAULT_SCAN_INTERVAL_MS
            ),
            initialDebounceMs = settings.getLong(
                KEY_DEBOUNCE,
                SwitchFilter.DEFAULT_DEBOUNCE_MS
            ),
            initialTouchInput = settings.getBoolean(KEY_TOUCH_INPUT, true),
            initialFirstCellExtraMs = settings.getLong(
                KEY_FIRST_CELL_EXTRA,
                ScanController.DEFAULT_FIRST_CELL_EXTRA_MS
            ),
        )
        controller.onTouchInputChanged = { on ->
            settings.edit { putBoolean(KEY_TOUCH_INPUT, on) }
        }
        controller.onFirstCellExtraChanged = { millis ->
            settings.edit { putLong(KEY_FIRST_CELL_EXTRA, millis) }
        }

        controller.setPhrases(loadPhrases())
        controller.onSpeak = { text ->
            // Fails quietly if no voice is installed: the phrase still selects,
            // it just does not speak. Flush, so a new phrase interrupts the last.
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "phrase")
        }
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.SUCCESS) return@TextToSpeech
            val catalan = tts?.setLanguage(Locale("ca"))
            if (catalan == TextToSpeech.LANG_MISSING_DATA ||
                catalan == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                // Spanish is closer to Catalan than the engine default, if the
                // Catalan voice is not installed on this device.
                tts?.setLanguage(Locale("es"))
            }
        }
        switches = SwitchFilter(debounceMs = controller.debounceMs)
        controller.onDebounceChanged = { millis ->
            settings.edit { putLong(KEY_DEBOUNCE, millis) }
            // Rebuilt rather than mutated, so the filter itself stays a plain
            // value with no settings to keep in step.
            switches = SwitchFilter(debounceMs = millis)
        }
        // apply() writes on a background thread, so tuning the speed never
        // blocks the cursor.
        controller.onIntervalChanged = { millis ->
            settings.edit { putLong(KEY_SCAN_INTERVAL, millis) }
        }
        controller.onWordFinished = ::rememberWord
        controller.onWordUndone = ::forgetWord

        loadPrediction()

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
     * Reads the model and her own history off the main thread, then swaps them
     * in. The grid works from the first frame with the small built-in list; a
     * megabyte of Catalan is not worth a blank screen.
     */
    private fun loadPrediction() {
        lifecycleScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                runCatching {
                    val model = assets.open(MODEL_ASSET).bufferedReader()
                        .use { NgramModel.load(it) }
                    val personal = if (personalFile.exists()) {
                        PersonalModel.fromLines(personalFile.readLines())
                    } else {
                        PersonalModel()
                    }
                    NgramPredictor(model, personal)
                }.getOrNull()
            }
            // A missing or damaged asset leaves the built-in list in place.
            // Worse suggestions are survivable; a crash on startup is not.
            if (loaded != null) {
                predictor = loaded
                controller.usePredictor(loaded)
            }
        }
    }

    /**
     * Reads the phrases from the editable file, seeding it with the defaults the
     * first time so a helper has something to edit rather than a blank file.
     */
    private fun loadPhrases(): List<String> {
        if (!phrasesFile.exists()) {
            runCatching { phrasesFile.writeText(DEFAULT_PHRASES.joinToString("\n")) }
            return DEFAULT_PHRASES
        }
        val lines = runCatching { phrasesFile.readLines() }.getOrNull()
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
        return if (lines.isNullOrEmpty()) DEFAULT_PHRASES else lines
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }

    private fun rememberWord(previous: String, word: String) {
        val learning = predictor?.personal ?: return
        learning.learn(previous, word)
        savePersonal(learning)
    }

    /** Undo took the word back, so it was never something she meant to say. */
    private fun forgetWord(previous: String, word: String) {
        val learning = predictor?.personal ?: return
        learning.unlearn(previous, word)
        savePersonal(learning)
    }

    private fun savePersonal(learning: PersonalModel) {
        // Written out on every word rather than at shutdown, because the app
        // being killed is exactly the case where the history must survive.
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching { personalFile.writeText(learning.toLines().joinToString("\n")) }
        }
    }

    /**
     * Both switches are taken here, before the key reaches anything on screen.
     *
     * onKeyDown is too late: a focused view gets first refusal, and Compose
     * treats space on a focused clickable as a click. That let a switch press
     * the settings button. Claiming the keys at the window means each switch
     * can only ever do the one thing it is for.
     */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val write = event.keyCode in WRITE_KEYS
        val undo = event.keyCode in UNDO_KEYS

        // On the diagnostics screen every key is swallowed and reported,
        // including ones the app does not use. Finding out what an unknown
        // switch box sends is the entire point of that screen.
        if (controller.inDiagnostics) {
            if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                reportKey(event, write, undo)
            }
            return true
        }

        if (!write && !undo) return super.dispatchKeyEvent(event)

        // Act on the press, and swallow the matching release so nothing else
        // sees it. repeatCount filters the auto-repeat that arrives when a
        // switch is held down, which would otherwise pour selections in.
        if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            val which = if (write) Switch.Write else Switch.Undo
            // A physical switch bounces; this is what turns that into one press.
            if (switches.accept(which, SystemClock.elapsedRealtime())) {
                if (write) controller.press() else controller.undo()
            }
        }
        return true
    }

    private fun reportKey(event: KeyEvent, write: Boolean, undo: Boolean) {
        val now = SystemClock.elapsedRealtime()
        val gap = lastDiagnosticKeyAt?.let { now - it }
        lastDiagnosticKeyAt = now

        val which = when {
            write -> Switch.Write
            undo -> Switch.Undo
            else -> null
        }
        controller.reportKey(
            KeyReport(
                keyCode = event.keyCode,
                name = KeyEvent.keyCodeToString(event.keyCode).removePrefix("KEYCODE_"),
                role = when (which) {
                    Switch.Write -> "escriu"
                    Switch.Undo -> "desfà"
                    null -> "sense assignar"
                },
                sinceLastMs = gap,
                // Shown so a carer can see the debounce doing its job rather
                // than wonder why a press did nothing.
                accepted = which != null && diagnosticFilter.accept(which, now),
            )
        )
    }

    private companion object {
        const val SETTINGS_FILE = "comunicador"
        const val KEY_SCAN_INTERVAL = "scan_interval_ms"
        const val KEY_DEBOUNCE = "debounce_ms"
        const val KEY_TOUCH_INPUT = "touch_input"
        const val KEY_FIRST_CELL_EXTRA = "first_cell_extra_ms"
        const val PHRASES_FILE = "phrases.txt"
        const val MODEL_ASSET = "ca-model.txt"

        /** Her own writing. Stays in the app's private storage, never leaves. */
        const val PERSONAL_FILE = "personal-model.txt"

        /**
         * A two switch interface sends a different key for each switch. Which
         * key each one sends depends on the box, and most can be reconfigured,
         * so these two lists are where to look when the hardware arrives.
         *
         * On a plain keyboard: space writes, enter undoes.
         */
        val WRITE_KEYS = setOf(
            KeyEvent.KEYCODE_SPACE,
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_1,
        )

        val UNDO_KEYS = setOf(
            KeyEvent.KEYCODE_ENTER,
            KeyEvent.KEYCODE_NUMPAD_ENTER,
            KeyEvent.KEYCODE_2,
        )
    }
}
