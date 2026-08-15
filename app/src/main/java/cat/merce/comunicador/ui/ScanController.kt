package cat.merce.comunicador.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.merce.comunicador.prediction.Predictor
import cat.merce.comunicador.prediction.WordListPredictor
import cat.merce.comunicador.prediction.Words
import cat.merce.comunicador.input.SwitchFilter
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
/** One key event as the diagnostics screen shows it. */
data class KeyReport(
    val keyCode: Int,
    /** Android's own name for the key, such as SPACE or ENTER. */
    val name: String,
    /** What the app currently does with it, in Catalan, for the carer to read. */
    val role: String,
    /** Milliseconds since the previous key, to make bounce visible. */
    val sinceLastMs: Long?,
    /** False when the debounce filter threw it away. */
    val accepted: Boolean,
)

/** Which of the two actions a physical button is being bound to. */
enum class SwitchRole { Write, Undo }

class ScanController(
    initialIntervalMs: Long = DEFAULT_SCAN_INTERVAL_MS,
    initialDebounceMs: Long = SwitchFilter.DEFAULT_DEBOUNCE_MS,
    initialTouchInput: Boolean = true,
    initialFirstCellExtraMs: Long = DEFAULT_FIRST_CELL_EXTRA_MS,
    initialAntiTremor: Boolean = false,
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

    /** The grid currently on screen: the phrases when in that screen, else writing. */
    val rows: List<List<Key>> get() = if (inPhrases) phrasesGrid else CATALAN_KEYBOARD

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

    /**
     * Extra time given to the very first letter of a row, on top of the normal
     * step. Pressing to enter a row and then reacting to the first letter are
     * two things in quick succession; this gives her a moment to catch up
     * before the cursor moves on. Only the first letter after entering gets it.
     */
    var firstCellExtraMs: Long by mutableStateOf(initialFirstCellExtraMs)
        private set

    /** Called when the first-letter extra time changes, so it can be saved. */
    var onFirstCellExtraChanged: ((Long) -> Unit)? = null

    /**
     * Bumped whenever a press changes where the cursor is, so the timing loop
     * on screen restarts its wait from now. Without this, pressing just before
     * a scheduled step would give the first letter almost no time at all.
     */
    var timingEpoch: Int by mutableStateOf(0)
        private set

    /**
     * True only while the cursor sits on the first letter of a freshly entered
     * row, before any step has moved it on. This is the one letter that gets
     * the extra time.
     */
    private var justEnteredRow: Boolean = false

    var inSettings: Boolean by mutableStateOf(false)
        private set

    /** The carer's screen for finding out what a switch interface sends. */
    var inDiagnostics: Boolean by mutableStateOf(false)
        private set

    /** Newest first. Only filled while [inDiagnostics]. */
    var recentKeys: List<KeyReport> by mutableStateOf(emptyList())
        private set

    /**
     * Bumped whenever a carer touches the diagnostics screen, so the idle
     * timeout can start again. She has no way to leave that screen herself, so
     * it must let itself go when nobody is holding the tablet.
     */
    var diagnosticsTouch: Int by mutableStateOf(0)
        private set

    /** How close together two presses of one switch may be. */
    var debounceMs: Long by mutableStateOf(initialDebounceMs)
        private set

    /** Called when the debounce changes, so it can be saved. */
    var onDebounceChanged: ((Long) -> Unit)? = null

    /**
     * When on, tapping the screen stands in for the two switches: the right
     * half writes, the left half undoes. For trying the app on a phone with no
     * switch box plugged in. A carer turns it off once real switches arrive, so
     * a stray touch cannot type.
     */
    var touchInput: Boolean by mutableStateOf(initialTouchInput)
        private set

    /** Called when touch input is turned on or off, so it can be saved. */
    var onTouchInputChanged: ((Boolean) -> Unit)? = null

    /**
     * When on, every press restarts the minimum-time window, so a tremor's
     * burst of taps counts once. Read by the input layer, which builds the
     * filter from it.
     */
    var antiTremor: Boolean by mutableStateOf(initialAntiTremor)
        private set

    /** Called when the anti-tremor setting changes, so it can be saved. */
    var onAntiTremorChanged: ((Boolean) -> Unit)? = null

    /**
     * Non-null while waiting for a helper to press the button they want bound to
     * this action. The input layer captures the next key and calls
     * [completeBinding]. Null the rest of the time.
     */
    var bindingRole: SwitchRole? by mutableStateOf(null)
        private set

    /** The name of the last button bound, shown as a brief confirmation. */
    var lastBoundLabel: String? by mutableStateOf(null)
        private set

    /** Called with a phrase to say out loud. */
    var onSpeak: ((String) -> Unit)? = null

    /** True while the phrases screen is showing instead of the writing grid. */
    var inPhrases: Boolean by mutableStateOf(false)
        private set

    /** The saved phrases, editable by a helper. */
    private var phrases: List<String> = DEFAULT_PHRASES
    private var phrasesGrid: List<List<Key>> = phrasesKeyboard(phrases)

    /** True when the undo button would do something. */
    var canUndo: Boolean by mutableStateOf(false)
        private set

    private var scanner = Scanner(ScanLayout(CATALAN_KEYBOARD.map { it.size }))

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
        // The cursor has moved off the first letter, so it is no longer special.
        justEnteredRow = false

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
                // She has just landed on the first letter of the row.
                justEnteredRow = true
            }

            is SelectResult.SelectedCell -> {
                val row = result.position.row
                when (val key = rows[row][result.position.col]) {
                    // The phrases screen and back out of it rebuild the scanner,
                    // so they are handled apart from anything that changes text.
                    Key.OpenPhrases -> { openPhrases(); return }
                    Key.Back -> { closePhrases(); return }
                    is Key.Phrase -> onSpeak?.invoke(key.text)

                    else -> {
                        val textBefore = text
                        apply(key)

                        // Opening the row and choosing from it are one action to
                        // undo, not two, so the row-opening step folds into this.
                        if (history.lastOrNull() is Step.OpenedRow) history.removeLast()
                        push(Step.ChangedText(textBefore, row))

                        justEnteredRow = false
                        noticeFinishedWord(textBefore, text)
                        refreshSuggestions()
                    }
                }
            }
        }
        state = scanner.state
        // Start the wait afresh, so the letter she lands on gets its full time.
        restartTiming()
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
        if (inPhrases) {
            // On the phrases screen the undo switch is the way back to writing,
            // a guaranteed physical exit even if the TORNA cell is missed.
            closePhrases()
            return
        }

        when (val step = history.removeLastOrNull()) {
            null -> {
                // Nothing left in the history, so fall back to a plain backspace.
                // This is what replaces the delete key the writing grid used to
                // have: the undo switch removes a letter when there is no larger
                // action to take back.
                if (text.isNotEmpty()) {
                    text = text.dropLast(1)
                    refreshSuggestions()
                }
            }

            is Step.OpenedRow -> {
                scanner.goToRow(step.row)
                state = scanner.state
                justEnteredRow = false
                restartTiming()
            }

            is Step.ChangedText -> {
                val undoneText = text
                text = step.before
                scanner.goIntoRow(step.row)
                state = scanner.state

                // She is back on the first letter of the row, so it earns the
                // extra time again just as if she had entered it herself.
                justEnteredRow = true
                restartTiming()

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

    /** Replaces the saved phrases, e.g. after a helper edits them. */
    fun setPhrases(newPhrases: List<String>) {
        phrases = newPhrases
        phrasesGrid = phrasesKeyboard(newPhrases)
        if (inPhrases) rescan(phrasesGrid)
    }

    /** Switches to the phrases screen, which scans like the writing grid. */
    fun openPhrases() {
        if (inPhrases) return
        inPhrases = true
        history.clear()
        rescan(phrasesGrid)
        justEnteredRow = false
        restartTiming()
        refreshCanUndo()
    }

    /** Returns from the phrases screen to writing. */
    fun closePhrases() {
        if (!inPhrases) return
        inPhrases = false
        rescan(CATALAN_KEYBOARD)
        justEnteredRow = false
        restartTiming()
        // Leave no phrase-screen steps behind to undo once back at writing.
        history.clear()
        refreshCanUndo()
    }

    /** Starts a fresh scan over a new layout, from the top row. */
    private fun rescan(layout: List<List<Key>>) {
        scanner = Scanner(ScanLayout(layout.map { it.size }))
        state = scanner.state
    }

    /**
     * Opens settings. Deliberately not reachable by scanning, so she cannot
     * land here by mistake; a carer opens it by touch.
     */
    fun openSettings() {
        inSettings = true
    }

    /** Opened from settings, by touch. Every key press is captured and shown. */
    fun openDiagnostics() {
        recentKeys = emptyList()
        inDiagnostics = true
        noteDiagnosticsTouch()
    }

    fun noteDiagnosticsTouch() {
        diagnosticsTouch++
    }

    fun reportKey(report: KeyReport) {
        if (!inDiagnostics) return
        recentKeys = (listOf(report) + recentKeys).take(MAX_REPORTED_KEYS)
    }

    fun useTouchInput(enabled: Boolean) {
        if (enabled == touchInput) return
        touchInput = enabled
        onTouchInputChanged?.invoke(enabled)
    }

    fun useAntiTremor(enabled: Boolean) {
        if (enabled == antiTremor) return
        antiTremor = enabled
        onAntiTremorChanged?.invoke(enabled)
    }

    /** A helper asked to bind the button for [role]; wait for a press. */
    fun startBinding(role: SwitchRole) {
        lastBoundLabel = null
        bindingRole = role
    }

    fun cancelBinding() {
        bindingRole = null
    }

    /** The input layer captured a button and saved it; show what was bound. */
    fun completeBinding(label: String) {
        lastBoundLabel = label
        bindingRole = null
    }

    fun changeDebounce(millis: Long) {
        val clamped = millis.coerceIn(0L, SwitchFilter.MAX_DEBOUNCE_MS)
        if (clamped == debounceMs) return
        debounceMs = clamped
        onDebounceChanged?.invoke(clamped)
    }

    fun closeSettings() {
        inDiagnostics = false
        if (!inSettings) return
        inSettings = false
        // Back to the top, so the rhythm after settings is always the same.
        scanner.goToRow(0)
        state = scanner.state
        justEnteredRow = false
        restartTiming()
        history.clear()
        refreshCanUndo()
    }

    /**
     * How long the cursor should sit on the current step before moving on. The
     * timing loop on screen reads this each step. The first letter of a row
     * lasts longer; everything else lasts one normal interval.
     */
    fun currentStepDurationMs(): Long =
        if (justEnteredRow && state is ScanState.Cell) {
            scanIntervalMs + firstCellExtraMs
        } else {
            scanIntervalMs
        }

    private fun restartTiming() {
        timingEpoch++
    }

    /** Set by the settings slider, under a carer's finger. */
    fun changeFirstCellExtra(millis: Long) {
        val clamped = millis.coerceIn(0L, MAX_FIRST_CELL_EXTRA_MS)
        if (clamped == firstCellExtraMs) return
        firstCellExtraMs = clamped
        onFirstCellExtraChanged?.invoke(clamped)
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

            // Handled in press(), never routed through here.
            Key.OpenPhrases, Key.Back, is Key.Phrase -> Unit
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

        /** Extra time on the first letter of a row, on a fresh install. */
        const val DEFAULT_FIRST_CELL_EXTRA_MS = 500L

        /** Beyond this the first letter feels stuck rather than generous. */
        const val MAX_FIRST_CELL_EXTRA_MS = 3000L

        private const val MAX_HISTORY = 100

        /** Enough lines to see a bounce burst, few enough to read at a glance. */
        private const val MAX_REPORTED_KEYS = 10

        /**
         * Diagnostics closes itself after this long without a carer touching
         * it. Key presses deliberately do not count: she would be pressing
         * switches, and that is exactly when she is stranded.
         */
        const val DIAGNOSTICS_IDLE_MS = 90_000L

        /** Folded words that already have a dedicated key of their own. */
        private val ALREADY_ON_GRID = setOf("si", "no")
    }
}
