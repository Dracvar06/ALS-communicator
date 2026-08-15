package cat.merce.comunicador.scan

/** Where a cell sits in the grid. Row and column are both zero based. */
data class Position(val row: Int, val col: Int)

/**
 * The shape of the grid, and nothing else.
 *
 * This deliberately holds no letters, labels or meanings. The scanner needs to
 * know how far to move and when to wrap, and knowing anything more would tie
 * the timing-critical layer to whatever the grid happens to contain today.
 * Turning a [Position] back into a letter is the caller's job.
 *
 * @param rowLengths how many cells each row holds, top row first. Rows may
 *   differ in length.
 */
class ScanLayout(rowLengths: List<Int>) {

    /** Copied on the way in so a caller cannot reshape the grid mid-scan. */
    private val rowLengths: List<Int> = rowLengths.toList()

    init {
        require(this.rowLengths.isNotEmpty()) {
            "A layout needs at least one row"
        }
        require(this.rowLengths.all { it >= 1 }) {
            // Scanning across an empty row would never reach an end, so the
            // pass counter would never tick and the row would never release.
            "Every row needs at least one cell, got $rowLengths"
        }
    }

    val rowCount: Int get() = rowLengths.size

    fun cellsIn(row: Int): Int = rowLengths[row]
}
