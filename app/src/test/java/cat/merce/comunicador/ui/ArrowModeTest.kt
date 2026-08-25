package cat.merce.comunicador.ui

import cat.merce.comunicador.scan.ScanState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Arrow mode: she steers the highlight herself.
 *
 * Row 0 is the three suggestions, sí, no and frases. Row 1 is espai, A, E, S,
 * R, L. The last row ends with the clear key and, in this mode only, backspace.
 */
class ArrowModeTest {

    private fun arrows() = ScanController(initialInputMode = InputMode.Arrows)

    /** Steers to a cell from wherever the highlight is, the way she would. */
    private fun ScanController.steerTo(row: Int, col: Int) {
        // Both moves wrap, so this always arrives; the bound is only there so a
        // mistake in a test fails rather than hangs.
        var guard = 0
        while ((state as ScanState.Cell).row != row && guard++ < 100) moveDown()
        while ((state as ScanState.Cell).col != col && guard++ < 100) moveRight()
        assertEquals(ScanState.Cell(row, col), state)
    }

    /** Where the backspace key ended up on the current grid. */
    private fun ScanController.deleteAt(): Pair<Int, Int> {
        val row = rows.indexOfFirst { it.contains(Key.Delete) }
        return row to rows[row].indexOf(Key.Delete)
    }

    @Test
    fun `starts on a single cell rather than a whole row`() {
        // Nothing scans here, so there is no row-highlighting stage to be in.
        assertEquals(ScanState.Cell(row = 0, col = 0), arrows().state)
    }

    @Test
    fun `the highlight does not move on its own`() {
        val c = arrows()
        repeat(20) { c.tick() }
        assertEquals(ScanState.Cell(row = 0, col = 0), c.state)
    }

    @Test
    fun `the arrows move the highlight`() {
        val c = arrows()
        c.moveDown()
        c.moveRight()
        assertEquals(ScanState.Cell(row = 1, col = 1), c.state)
    }

    @Test
    fun `a press takes whatever the highlight is on`() {
        val c = arrows()
        c.moveDown()
        c.moveRight() // A
        c.press()
        assertEquals("A", c.text)
    }

    @Test
    fun `the highlight stays put after a press`() {
        val c = arrows()
        c.moveDown()
        c.moveRight()
        c.press()
        c.press()

        // Doubled letters cost one press, not a journey back across the grid.
        assertEquals("AA", c.text)
        assertEquals(ScanState.Cell(row = 1, col = 1), c.state)
    }

    @Test
    fun `the grid carries a backspace key`() {
        val c = arrows()
        val hasDelete = c.rows.any { row -> row.any { it == Key.Delete } }
        // With one button and no undo switch, removing a letter has to be
        // reachable on the grid or it cannot be done at all.
        assertTrue(hasDelete)
    }

    @Test
    fun `backspace removes the last letter`() {
        val c = arrows()
        c.moveDown()
        c.moveRight()
        c.press()
        c.press()
        assertEquals("AA", c.text)

        val (deleteRow, deleteCol) = c.deleteAt()
        c.steerTo(deleteRow, deleteCol)
        c.press()

        assertEquals("A", c.text)
    }

    @Test
    fun `backspace on an empty sentence does nothing`() {
        val c = arrows()
        val (deleteRow, deleteCol) = c.deleteAt()
        c.steerTo(deleteRow, deleteCol)
        c.press()
        assertEquals("", c.text)
    }

    @Test
    fun `scanning mode has no backspace key`() {
        // It would be a wasted cell there: the undo switch already does it.
        val c = ScanController()
        assertTrue(c.rows.none { row -> row.any { it == Key.Delete } })
    }

    @Test
    fun `undo takes back a letter without moving the highlight`() {
        val c = arrows()
        c.moveDown()
        c.moveRight()
        c.press()
        assertEquals("A", c.text)

        c.undo()
        assertEquals("", c.text)
        // She is still where she was, which is where she will try again from.
        assertEquals(ScanState.Cell(row = 1, col = 1), c.state)
    }

    @Test
    fun `switching to arrows from scanning rebuilds the grid and starts afresh`() {
        val c = ScanController()
        c.tick()
        c.press() // inside row 1
        assertTrue(c.state is ScanState.Cell)

        c.useInputMode(InputMode.Arrows)
        assertEquals(ScanState.Cell(row = 0, col = 0), c.state)
        assertTrue(c.rows.any { row -> row.any { it == Key.Delete } })
    }

