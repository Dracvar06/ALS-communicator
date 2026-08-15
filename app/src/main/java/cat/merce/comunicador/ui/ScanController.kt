package cat.merce.comunicador.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.merce.comunicador.scan.ScanLayout
import cat.merce.comunicador.scan.ScanState
import cat.merce.comunicador.scan.Scanner
import cat.merce.comunicador.scan.SelectResult

/**
 * Joins the scan machine to the screen.
 *
 * The [Scanner] deals only in row and column numbers. This class is what turns
 * a chosen position into a letter and puts it on the end of the sentence. It
 * holds the only mutable state the screen cares about.
 */
class ScanController(
    val rows: List<List<Key>> = CATALAN_KEYBOARD,
    val scanIntervalMs: Long = DEFAULT_SCAN_INTERVAL_MS,
) {

    private val scanner = Scanner(ScanLayout(rows.map { it.size }))

    /** Where the highlight is. */
    var state: ScanState by mutableStateOf(scanner.state)
        private set

    /** What she has written so far. */
    var text: String by mutableStateOf("")
        private set

    /** The scan interval elapsed. */
    fun tick() {
        scanner.tick()
        state = scanner.state
    }

    /** The switch was pressed. */
    fun press() {
        when (val result = scanner.select()) {
            // Opening a row types nothing.
            SelectResult.EnteredRow -> Unit
            is SelectResult.SelectedCell -> {
                val position = result.position
                apply(rows[position.row][position.col])
            }
        }
        state = scanner.state
    }

    private fun apply(key: Key) {
        text = when (key) {
            is Key.Letter -> text + key.char
            Key.Space -> "$text "
            Key.Delete -> text.dropLast(1)
            Key.Clear -> ""
        }
    }

    /** Should the cell at this position be highlighted right now? */
    fun isLit(row: Int, col: Int): Boolean = when (val current = state) {
        // A whole row lights up before it is entered.
        is ScanState.Row -> current.row == row
        is ScanState.Cell -> current.row == row && current.col == col
    }

    companion object {
        /**
         * How long each step lasts, in milliseconds.
         *
         * This is the single most important number in the app and the one that
         * will need tuning against a real person. It is a constant for now, so
         * changing it means a rebuild. Moving it into a file a carer can edit
         * is the next job.
         */
        const val DEFAULT_SCAN_INTERVAL_MS = 1000L
    }
}
