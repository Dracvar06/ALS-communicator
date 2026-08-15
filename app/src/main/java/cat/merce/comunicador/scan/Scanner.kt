package cat.merce.comunicador.scan

/**
 * The row-column scan state machine.
 *
 * The cursor steps down the rows. A press enters a row. The cursor then steps
 * across that row. A press chooses a cell.
 *
 * This class does not know what time it is, and that is the point. It never
 * sleeps, starts a timer, reads a clock or touches a thread. Something outside
 * calls [tick] when the scan interval elapses; all this class decides is what a
 * tick *means*. Keeping every measurement of time out of here is what makes the
 * hard constraint about a scan that never stutters possible to hold: there is
 * nothing in this file that can be slow.
 *
 * Not thread safe, and does not need to be. Drive it from one thread.
 *
 * @param rowPassLimit how many complete sweeps of a row happen before the row
 *   gives up and returns to row scanning. See [tick].
 */
class Scanner(
    private val layout: ScanLayout,
    private val rowPassLimit: Int = DEFAULT_ROW_PASS_LIMIT,
) {

    init {
        require(rowPassLimit >= 1) {
            // Zero passes would leave a row the instant it was entered, which
            // would make every cell in the grid unreachable.
            "rowPassLimit must be at least 1, was $rowPassLimit"
        }
    }

    /**
     * The first row starts out highlighted rather than blank, so that it gets a
     * full interval of attention like every other row does.
     */
    var state: ScanState = ScanState.Row(0)
        private set

    /** Complete sweeps of the row currently entered. Meaningless while scanning rows. */
    private var passesCompleted = 0

    /** The scan interval elapsed. Move the cursor on. */
    fun tick() {
        state = when (val current = state) {
            // Row scanning wraps forever and never gives up. There is nothing
            // to fall back to from here, so stopping would strand her.
            is ScanState.Row -> ScanState.Row((current.row + 1) % layout.rowCount)

            is ScanState.Cell -> advanceWithin(current)
        }
    }

    private fun advanceWithin(current: ScanState.Cell): ScanState {
        val nextCol = current.col + 1
        if (nextCol < layout.cellsIn(current.row)) {
            return current.copy(col = nextCol)
        }

        // The cursor has run off the end, so that is one full sweep done.
        passesCompleted++
        return if (passesCompleted >= rowPassLimit) {
            // Give up on this row. A press on the wrong row is the one mistake
            // a single switch cannot undo, so the row has to release her by
            // itself. Resuming at this same row rather than the top saves a
            // whole cycle of the grid, since wherever she meant to go is
            // probably next door.
            ScanState.Row(current.row)
        } else {
            current.copy(col = 0)
        }
    }

    /** The switch was pressed. */
    fun select(): SelectResult = when (val current = state) {
        is ScanState.Row -> {
            passesCompleted = 0
            state = ScanState.Cell(row = current.row, col = 0)
            SelectResult.EnteredRow
        }

        is ScanState.Cell -> {
            // Always back to the top. Resuming near the last choice would be
            // quicker, but starting from the same place every single time makes
            // the rhythm after a press learnable, and that matters more.
            state = ScanState.Row(0)
            SelectResult.SelectedCell(Position(row = current.row, col = current.col))
        }
    }

    companion object {
        const val DEFAULT_ROW_PASS_LIMIT = 2
    }
}
