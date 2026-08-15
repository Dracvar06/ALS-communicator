package cat.merce.comunicador.scan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

/**
 * The scan state machine, tested with no Android and no clock.
 *
 * The machine never measures time. Something outside it decides when a tick
 * happens; the machine only decides what a tick means. That is why every test
 * here can call tick() as fast as it likes and still be checking real
 * behaviour: the scan interval is a separate concern, and keeping it out of
 * this class is what makes the timing safe to reason about later.
 */
class ScannerTest {

    /** Three rows of four, five and two cells. Ragged on purpose. */
    private fun layout() = ScanLayout(listOf(4, 5, 2))

    private fun scanner(passLimit: Int = 2) =
        Scanner(layout(), rowPassLimit = passLimit)

    // ---------------------------------------------------------------
    // Scanning across the rows
    // ---------------------------------------------------------------

    @Test
    fun `starts on the first row`() {
        // The first row must already be highlighted before any tick happens,
        // otherwise the user never gets a full interval to react to it.
        assertEquals(ScanState.Row(0), scanner().state)
    }

    @Test
    fun `a tick moves to the next row`() {
        val s = scanner()
        s.tick()
        assertEquals(ScanState.Row(1), s.state)
    }

    @Test
    fun `row scanning wraps around to the first row`() {
        val s = scanner()
        s.tick() // row 1
        s.tick() // row 2, the last one
        s.tick()
        assertEquals(ScanState.Row(0), s.state)
    }

    @Test
    fun `row scanning never stops on its own`() {
        // There is nothing to fall back to from row scanning, so it loops
        // forever. Giving up here would be a dead end.
        val s = scanner()
        repeat(100) { s.tick() }
        assertEquals(ScanState.Row(100 % 3), s.state)
    }

    // ---------------------------------------------------------------
    // Entering a row
    // ---------------------------------------------------------------

    @Test
    fun `pressing on a row enters it at the first cell`() {
        val s = scanner()
        s.tick() // row 1
        s.select()
        assertEquals(ScanState.Cell(row = 1, col = 0), s.state)
    }

    @Test
    fun `entering a row is not a selection`() {
        val s = scanner()
        assertEquals(SelectResult.EnteredRow, s.select())
    }

    @Test
    fun `a tick moves to the next cell in the row`() {
        val s = scanner()
        s.select() // enter row 0
        s.tick()
        assertEquals(ScanState.Cell(row = 0, col = 1), s.state)
    }

    // ---------------------------------------------------------------
    // Choosing a cell
    // ---------------------------------------------------------------

    @Test
    fun `pressing on a cell reports which cell it was`() {
        val s = scanner()
        s.tick()   // row 1
        s.select() // enter row 1, cell 0
        s.tick()   // cell 1
        s.tick()   // cell 2
        assertEquals(
            SelectResult.SelectedCell(Position(row = 1, col = 2)),
            s.select()
        )
    }

    @Test
    fun `after choosing a cell scanning restarts at the first row`() {
        // Always returning to the top means the position after a selection is
        // the same every single time, so the rhythm is learnable. Resuming
        // near the last choice would be faster but less predictable.
        val s = scanner()
        s.tick()   // row 1
        s.select() // enter row 1
        s.select() // choose cell (1, 0)
        assertEquals(ScanState.Row(0), s.state)
    }

    // ---------------------------------------------------------------
    // Getting out of a row entered by mistake
    // ---------------------------------------------------------------

    @Test
    fun `a row is abandoned after the configured number of passes`() {
        // Hard constraint: no dead ends. If she presses on the wrong row, the
        // only way out with one switch is for the row to give up by itself.
        val s = scanner(passLimit = 2)
        s.select() // enter row 0, which has 4 cells

        repeat(4) { s.tick() } // first pass complete, back to cell 0
        assertEquals(ScanState.Cell(row = 0, col = 0), s.state)

        repeat(4) { s.tick() } // second pass complete, so give up
        assertEquals(ScanState.Row(0), s.state)
    }

    @Test
    fun `an unfinished pass does not abandon the row`() {
        val s = scanner(passLimit = 1)
        s.select() // enter row 0
        repeat(3) { s.tick() } // cell 3, the last one, still inside the row
        assertEquals(ScanState.Cell(row = 0, col = 3), s.state)
    }

