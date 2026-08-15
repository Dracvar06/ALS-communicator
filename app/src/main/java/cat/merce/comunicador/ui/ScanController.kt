package cat.merce.comunicador.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.merce.comunicador.prediction.Predictor
import cat.merce.comunicador.prediction.WordListPredictor
import cat.merce.comunicador.prediction.Words
import cat.merce.comunicador.scan.ScanLayout
import cat.merce.comunicador.scan.ScanState
import cat.merce.comunicador.scan.Scanner
import cat.merce.comunicador.scan.SelectResult

/**
 * Joins the scan machine to the screen.
 *
 * The [Scanner] deals only in row and column numbers. This class turns a chosen
 * position into a letter or a word, keeps the history that [undo] walks back
 * through, and holds the state the screen draws from.
 */
class ScanController(
    initialIntervalMs: Long = DEFAULT_SCAN_INTERVAL_MS,
) {

    /**
     * Starts as the small built-in list and is replaced by the full Catalan
     * model once it has been read from disk. Swapping rather than waiting means
     * the grid is usable the instant the app opens.
     */
    private var predictor: Predictor = WordListPredictor()

    /** Called when she finishes a word, so it can be remembered. */
    var onWordFinished: ((previous: String, word: String) -> Unit)? = null

    /** Called when undo takes a finished word back, so it can be forgotten. */
    var onWordUndone: ((previous: String, word: String) -> Unit)? = null

    /** Called when the speed changes, so it can be saved. */
    var onIntervalChanged: ((Long) -> Unit)? = null

    val rows: List<List<Key>> get() = CATALAN_KEYBOARD

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

    /** True when the undo button would do something. */
    var canUndo: Boolean by mutableStateOf(false)
        private set

    private val scanner = Scanner(ScanLayout(CATALAN_KEYBOARD.map { it.size }))

    /**
     * What undo walks back through, oldest first.
     *
     * Only two things ever need reversing: opening a row, and changing the
     * text. Everything the grid can do is one of those.
     */
    private sealed interface Step {
        /** She opened [row]. Undoing leaves it again. */
        data class OpenedRow(val row: Int) : Step

        /** She changed the text from [before], while inside [row]. */
        data class ChangedText(val before: String, val row: Int) : Step
    }

    private val history = ArrayDeque<Step>()

    init {
        refreshSuggestions()
    }

    /** The scan interval elapsed. */
    fun tick() {
        // Nothing is scanning while a carer has settings open.
        if (inSettings) return

        val before = state
        scanner.tick()
        state = scanner.state

        if (before is ScanState.Cell && state is ScanState.Row) {
            // The row ran out of passes and let go by itself. She is already
            // back where undo would have put her, so that step is spent.
            if (history.lastOrNull() is Step.OpenedRow) {
                history.removeLast()
                refreshCanUndo()
            }
        }
    }

    /** The writing switch was pressed. */
    fun press() {
        if (inSettings) {
            // Any press gets a carer out of settings. Settings is the one place
            // the grid cannot reach, so it must not need the grid to leave.
            closeSettings()
            return
        }

        val before = state
        when (val result = scanner.select()) {
            SelectResult.EnteredRow -> {
                if (before is ScanState.Row) push(Step.OpenedRow(before.row))
            }

            is SelectResult.SelectedCell -> {
                val row = result.position.row
                val textBefore = text
                apply(rows[row][result.position.col])

                // Opening the row and choosing from it are one action to undo,
                // not two, so the row-opening step is folded into this one.
                if (history.lastOrNull() is Step.OpenedRow) history.removeLast()
                push(Step.ChangedText(textBefore, row))

                noticeFinishedWord(textBefore, text)
                refreshSuggestions()
            }
        }
        state = scanner.state
    }

    /**
     * The undo switch was pressed. Steps back exactly one action.
     *
     * Inside a row, it leaves the row. After a letter, it removes the letter
     * and puts her back inside the row that letter came from, so she can choose
     * again without waiting for the scan to come round. Pressing it repeatedly
     * keeps walking back.
     */
    fun undo() {
        if (inSettings) {
            closeSettings()
            return
        }

        when (val step = history.removeLastOrNull()) {
            null -> Unit // Nothing to undo. Doing nothing is the right answer.

            is Step.OpenedRow -> {
                scanner.goToRow(step.row)
                state = scanner.state
            }

            is Step.ChangedText -> {
                val undoneText = text
                text = step.before
                scanner.goIntoRow(step.row)
                state = scanner.state

                // She is inside the row again, so one more undo should take her
                // out of it, exactly as if she had just opened it.
                push(Step.OpenedRow(step.row))

                // A word taken back is not a word she meant, so it must not
                // stay in what the app has learned about her.
                finishedWord(step.before, undoneText)?.let { (previous, word) ->
                    onWordUndone?.invoke(previous, word)
                }
                refreshSuggestions()
            }
        }
        refreshCanUndo()
    }

    /**
     * Opens settings. Deliberately not reachable by scanning, so she cannot
     * land here by mistake; a carer opens it by touch.
     */
    fun openSettings() {
        inSettings = true
    }

    fun closeSettings() {
        if (!inSettings) return
        inSettings = false
        // Back to the top, so the rhythm after settings is always the same.
        scanner.goToRow(0)
        state = scanner.state
        history.clear()
        refreshCanUndo()
    }

    /** Set by the settings slider, under a carer's finger. */
    fun changeInterval(millis: Long) {
        val clamped = millis.coerceIn(MIN_INTERVAL_MS, MAX_INTERVAL_MS)
        if (clamped == scanIntervalMs) return
        scanIntervalMs = clamped
        onIntervalChanged?.invoke(clamped)
    }

    /** Swaps in the full model once it has finished loading. */
    fun usePredictor(replacement: Predictor) {
        predictor = replacement
        refreshSuggestions()
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
        }
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

    private fun push(step: Step) {
        history.addLast(step)
        // Undoing back to the start of the day is not a thing anyone needs, and
        // the list must not grow without limit.
        while (history.size > MAX_HISTORY) history.removeFirst()
        refreshCanUndo()
    }

    private fun refreshCanUndo() {
        canUndo = history.isNotEmpty()
    }

    private fun noticeFinishedWord(before: String, after: String) {
        finishedWord(before, after)?.let { (previous, word) ->
            onWordFinished?.invoke(previous, word)
        }
    }

    /**
     * The word completed by going from [before] to [after], if any.
     *
     * A word counts as finished the moment a space appears after it, whether
     * she spelled it out or took a suggestion. Deletions finish nothing: a word
     * she removed is the opposite of a word she meant.
     */
    private fun finishedWord(before: String, after: String): Pair<String, String>? {
        if (after.length <= before.length) return null
        if (!after.endsWith(" ") || before.endsWith(" ")) return null

        val written = after.trim().split(' ').filter { it.isNotEmpty() }
        val word = written.lastOrNull() ?: return null
        return written.getOrElse(written.size - 2) { "" } to word
    }

    private fun refreshSuggestions() {
        // A few more than needed, because some get dropped just below.
        val offered = predictor.predict(text, SUGGESTION_SLOTS + ALREADY_ON_GRID.size)
        suggestions = offered
            // Sí and no have their own keys, so spending a suggestion slot on
            // them would waste one of the three most valuable cells.
            .filterNot { Words.fold(it) in ALREADY_ON_GRID }
            // Upper case throughout, to match the letter keys.
            .map { it.uppercase() }
            .take(SUGGESTION_SLOTS)
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

        /** The slider moves in tenths of a second. */
        const val INTERVAL_STEP_MS = 100L

        private const val MAX_HISTORY = 100

        /** Folded words that already have a dedicated key of their own. */
        private val ALREADY_ON_GRID = setOf("si", "no")
    }
}
