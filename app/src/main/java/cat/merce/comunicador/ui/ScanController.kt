package cat.merce.comunicador.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.merce.comunicador.prediction.NgramPredictor
import cat.merce.comunicador.prediction.Predictor
import cat.merce.comunicador.prediction.WordListPredictor
import cat.merce.comunicador.prediction.Words
import cat.merce.comunicador.input.SwitchFilter
import cat.merce.comunicador.scan.Cursor
import cat.merce.comunicador.scan.Position
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
    initialLanguage: Language = CATALAN,
    initialLocked: Boolean = false,
    initialOpenOnBoot: Boolean = false,
    initialInputMode: InputMode = InputMode.Scan,
    initialArrowPlacement: ArrowPlacement = ArrowPlacement.Right,
    initialChooseFirst: Boolean = true,
    initialEraseKeys: Boolean = true,
    initialArrowShape: ArrowShape = ArrowShape.Separate,
    initialForgiveMistakes: Boolean = true,
    initialBoldWriting: Boolean = true,
) {

    /**
     * Scanning, or steering with arrows. The two are different enough that the
     * grid itself changes: arrow mode carries a backspace key, because it has
     * no second switch to undo with.
     */
    var inputMode: InputMode by mutableStateOf(initialInputMode)
        private set

    var onInputModeChanged: ((InputMode) -> Unit)? = null

    /**
     * Where the arrow pad sits. Whichever hand still reaches, and where the
     * forearm falls on the way, are not things anyone gets to choose, so the
     * app moves instead. See [ArrowPlacement].
     */
    var arrowPlacement: ArrowPlacement by mutableStateOf(initialArrowPlacement)
        private set

    var onArrowPlacementChanged: ((ArrowPlacement) -> Unit)? = null

    /**
     * Whether choose comes before the arrows: above them in a column, or to
     * their left in a strip.
     *
     * A separate question from where the pad goes, because the answer is about
     * the arm rather than the screen — whichever of the two the forearm sweeps
     * over on the way in is the one that should not be there.
     */
    var chooseFirst: Boolean by mutableStateOf(initialChooseFirst)
        private set

    var onChooseFirstChanged: ((Boolean) -> Unit)? = null

    fun useChooseFirst(enabled: Boolean) {
        if (enabled == chooseFirst) return
        chooseFirst = enabled
        onChooseFirstChanged?.invoke(enabled)
    }

    /**
     * Whether the arrow pad carries its own two erase buttons.
     *
     * Erasing is the one thing that is worth its own button. Everything else on
     * the grid is a letter she meant to reach, but a mistake has to be undone
     * *now*, and steering to the corner for it means several arrow presses at
     * the exact moment she is least happy with the app. The keys stay on the
     * grid as well, so turning this off loses nothing but the shortcut.
     */
    var eraseKeys: Boolean by mutableStateOf(initialEraseKeys)
        private set

    var onEraseKeysChanged: ((Boolean) -> Unit)? = null

    fun useEraseKeys(enabled: Boolean) {
        if (enabled == eraseKeys) return
        eraseKeys = enabled
        clearArmed = false
        onEraseKeysChanged?.invoke(enabled)
    }

    /**
     * Whether the pad's clear-all button has been pressed once and is waiting
     * for the second press that actually empties the sentence.
     *
     * The one destructive button in the app now sits within reach of a hand
     * that cannot always be placed, and in arrow mode there is no undo switch
     * to take a wipe back with. So it costs two presses instead of one. Any
     * other action disarms it, which means the cost of a mistaken first press
     * is nothing at all.
     */
    var clearArmed: Boolean by mutableStateOf(false)
        private set

    /**
     * Erase one letter from the arrow pad, without steering to the key first.
     */
    fun eraseLetter() {
        clearArmed = false
        if (!canErase()) return
        val textBefore = text
        apply(Key.Delete)
        if (text != textBefore) {
            push(Step.ChangedText(textBefore, cursor.position.row))
            refreshSuggestions()
        }
    }

    /**
     * Empty the sentence from the arrow pad. The first press only arms it; the
     * second one, with nothing else in between, does it.
     */
    fun eraseAll() {
        if (!canErase()) { clearArmed = false; return }
        if (text.isEmpty()) { clearArmed = false; return }
        if (!clearArmed) {
            clearArmed = true
            return
        }
        clearArmed = false
        val textBefore = text
        apply(Key.Clear)
        push(Step.ChangedText(textBefore, cursor.position.row))
        refreshSuggestions()
    }

    /**
     * Whether the arrows are drawn as four separate buttons or as one shape.
     *
     * Appearance only: see [ArrowShape]. What answers to a tap does not change.
     */
    var arrowShape: ArrowShape by mutableStateOf(initialArrowShape)
        private set

    var onArrowShapeChanged: ((ArrowShape) -> Unit)? = null

    fun useArrowShape(shape: ArrowShape) {
        if (shape == arrowShape) return
        arrowShape = shape
        onArrowShapeChanged?.invoke(shape)
    }

    /** The pad's erase buttons are hers, and only while she is writing. */
    private fun canErase(): Boolean =
        eraseKeys && !inSettings && !inTutorial && !inPhrases

    /**
     * The cell she touched last, in direct mode. Null until she touches one.
     *
     * Lit so that a press is visibly acknowledged. Nothing is highlighted
     * before her first touch, because a highlight sitting on a cell she has not
     * chosen is exactly the thing that makes people believe the app is doing
     * something it is not.
     */
    var directLast: Position? by mutableStateOf(null)
        private set

    /**
     * Battery charge, 0..100, or null before the first reading arrives.
     *
     * On her own tablet the app hides the system bars and locked mode stops
     * anyone leaving it, so this is the only place the charge can be seen. A
     * communication device that goes flat without warning is a person left
     * unable to speak, which makes this less of an ornament than it looks.
     */
    var batteryPercent: Int? by mutableStateOf(null)
        private set

    var batteryCharging: Boolean by mutableStateOf(false)
        private set

    fun setBattery(percent: Int?, charging: Boolean) {
        batteryPercent = percent?.coerceIn(0, 100)
        batteryCharging = charging
    }

    /**
     * Locked mode: the app holds the screen and cannot be left. For her own
     * device, where wandering out of the app means losing her voice until
     * somebody notices. Off on a helper's phone, which is also an ordinary
     * phone. A helper can always unlock from settings, which is why locking is
     * safe to offer at all.
     */
    var locked: Boolean by mutableStateOf(initialLocked)
        private set

    var onLockedChanged: ((Boolean) -> Unit)? = null

    /** Reopen the app by itself after the device restarts. */
    var openOnBoot: Boolean by mutableStateOf(initialOpenOnBoot)
        private set

    var onOpenOnBootChanged: ((Boolean) -> Unit)? = null

    fun useLocked(enabled: Boolean) {
        if (enabled == locked) return
        locked = enabled
        onLockedChanged?.invoke(enabled)
    }

    fun useOpenOnBoot(enabled: Boolean) {
        if (enabled == openOnBoot) return
        openOnBoot = enabled
        onOpenOnBootChanged?.invoke(enabled)
    }

    /** Everything that differs by language: letters, words, phrases, voice. */
    var language: Language by mutableStateOf(initialLanguage)
        private set

    /** Called when the language changes, so it can be saved and reloaded. */
    var onLanguageChanged: ((Language) -> Unit)? = null

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
    val rows: List<List<Key>> get() = if (inPhrases) phrasesGrid else writingRows()

    /**
     * True while sí and no have keys of their own, which is while the sentence
     * is still empty. See [EXTRA_SUGGESTION_SLOTS].
     */
    private val yesNoOnGrid: Boolean get() = text.isEmpty()

    /**
     * The writing grid as it stands right now.
     *
     * The same six cells in the same places, every time. Only what two of them
     * mean changes, and only between an empty sentence and a started one, so
     * the shape the scanner and the cursor are working over never moves under
     * her.
     */
    private fun writingRows(): List<List<Key>> {
        if (yesNoOnGrid) return writingGrid
        return writingGrid.mapIndexed { index, row ->
            if (index != SUGGESTION_ROW) {
                row
            } else {
                row.map { key ->
                    when (key) {
                        Key.Yes -> Key.Suggestion(SUGGESTION_SLOTS)
                        Key.No -> Key.Suggestion(SUGGESTION_SLOTS + 1)
                        else -> key
                    }
                }
            }
        }
    }

    private var writingGrid: List<List<Key>> =
        keyboardFor(initialLanguage, withDelete = initialInputMode != InputMode.Scan)

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

    /**
     * The walkthrough, which explains the app to the person setting it up.
     *
     * Not scannable and not reachable from the grid, like settings: it is for
     * the helper, and every control the grid has to carry costs her time on
     * every letter for the rest of the day.
     */
    var inTutorial: Boolean by mutableStateOf(false)
        private set

    /** Which page of the walkthrough is showing. */
    var tutorialPage: Int by mutableStateOf(0)
        private set

    /** The pages of the current language's walkthrough. */
    val tutorialPages: List<TutorialPage> get() = language.tutorial

    fun openTutorial() {
        tutorialPage = 0
        inTutorial = true
    }

    fun nextTutorialPage() {
        if (tutorialPage < tutorialPages.size - 1) tutorialPage++ else closeTutorial()
    }

    fun previousTutorialPage() {
        if (tutorialPage > 0) tutorialPage--
    }

    /**
     * Leaves the walkthrough and goes back to writing.
     *
     * Reached by a switch press as well as by the button, for the same reason
     * settings is: a screen she cannot leave without a helper's hands is a
     * screen that can strand her.
     */
    fun closeTutorial() {
        if (!inTutorial) return
        inTutorial = false
        // Out to the grid rather than back to settings. The last page asks the
        // helper to go and try it, so that is where they should land.
        closeSettings()
        restartTiming()
    }

    /** The carer's screen for finding out what a switch interface sends. */
    var inDiagnostics: Boolean by mutableStateOf(false)
        private set

    /** Newest first. Only filled while [inDiagnostics]. */
    var recentKeys: List<KeyReport> by mutableStateOf(emptyList())
        private set

    /**
     * Bumped when a carer touches the diagnostics screen, so the idle timeout
     * starts again. Only a touch counts, not a button press, so a drifting
     * controller cannot hold the screen open. It closes on its own otherwise.
     */
    var diagnosticsActivity: Int by mutableStateOf(0)
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
     * this action. The input layer captures the next press and calls
     * [addedBinding]. Null the rest of the time.
     */
    var bindingRole: SwitchRole? by mutableStateOf(null)
        private set

    /**
     * Non-null while showing "added — another?" after a capture, so several
     * buttons can be bound to the same action in one go.
     */
    var bindingMoreRole: SwitchRole? by mutableStateOf(null)
        private set

    /** The buttons bound during the current binding session, for the prompt. */
    var boundThisSession: List<String> by mutableStateOf(emptyList())
        private set

    /** The buttons currently bound to each action, shown in settings. */
    var writeButtonLabels: List<String> by mutableStateOf(emptyList())
        private set
    var undoButtonLabels: List<String> by mutableStateOf(emptyList())
        private set

    /** Called with a phrase to say out loud. */
    var onSpeak: ((String) -> Unit)? = null

    /** True while the phrases screen is showing instead of the writing grid. */
    var inPhrases: Boolean by mutableStateOf(false)
        private set

    /** The saved phrases, editable by a helper. */
    private var phrases: List<String> = initialLanguage.defaultPhrases
    private var phrasesGrid: List<List<Key>> = phrasesKeyboard(phrases)

    /** True when the undo button would do something. */
    var canUndo: Boolean by mutableStateOf(false)
        private set

    private var scanner = Scanner(ScanLayout(writingGrid.map { it.size }))

    /** Used instead of [scanner] in arrow mode. Both are kept in step. */
    private var cursor = Cursor(ScanLayout(writingGrid.map { it.size }))

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
        // Arrow mode has no row-scanning stage, so it starts on a single cell.
        state = currentState()
    }

    /**
     * Where the highlight belongs right now, according to whichever machine is
     * driving. Every place that used to read scanner.state goes through here
     * instead, so no mode can be forgotten in one branch.
     */
    private fun currentState(): ScanState = when (inputMode) {
        InputMode.Scan -> scanner.state
        InputMode.Arrows -> cursor.position.let { ScanState.Cell(it.row, it.col) }
        // Direct mode has no cursor at all; what it lights is the cell she last
        // touched, which isLit reads from directLast rather than from here.
        InputMode.Direct -> ScanState.Cell(0, 0)
    }

    /** The scan interval elapsed. */
    fun tick() {
        // Nothing is scanning while a carer has settings or the guide open.
        if (inSettings || inTutorial) return
        // Only scanning has a highlight that moves on its own. The clock on
        // screen keeps running; it simply has nothing to advance.
        if (inputMode != InputMode.Scan) return

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
        clearArmed = false
        if (inTutorial) {
            closeTutorial()
            return
        }
        if (inSettings) {
            // Any press gets a carer out of settings. Settings is the one place
            // the grid cannot reach, so it must not need the grid to leave.
            closeSettings()
            return
        }

        if (inputMode == InputMode.Arrows) {
            pressArrows()
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
     * A press in arrow mode: take whatever the cursor is sitting on.
     *
     * The cursor deliberately stays where it is afterwards. Scanning has to
     * return to the top because the rhythm is what she is following, but here
     * she is steering, and the next letter is far more often near the last one
     * than back at the beginning. Doubled letters cost one press.
     */
    private fun pressArrows() {
        val position = cursor.position
        when (val key = rows[position.row][position.col]) {
            Key.OpenPhrases -> { openPhrases(); return }
            Key.Back -> { closePhrases(); return }
            is Key.Phrase -> onSpeak?.invoke(key.text)

            else -> {
                val textBefore = text
                apply(key)
                push(Step.ChangedText(textBefore, position.row))
                noticeFinishedWord(textBefore, text)
                refreshSuggestions()
            }
        }
    }

    /**
     * She touched the cell at [row], [col]. Ignored unless direct mode is on.
     *
     * The whole of direct mode. There is no cursor to move and no rhythm to
     * follow: the cell she touched is the cell she chose.
     */
    fun touchKey(row: Int, col: Int) {
        if (inputMode != InputMode.Direct || inSettings || inTutorial) return
        val grid = rows
        if (row !in grid.indices || col !in grid[row].indices) return

        directLast = Position(row, col)
        when (val key = grid[row][col]) {
            Key.OpenPhrases -> openPhrases()
            Key.Back -> closePhrases()
            is Key.Phrase -> onSpeak?.invoke(key.text)

            else -> {
                val textBefore = text
                apply(key)
                push(Step.ChangedText(textBefore, row))
                noticeFinishedWord(textBefore, text)
                refreshSuggestions()
            }
        }
    }

    /** Steer the cursor. Ignored unless arrow mode is on. */
    fun moveUp() = move { cursor.up() }

    fun moveDown() = move { cursor.down() }

    fun moveLeft() = move { cursor.left() }

    fun moveRight() = move { cursor.right() }

    private inline fun move(step: () -> Unit) {
        if (inputMode != InputMode.Arrows || inSettings || inTutorial) return
        // Anything else she does is an answer of "no" to the clear-all button.
        clearArmed = false
        step()
        state = currentState()
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
        if (inTutorial) {
            closeTutorial()
            return
        }
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

                // Only scanning has a row to be put back inside. In arrow mode
                // the cursor is already wherever she left it, which is where
                // she will want to try again from, so it is not moved.
                if (inputMode == InputMode.Scan) {
                    scanner.goIntoRow(step.row)
                    state = scanner.state

                    // She is back on the first letter of the row, so it earns
                    // the extra time again as if she had entered it herself.
                    justEnteredRow = true
                    restartTiming()

                    // She is inside the row again, so one more undo should take
                    // her out of it, exactly as if she had just opened it.
                    push(Step.OpenedRow(step.row))
                }

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
     * Yes and no have their own keys, so a suggestion slot spent on them would
     * waste one of the three most valuable cells. Folded, and per language.
     */
    private fun alreadyOnGrid(): Set<String> =
        setOf(Words.fold(language.yesLabel), Words.fold(language.noLabel))

    /**
     * Switches language. The letters, their order, the words on the keys and
     * the phrases all change together, so the grid is rebuilt and the scan
     * starts again from the top.
     */
    fun changeLanguage(newLanguage: Language) {
        if (newLanguage.code == language.code) return
        language = newLanguage
        writingGrid = keyboardFor(newLanguage, withDelete = inputMode != InputMode.Scan)
        setPhrases(newLanguage.defaultPhrases)
        inPhrases = false
        rescan(writingGrid)
        justEnteredRow = false
        restartTiming()
        history.clear()
        refreshCanUndo()
        refreshSuggestions()
        onLanguageChanged?.invoke(newLanguage)
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
        rescan(writingGrid)
        justEnteredRow = false
        restartTiming()
        // Leave no phrase-screen steps behind to undo once back at writing.
        history.clear()
        refreshCanUndo()
    }

    /** Starts afresh over a new layout, from the top left. */
    private fun rescan(layout: List<List<Key>>) {
        // The cell she last touched belongs to the grid that is going away.
        directLast = null
        val shape = ScanLayout(layout.map { it.size })
        scanner = Scanner(shape)
        // Rebuilt together, so a cursor can never be left pointing at a cell
        // that the new grid does not have.
        cursor = Cursor(shape)
        state = currentState()
    }

    /**
     * Opens settings. Deliberately not reachable by scanning, so she cannot
     * land here by mistake; a carer opens it by touch.
     */
    fun openSettings() {
        clearArmed = false
        inSettings = true
    }

    /** Opened from settings, by touch. Every key press is captured and shown. */
    fun openDiagnostics() {
        recentKeys = emptyList()
        inDiagnostics = true
        noteDiagnosticsActivity()
    }

    fun noteDiagnosticsActivity() {
        diagnosticsActivity++
    }

    fun reportKey(report: KeyReport) {
        if (!inDiagnostics) return
        recentKeys = (listOf(report) + recentKeys).take(MAX_REPORTED_KEYS)
        // Button presses deliberately do NOT keep the screen open. A drifting
        // controller sends a steady trickle of phantom presses, and letting
        // those reset the timer would hold the screen open for good. Only a
        // touch resets it; otherwise it closes on its own on the timer below.
    }

    /**
     * Switches between scanning and arrows.
     *
     * The writing grid is rebuilt, because arrow mode carries a backspace key
     * that scanning does not need, and both machines start again from the top
     * left so the change is never half applied.
     */
    fun useInputMode(mode: InputMode) {
        if (mode == inputMode) return
        inputMode = mode
        writingGrid = keyboardFor(language, withDelete = mode != InputMode.Scan)
        rescan(if (inPhrases) phrasesGrid else writingGrid)
        justEnteredRow = false
        restartTiming()
        history.clear()
        refreshCanUndo()
        onInputModeChanged?.invoke(mode)
    }

    fun useArrowPlacement(placement: ArrowPlacement) {
        if (placement == arrowPlacement) return
        arrowPlacement = placement
        onArrowPlacementChanged?.invoke(placement)
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

    /** A helper asked to bind buttons for [role]; wait for the first press. */
    fun startBinding(role: SwitchRole) {
        boundThisSession = emptyList()
        bindingMoreRole = null
        bindingRole = role
    }

    fun cancelBinding() {
        bindingRole = null
        bindingMoreRole = null
    }

    /**
     * The input layer captured a button for [role] and saved it. Move to the
     * "add another?" prompt so more buttons can be bound to the same action.
     */
    fun addedBinding(role: SwitchRole, label: String) {
        boundThisSession = boundThisSession + label
        bindingRole = null
        bindingMoreRole = role
    }

    /** From the prompt: bind one more button to the same action. */
    fun bindMore() {
        bindingMoreRole?.let { bindingRole = it }
        bindingMoreRole = null
    }

    /** From the prompt: done binding this action. */
    fun finishBinding() {
        bindingRole = null
        bindingMoreRole = null
    }

    /** The input layer reports the buttons now bound to each action. */
    fun setButtonLabels(write: List<String>, undo: List<String>) {
        writeButtonLabels = write
        undoButtonLabels = undo
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
        // Arrow mode has no rhythm to restore and no reason to move her, so the
        // cursor is left exactly where she parked it.
        if (inputMode == InputMode.Scan) scanner.goToRow(0)
        state = currentState()
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
        applyForgiveness()
        refreshSuggestions()
    }

    /**
     * Whether a word with a mistake in it still gets suggestions.
     *
     * A press that lands twice, or not at all, or on the neighbouring cell,
     * used to empty the suggestion row completely — and the row is worth more
     * to her at that moment than at any other, because finishing the word from
     * a list is how she gets out of the mistake without erasing back to it.
     */
    var forgiveMistakes: Boolean by mutableStateOf(initialForgiveMistakes)
        private set

    var onForgiveMistakesChanged: ((Boolean) -> Unit)? = null

    fun useForgiveMistakes(enabled: Boolean) {
        if (enabled == forgiveMistakes) return
        forgiveMistakes = enabled
        applyForgiveness()
        refreshSuggestions()
        onForgiveMistakesChanged?.invoke(enabled)
    }

    private fun applyForgiveness() {
        (predictor as? NgramPredictor)?.forgiving = forgiveMistakes
    }

    /**
     * Whether the sentence she is writing is set in bold.
     *
     * The grid has always been bold and the sentence has not, which is the
     * wrong way round: the letters are read one at a time under a highlight,
     * and the sentence is read across a room by whoever she is talking to.
     */
    var boldWriting: Boolean by mutableStateOf(initialBoldWriting)
        private set

    var onBoldWritingChanged: ((Boolean) -> Unit)? = null

    fun useBoldWriting(enabled: Boolean) {
        if (enabled == boldWriting) return
        boldWriting = enabled
        onBoldWritingChanged?.invoke(enabled)
    }

    /** The word shown on a cell right now, in the current language. */
    fun label(key: Key): String = when (key) {
        is Key.Suggestion -> suggestions.getOrElse(key.slot) { "" }
        is Key.Letter -> key.char
        is Key.Phrase -> key.text
        Key.Space -> language.spaceLabel
        Key.Yes -> language.yesLabel
        Key.No -> language.noLabel
        Key.OpenPhrases -> language.phrasesLabel
        Key.Back -> language.backLabel
        Key.Clear -> language.clearLabel
        Key.Delete -> "\u232b"
    }

    /** Should the cell at this position be highlighted right now? */
    fun isLit(row: Int, col: Int): Boolean {
        if (inputMode == InputMode.Direct) {
            return directLast?.let { it.row == row && it.col == col } == true
        }
        return litByCursor(row, col)
    }

    private fun litByCursor(row: Int, col: Int): Boolean = when (val current = state) {
        // A whole row lights up before it is entered.
        is ScanState.Row -> current.row == row
        is ScanState.Cell -> current.row == row && current.col == col
    }

    private fun apply(key: Key) {
        when (key) {
            is Key.Letter -> text += key.char
            is Key.Suggestion -> applySuggestion(key.slot)
            Key.Yes -> text += language.yesLabel + " "
            Key.No -> text += language.noLabel + " "
            Key.Space -> text += " "
            Key.Clear -> text = ""
            Key.Delete -> if (text.isNotEmpty()) text = text.dropLast(1)

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
        // Three while sí and no hold their own keys; five once those two cells
        // have been lent to prediction.
        val wanted = if (yesNoOnGrid) {
            SUGGESTION_SLOTS
        } else {
            SUGGESTION_SLOTS + EXTRA_SUGGESTION_SLOTS
        }

        // A few more than needed, because some get dropped just below.
        val offered = predictor.predict(text, wanted + 2)
        suggestions = offered
            // Only worth dropping while sí and no are actually on the grid. Once
            // they are not, they are ordinary words she may well want, and
            // refusing to offer them would be refusing her two common answers.
            .filterNot { yesNoOnGrid && Words.fold(it) in alreadyOnGrid() }
            // Upper case throughout, to match the letter keys.
            .map { it.uppercase() }
            .take(wanted)
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

        /** The suggestions, yes, no and the phrases key all live on row 0. */
        private const val SUGGESTION_ROW = 0

        private const val MAX_HISTORY = 100

        /** Enough lines to see a bounce burst, few enough to read at a glance. */
        private const val MAX_REPORTED_KEYS = 10

        /** Diagnostics closes this long after the carer's last touch. */
        const val DIAGNOSTICS_IDLE_MS = 30_000L


    }
}