    @Test
    fun `abandoning a row resumes scanning at that same row`() {
        // She was heading somewhere near row 1. Dropping her back at row 0
        // would cost a full cycle of the grid for no reason.
        val s = scanner(passLimit = 1)
        s.tick()   // row 1
        s.select() // enter row 1, which has 5 cells
        repeat(5) { s.tick() }
        assertEquals(ScanState.Row(1), s.state)
        s.tick()
        assertEquals(ScanState.Row(2), s.state)
    }

    @Test
    fun `the pass count resets each time a row is entered`() {
        val s = scanner(passLimit = 1)
        s.select()             // enter row 0
        repeat(4) { s.tick() } // one pass, so give up
        assertEquals(ScanState.Row(0), s.state)

        s.select()             // enter row 0 again
        assertEquals(ScanState.Cell(row = 0, col = 0), s.state)
        repeat(3) { s.tick() } // a fresh pass, still inside the row
        assertEquals(ScanState.Cell(row = 0, col = 3), s.state)
    }

    // ---------------------------------------------------------------
    // Being put back, which is what undo needs
    // ---------------------------------------------------------------

    @Test
    fun `it can be sent back out to row scanning`() {
        val s = scanner()
        s.tick()   // row 1
        s.select() // inside row 1
        s.goToRow(1)
        assertEquals(ScanState.Row(1), s.state)
        s.tick()
        assertEquals(ScanState.Row(2), s.state)
    }

    @Test
    fun `it can be sent back into a row at the first cell`() {
        val s = scanner()
        s.goIntoRow(2)
        assertEquals(ScanState.Cell(row = 2, col = 0), s.state)
    }

    @Test
    fun `being sent back into a row gives a fresh set of passes`() {
        // Otherwise undoing into a row she had nearly used up would drop her
        // straight back out of it.
        val s = scanner(passLimit = 2)
        s.select()             // enter row 0, four cells
        repeat(4) { s.tick() } // one pass gone
        s.goIntoRow(0)

        repeat(4) { s.tick() } // this is pass one again, not pass two
        assertEquals(ScanState.Cell(row = 0, col = 0), s.state)
    }

    @Test
    fun `a row that does not exist is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { scanner().goToRow(3) }
        assertThrows(IllegalArgumentException::class.java) { scanner().goIntoRow(-1) }
    }

    // ---------------------------------------------------------------
    // Rows of different lengths
    // ---------------------------------------------------------------

    @Test
    fun `each row wraps at its own length`() {
        val s = scanner(passLimit = 5)
        s.tick()   // row 1
        s.tick()   // row 2, which has only 2 cells
        s.select()
        s.tick()
        assertEquals(ScanState.Cell(row = 2, col = 1), s.state)
        s.tick()
        assertEquals(ScanState.Cell(row = 2, col = 0), s.state)
    }

    @Test
    fun `a row with one cell still works`() {
        val s = Scanner(ScanLayout(listOf(1)), rowPassLimit = 2)
        s.select() // enter the row
        assertEquals(ScanState.Cell(row = 0, col = 0), s.state)
        s.tick()   // one pass done
        assertEquals(ScanState.Cell(row = 0, col = 0), s.state)
        s.tick()   // two passes done, give up
        assertEquals(ScanState.Row(0), s.state)
    }

    // ---------------------------------------------------------------
    // Layouts that make no sense
    // ---------------------------------------------------------------

    @Test
    fun `a layout with no rows is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            ScanLayout(emptyList())
        }
    }

    @Test
    fun `a layout with an empty row is rejected`() {
        // An empty row can never be escaped by scanning across it.
        assertThrows(IllegalArgumentException::class.java) {
            ScanLayout(listOf(3, 0, 2))
        }
    }

    @Test
    fun `a pass limit below one is rejected`() {
        // Zero passes would mean entering a row and leaving it immediately,
        // making every cell unreachable.
        assertThrows(IllegalArgumentException::class.java) {
            Scanner(layout(), rowPassLimit = 0)
        }
    }
}
