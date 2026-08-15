package cat.merce.comunicador.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
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

    /**
     * The inputs bound to each action, as tokens. A token is either a keyboard
     * or button key ("k:96") or an analog axis direction ("a:17:+"), because a
     * controller's triggers and d-pad arrive as axes, not keys. Editable from
     * settings, several allowed per action.
     */
    private var writeTokens: Set<String> = DEFAULT_WRITE_TOKENS
    private var undoTokens: Set<String> = DEFAULT_UNDO_TOKENS

    /** Whether each axis direction is currently pressed, for edge detection. */
    private val axisDown = HashMap<String, Boolean>()

    private fun buildFilter() = SwitchFilter(
        debounceMs = controller.debounceMs,
        restartOnReject = controller.antiTremor,
    )

    private fun pushButtonLabels() {
        controller.setButtonLabels(
            write = writeTokens.map { labelForToken(it) },
            undo = undoTokens.map { labelForToken(it) },
        )
    }

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
            initialAntiTremor = settings.getBoolean(KEY_ANTI_TREMOR, false),
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
        // What drives each action: the helper's bound buttons if they have set
        // any, otherwise the built-in defaults so a keyboard or game controller
        // works out of the box.
        writeTokens = settings.getStringSet(KEY_WRITE_TOKENS, null)?.takeIf { it.isNotEmpty() }
            ?: DEFAULT_WRITE_TOKENS
        undoTokens = settings.getStringSet(KEY_UNDO_TOKENS, null)?.takeIf { it.isNotEmpty() }
            ?: DEFAULT_UNDO_TOKENS
        pushButtonLabels()

        switches = buildFilter()
        controller.onDebounceChanged = { millis ->
            settings.edit { putLong(KEY_DEBOUNCE, millis) }
            // Rebuilt rather than mutated, so the filter itself stays a plain
            // value with no settings to keep in step.
            switches = buildFilter()
        }
        controller.onAntiTremorChanged = { on ->
            settings.edit { putBoolean(KEY_ANTI_TREMOR, on) }
            switches = buildFilter()
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

    /**
     * Saves a captured input as one of this action's buttons. The first capture
     * of a binding session replaces the set, so re-binding starts clean; the
     * rest add to it, so several buttons can drive the same action.
     */
    private fun captureBinding(role: SwitchRole, token: String, name: String) {
        val first = controller.boundThisSession.isEmpty()
        when (role) {
            SwitchRole.Write -> {
                writeTokens = if (first) setOf(token) else writeTokens + token
                settings.edit { putStringSet(KEY_WRITE_TOKENS, writeTokens) }
            }
            SwitchRole.Undo -> {
                undoTokens = if (first) setOf(token) else undoTokens + token
                settings.edit { putStringSet(KEY_UNDO_TOKENS, undoTokens) }
            }
        }
        pushButtonLabels()
        controller.addedBinding(role, name)
    }

    /**
     * One input happened: a key press or an axis crossing into its pressed
     * range. Route it — capture it if binding, show it if on diagnostics, or
     * act on it if it is one of the bound inputs.
     */
    private fun handlePress(token: String, name: String) {
        val role = controller.bindingRole
        if (role != null) {
            captureBinding(role, token, name)
            return
        }

        val write = token in writeTokens
        val undo = token in undoTokens

        if (controller.inDiagnostics) {
            report(token, name, write, undo)
            return
        }
        if (!write && !undo) return

        val which = if (write) Switch.Write else Switch.Undo
        if (switches.accept(which, SystemClock.elapsedRealtime())) {
            if (write) controller.press() else controller.undo()
        }
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
        val down = event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0
        val token = eventToken(event)

        // While binding or on diagnostics, every key is swallowed: one to be
        // captured, the other to be shown. repeatCount filters the auto-repeat
        // of a held button, which would otherwise pour presses in.
        if (controller.bindingRole != null || controller.inDiagnostics) {
            if (down) handlePress(token, eventName(event))
            return true
        }

        // Otherwise only take the keys that are actually bound; let the rest
        // through so the system still works.
        if (token !in writeTokens && token !in undoTokens) {
            return super.dispatchKeyEvent(event)
        }
        if (down) handlePress(token, eventName(event))
        return true
    }

    /**
     * How a key event is identified.
     *
     * Normally the key code, but a controller can send buttons Android has no
     * name for: the Stadia pad's triggers arrive as KEYCODE_UNKNOWN, code 0,
     * so every one of them looked identical and none could be bound. The scan
     * code is the raw code from the device and stays distinct per button, so it
     * identifies exactly the buttons the key code cannot.
     */
    private fun eventToken(event: KeyEvent): String =
        if (event.keyCode != KeyEvent.KEYCODE_UNKNOWN || event.scanCode == 0) {
            keyToken(event.keyCode)
        } else {
            scanToken(event.scanCode)
        }

    private fun eventName(event: KeyEvent): String =
        if (event.keyCode != KeyEvent.KEYCODE_UNKNOWN || event.scanCode == 0) {
            keyName(event.keyCode)
        } else {
            scanName(event.scanCode)
        }

    /**
     * A controller's triggers and d-pad arrive here as analog axes, not keys.
     * Each axis direction is watched crossing into its pressed range, and that
     * crossing is treated as one press.
     */
    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        val fromGamepad = event.source and InputDevice.SOURCE_JOYSTICK ==
            InputDevice.SOURCE_JOYSTICK
        if (!fromGamepad || event.action != MotionEvent.ACTION_MOVE) {
            return super.dispatchGenericMotionEvent(event)
        }

        for (channel in AXIS_CHANNELS) {
            val value = event.getAxisValue(channel.axis)
            val wasDown = axisDown[channel.token] ?: false
            // Hysteresis, so a trigger resting near the threshold does not
            // flutter between pressed and not.
            val nowDown = if (wasDown) {
                channel.signed(value) > AXIS_RELEASE
            } else {
                channel.signed(value) > AXIS_PRESS
            }
            if (nowDown && !wasDown &&
                (controller.bindingRole != null || controller.inDiagnostics ||
                    channel.token in writeTokens || channel.token in undoTokens)
            ) {
                handlePress(channel.token, channel.label)
            }
            axisDown[channel.token] = nowDown
        }

        // Consume while binding or testing, so a stray stick move does nothing
        // there; otherwise let the system have the event.
        return if (controller.bindingRole != null || controller.inDiagnostics) {
            true
        } else {
            super.dispatchGenericMotionEvent(event)
        }
    }

    private fun report(token: String, name: String, write: Boolean, undo: Boolean) {
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
                keyCode = tokenCode(token),
                name = name,
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
        const val KEY_ANTI_TREMOR = "anti_tremor"
        const val KEY_WRITE_TOKENS = "write_tokens"
        const val KEY_UNDO_TOKENS = "undo_tokens"
        const val PHRASES_FILE = "phrases.txt"

        /** A key press is not treated as a press until an axis exceeds this... */
        const val AXIS_PRESS = 0.5f

        /** ...and is not treated as released until it falls back below this. */
        const val AXIS_RELEASE = 0.3f
        const val MODEL_ASSET = "ca-model.txt"

        /** Her own writing. Stays in the app's private storage, never leaves. */
        const val PERSONAL_FILE = "personal-model.txt"

        /**
         * A two switch interface sends a different key for each switch. Which
         * key each one sends depends on the box, and most can be reconfigured,
         * so these two lists are where to look when the hardware arrives. The
         * COMPROVA ELS POLSADORS screen shows what any given device sends.
         *
         * Several devices are covered at once, so a test rig works out of the box:
         *  - a plain keyboard: space writes, enter undoes;
         *  - a game controller (e.g. a Stadia pad over Bluetooth): A and the
         *    right bumper / d-pad-right write, B and the left bumper /
         *    d-pad-left undo, following Android's confirm/back convention;
         *  - a generic switch box: the number keys 1 and 2.
         */
        // Sensible defaults so a keyboard or controller works before a helper
        // binds anything: space/enter, 1/2, and the controller's A/B and
        // bumpers. Triggers and d-pad are left out of the defaults because they
        // arrive as axes and vary by controller; a helper binds those.
        val DEFAULT_WRITE_TOKENS = setOf(
            keyToken(KeyEvent.KEYCODE_SPACE),
            keyToken(KeyEvent.KEYCODE_1),
            keyToken(KeyEvent.KEYCODE_BUTTON_A),
            keyToken(KeyEvent.KEYCODE_BUTTON_R1),
        )

        val DEFAULT_UNDO_TOKENS = setOf(
            keyToken(KeyEvent.KEYCODE_ENTER),
            keyToken(KeyEvent.KEYCODE_NUMPAD_ENTER),
            keyToken(KeyEvent.KEYCODE_2),
            keyToken(KeyEvent.KEYCODE_BUTTON_B),
            keyToken(KeyEvent.KEYCODE_BUTTON_L1),
        )

        /**
         * The analog axis directions worth watching: the two triggers (which
         * different controllers report on different axes, so several are
         * listed), and the four d-pad directions, which are a hat axis rather
         * than keys on most controllers.
         */
        val AXIS_CHANNELS = listOf(
            AxisChannel(MotionEvent.AXIS_LTRIGGER, true, "L2"),
            AxisChannel(MotionEvent.AXIS_BRAKE, true, "L2"),
            AxisChannel(MotionEvent.AXIS_RTRIGGER, true, "R2"),
            AxisChannel(MotionEvent.AXIS_GAS, true, "R2"),
            AxisChannel(MotionEvent.AXIS_HAT_X, true, "CREUETA DRETA"),
            AxisChannel(MotionEvent.AXIS_HAT_X, false, "CREUETA ESQUERRA"),
            AxisChannel(MotionEvent.AXIS_HAT_Y, false, "CREUETA AMUNT"),
            AxisChannel(MotionEvent.AXIS_HAT_Y, true, "CREUETA AVALL"),
        )

        fun keyToken(code: Int) = "k:$code"

        fun keyName(code: Int) =
            KeyEvent.keyCodeToString(code).removePrefix("KEYCODE_")

        fun scanToken(code: Int) = "s:$code"

        /**
         * Linux input codes for the gamepad buttons Android often leaves
         * unnamed, so the helper sees "R2" rather than a bare number. Anything
         * not listed still works; it is just shown by its number.
         */
        val SCAN_NAMES = mapOf(
            304 to "A", 305 to "B", 307 to "X", 308 to "Y",
            310 to "L1", 311 to "R1",
            312 to "L2", 313 to "R2",
            314 to "SELECT", 315 to "START", 316 to "LOGO",
            317 to "STICK ESQ.", 318 to "STICK DRET",
        )

        fun scanName(code: Int) = SCAN_NAMES[code] ?: "BOTÓ $code"

        /** A readable name for a bound token, for showing the helper. */
        fun labelForToken(token: String): String = when {
            token.startsWith("k:") -> keyName(token.removePrefix("k:").toIntOrNull() ?: 0)
            token.startsWith("s:") -> scanName(token.removePrefix("s:").toIntOrNull() ?: 0)
            else -> AXIS_CHANNELS.firstOrNull { it.token == token }?.label ?: "EIX"
        }

        /** A number to show beside a reported input; the axis, key or scan code. */
        fun tokenCode(token: String): Int =
            token.substringAfter(':').substringBefore(':').toIntOrNull() ?: 0
    }

    /** One direction of one analog axis, treated as a button. */
    class AxisChannel(val axis: Int, val positive: Boolean, val label: String) {
        val token = "a:$axis:${if (positive) "+" else "-"}"

        /** Positive so that "pressed" is always "above the threshold". */
        fun signed(value: Float) = if (positive) value else -value
    }
}
