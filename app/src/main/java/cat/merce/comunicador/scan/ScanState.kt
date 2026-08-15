package cat.merce.comunicador.scan

/**
 * Where the cursor is right now.
 *
 * Row-column scanning has exactly two modes, so this type has exactly two
 * shapes. The UI reads this to decide what to highlight: a whole row, or one
 * cell.
 */
sealed interface ScanState {

    /** The whole of [row] is highlighted, waiting to be entered. */
    data class Row(val row: Int) : ScanState

    /** A single cell inside a row that has already been entered. */
    data class Cell(val row: Int, val col: Int) : ScanState
}

/**
 * What a switch press turned out to mean.
 *
 * A press does one of two quite different things depending on where the cursor
 * was, and the caller has to tell them apart: one of them produces a letter and
 * the other does not.
 */
sealed interface SelectResult {

    /** The press opened a row. Nothing has been typed. */
    data object EnteredRow : SelectResult

    /** The press chose a cell. This is the one that produces a letter. */
    data class SelectedCell(val position: Position) : SelectResult
}
