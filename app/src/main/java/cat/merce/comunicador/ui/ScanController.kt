package cat.merce.comunicador.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.merce.comunicador.prediction.Predictor
import cat.merce.comunicador.prediction.WordListPredictor
import cat.merce.comunicador.scan.ScanLayout
import cat.merce.comunicador.scan.ScanState
import cat.merce.comunicador.scan.Scanner
import cat.merce.comunicador.scan.SelectResult

/**
 * Joins the scan machine to the screen.
 *
 * The [Scanner] deals only in row and column numbers. This class turns a chosen
 * position into a letter, a word or a setting, and holds the state the screen
 * draws from.
 */
class ScanController(
    private val predictor: Predictor = WordListPredictor(),
    initialIntervalMs: Long = DEFAULT_SCAN_INTERVAL_MS,
) {

    /** The grid on screen. Swaps when settings open. */
    var rows: List<List<Key>> by mutableStateOf(CATALAN_KEYBOARD)
        private set

    /** Where the highlight is. */
    var state: ScanState by mutableStateOf(ScanState.Row(0))
        private set

    /** What she has written so far. */
    var text: String by mutableStateOf("")
        private set

    /** The words currently offered in the suggestion slots. */
    var suggestions: List<String> by mutableStateOf(emptyList())
        private set

    /** How long each step of the cursor lasts. */
    var scanIntervalMs: Long by mutableStateOf(initialIntervalMs)
        private set

    var inSettings: Boolean by mutableStateOf(false)
        private set

    /** Called when the speed changes, so it can be saved. */
    var onIntervalChanged: ((Long) -> Unit)? = null

    private var scanner = Scanner(layoutOf(CATALAN_KEYBOARD))

    init {
        refreshSuggestions()
    }

    /** The scan interval elapsed. */
    fun tick() {
        scanner.tick()
        state = scanner.state
    }

    /** The switch was pressed. */
    fun press() {
        when (val result = scanner.select()) {
            // Opening a row does nothing on its own.
            SelectResult.EnteredRow -> Unit
            is SelectResult.SelectedCell -> {
                val position = result.position
                apply(rows[position.row][position.col])
            }
        }
        // Read back afterwards: choosing a settings key can replace the
        // scanner entirely.
        state = scanner.state
    }

    /**
     * Opens settings. Deliberately not reachable by scanning, so she cannot
     * land here by mistake; a carer opens it by touch.
     */
    fun openSettings() {
        if (inSettings) return
        inSettings = true
        switchTo(SETTINGS_KEYBOARD)
    }

    /** The word shown on a cell right now. */
    fun label(key: Key): String = when (key) {
        is Key.Suggestion -> suggestions.getOrElse(key.slot) { "" }
        else -> key.label
    }

    /** Should the cell at this position be highlighted right now? */
    fun isLit(row: Int, col: Int): Boolean = when (val current = state) {
        // A whole row lights up before it is entered.
        is ScanState.Row -> current.row == row
        is ScanState.Cell -> current.row == row && current.col == col
    }

    private fun apply(key: Key) {
        when (key) {
            is Key.Letter -> text += key.char
            is Key.Suggestion -> applySuggestion(key.slot)
            Key.Yes -> text += "SÍ "
            Key.No -> text += "NO "
            Key.Space -> text += " "
            Key.Delete -> text = text.dropLast(1)
            Key.Clear -> text = ""

            Key.Slower -> setInterval(scanIntervalMs + INTERVAL_STEP_MS)
            Key.Faster -> setInterval(scanIntervalMs - INTERVAL_STEP_MS)
            Key.CloseSettings -> {
                inSettings = false
                switchTo(CATALAN_KEYBOARD)
            }
        }
        refreshSuggestions()
    }

    /**
     * Replaces the part word she is typing with the whole suggested word, and
     * adds the space after it, since the next thing is always a new word.
     */
    private fun applySuggestion(slot: Int) {
        val word = suggestions.getOrNull(slot)
        // An empty slot is a wasted press rather than a crash. Slots keep their
        // place even when empty so the grid never changes shape under her.
        if (word.isNullOrEmpty()) return

        val finished = text.substringBeforeLast(' ', missingDelimiterValue = "")
        text = if (finished.isEmpty()) "$word " else "$finished $word "
    }

    private fun refreshSuggestions() {
        // Upper case throughout, to match the letter keys.
        suggestions = predictor.predict(text, SUGGESTION_SLOTS).map { it.uppercase() }
    }

    private fun setInterval(millis: Long) {
        val clamped = millis.coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
        if (clamped == scanIntervalMs) return
        scanIntervalMs = clamped
        onIntervalChanged?.invoke(clamped)
    }

    /** Starts a fresh scan over a different grid, from the top. */
    private fun switchTo(keyboard: List<List<Key>>) {
        rows = keyboard
        scanner = Scanner(layoutOf(keyboard))
        state = scanner.state
    }

    companion object {
        /**
         * How long each step lasts, in milliseconds. Adjustable in settings and
         * saved between runs, so this is only the value on a fresh install.
         */
        const val DEFAULT_SCAN_INTERVAL_MS = 1000L

        /** Below this the cursor is faster than most people can react to. */
        const val MIN_INTERVAL_MS = 300L

        /** Above this a short sentence takes minutes. */
        const val MAX_INTERVAL_MS = 5000L

        const val INTERVAL_STEP_MS = 100L

        private fun layoutOf(keyboard: List<List<Key>>) =
            ScanLayout(keyboard.map { it.size })
    }
}