    @Test
    fun `switching back to scanning drops the backspace key`() {
        val c = arrows()
        c.useInputMode(InputMode.Scan)
        assertTrue(c.rows.none { row -> row.any { it == Key.Delete } })
        assertEquals(ScanState.Row(0), c.state)
    }

    @Test
    fun `the highlight cannot be left off the end of a rebuilt grid`() {
        val c = arrows()
        // The far corner of the writing grid, which the phrases grid may not
        // have at all.
        c.steerTo(c.rows.size - 1, c.rows.last().size - 1)
        c.openPhrases()

        val position = c.state as ScanState.Cell
        assertTrue(position.row < c.rows.size)
        assertTrue(position.col < c.rows[position.row].size)
    }

    @Test
    fun `the phrases key opens the phrases screen`() {
        val c = arrows()
        val col = c.rows[0].indexOf(Key.OpenPhrases)
        assertNotEquals(-1, col)
        c.steerTo(0, col)
        c.press()
        assertTrue(c.inPhrases)
    }

    @Test
    fun `changing language keeps the backspace key`() {
        val c = arrows()
        c.changeLanguage(ENGLISH)
        assertTrue(c.rows.any { row -> row.any { it == Key.Delete } })
    }

    @Test
    fun `settings freezes the arrows`() {
        val c = arrows()
        c.openSettings()
        c.moveDown()
        c.moveRight()
        assertEquals(ScanState.Cell(row = 0, col = 0), c.state)
    }

    @Test
    fun `the pad starts on the right and can be moved`() {
        val c = arrows()
        assertEquals(ArrowPlacement.Right, c.arrowPlacement)

        c.useArrowPlacement(ArrowPlacement.BottomRight)
        assertEquals(ArrowPlacement.BottomRight, c.arrowPlacement)
    }

    @Test
    fun `moving the pad is reported once, so it can be saved`() {
        val c = arrows()
        var saved: ArrowPlacement? = null
        var times = 0
        c.onArrowPlacementChanged = { saved = it; times++ }

        c.useArrowPlacement(ArrowPlacement.BottomRight)
        c.useArrowPlacement(ArrowPlacement.BottomRight)

        assertEquals(ArrowPlacement.BottomRight, saved)
        assertEquals(1, times)
    }

    @Test
    fun `an unknown saved placement falls back to the right`() {
        // A settings file written by a newer version, or a corrupted one.
        // Landing on a side she can reach beats refusing to start.
        assertEquals(ArrowPlacement.Right, ArrowPlacement.fromCode("Sideways"))
        assertEquals(ArrowPlacement.Right, ArrowPlacement.fromCode(null))
    }

    @Test
    fun `the strip that used to be called Bottom still opens`() {
        // Saved before the strip could be turned round. Somebody who had
        // already chosen it must not be dropped back to a side column.
        assertEquals(ArrowPlacement.BottomRight, ArrowPlacement.fromCode("Bottom"))
        assertTrue(ArrowPlacement.fromCode("Bottom").alongTheBottom)
    }

    @Test
    fun `only the strip placements are along the bottom`() {
        assertTrue(ArrowPlacement.BottomLeft.alongTheBottom)
        assertTrue(ArrowPlacement.BottomRight.alongTheBottom)
        assertFalse(ArrowPlacement.Right.alongTheBottom)
        assertFalse(ArrowPlacement.Left.alongTheBottom)
    }

    @Test
    fun `arrowsFirst says which end the arrows are drawn at`() {
        assertTrue(ArrowPlacement.BottomLeft.arrowsFirst)
        assertFalse(ArrowPlacement.BottomRight.arrowsFirst)
    }

    @Test
    fun `moving the pad does not disturb what she has written`() {
        val c = arrows()
        c.moveDown()
        c.moveRight()
        c.press()
        assertEquals("A", c.text)

        c.useArrowPlacement(ArrowPlacement.BottomRight)

        // Purely where the buttons are drawn. Nothing about the grid, the
        // sentence or where the highlight is should move with them.
        assertEquals("A", c.text)
        assertEquals(cat.merce.comunicador.scan.ScanState.Cell(1, 1), c.state)
    }

    @Test
    fun `battery starts unknown and takes the reading it is given`() {
        val c = arrows()
        assertEquals(null, c.batteryPercent)

        c.setBattery(42, charging = true)
        assertEquals(42, c.batteryPercent)
        assertTrue(c.batteryCharging)
    }

    @Test
    fun `a nonsense battery reading is clamped rather than shown`() {
        val c = arrows()
        c.setBattery(140, charging = false)
        assertEquals(100, c.batteryPercent)
    }
}
