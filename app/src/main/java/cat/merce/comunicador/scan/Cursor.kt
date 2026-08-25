package cat.merce.comunicador.scan

/**
 * A cursor she steers herself, one cell at a time.
 *
 * The alternative to [Scanner]. Where the scanner moves on its own and she
 * waits for it to arrive, this one never moves unless she moves it: four
 * directions and a choice. It costs more presses per letter, but nothing is
 * ever missed and nothing has to be waited out, and for someone who can reach
 * the screen that trade is often the better one.
 *
 * Like the scanner, this knows nothing about time, letters or the screen. It
 * moves a row and a column around a shape and stops there.
 *
 * Not thread safe, and does not need to be. Drive it from one thread.
 */
class Cursor(private val layout: ScanLayout, start: Position = Position(0, 0)) {

    var position: Position = clampInto(start.row, start.col)
        private set

    /**
     * The column she last aimed for, which is not always the column she is in.
     *
     * Rows differ in length, so moving down from the sixth column into a
     * four-column row has to land somewhere nearer the left. Remembering what
     * she actually asked for means carrying on downwards returns her to the
     * sixth column rather than stranding her at the fourth. Text editors behave
     * this way, and being wrong about it is the sort of thing that feels broken
     * without anyone being able to say why.
     */
    private var desiredCol: Int = position.col

    fun left() {
        val row = position.row
        val col = position.col - 1
        // Wrapping rather than stopping at the edge: with one row of the grid
        // in front of her, the last cell is one press away from the first.
        position = position.copy(col = if (col < 0) layout.cellsIn(row) - 1 else col)
        desiredCol = position.col
    }

    fun right() {
        val row = position.row
        val col = position.col + 1
        position = position.copy(col = if (col >= layout.cellsIn(row)) 0 else col)
        desiredCol = position.col
    }

    fun up() = vertical(-1)

    fun down() = vertical(1)

    private fun vertical(delta: Int) {
        val rows = layout.rowCount
        val row = ((position.row + delta) % rows + rows) % rows
        position = Position(row = row, col = desiredCol.coerceAtMost(layout.cellsIn(row) - 1))
        // desiredCol deliberately survives, so a short row passed through does
        // not permanently drag her leftwards.
    }

    /**
     * Puts the cursor somewhere directly, clamped to fit.
     *
     * Used when the grid underneath changes shape, so the cursor can never be
     * left pointing at a cell that no longer exists.
     */
    fun moveTo(row: Int, col: Int) {
        position = clampInto(row, col)
        desiredCol = position.col
    }

    private fun clampInto(row: Int, col: Int): Position {
        val safeRow = row.coerceIn(0, layout.rowCount - 1)
        return Position(safeRow, col.coerceIn(0, layout.cellsIn(safeRow) - 1))
    }
}
