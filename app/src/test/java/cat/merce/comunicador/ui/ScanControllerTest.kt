package cat.merce.comunicador.ui

import cat.merce.comunicador.scan.ScanState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The undo switch.
 *
 * Row 0 of the grid is the three suggestions, sí, no and delete. Row 1 is
 * espai, A, E, S, R, L. The last row ends with the clear key.
 */
class ScanControllerTest {

    /** Walks the cursor to a row, opens it, and walks across to a cell. */
    private fun ScanController.choose(row: Int, col: Int) {
        repeat(row) { tick() }
        press()
        repeat(col) { tick() }
        press()
    }

    // ---------------------------------------------------------------
    // Undoing a row opened by mistake
    // ---------------------------------------------------------------

    @Test
    fun `undo leaves a row that was opened by mistake`() {
        val c = ScanController()
        c.tick()  // row 1
        c.press() // she meant row 2
        assertEquals(ScanState.Cell(row = 1, col = 0), c.state)

        c.undo()
        assertEquals(ScanState.Row(1), c.state)
    }

    @Test
    fun `after undoing back out, scanning carries on from that row`() {
        val c = ScanController()
        c.tick()
        c.press()
        c.undo()
        c.tick()
        // Not back to the top: she was heading somewhere near here.
        assertEquals(ScanState.Row(2), c.state)
    }

    // ---------------------------------------------------------------
    // Undoing a letter
    // ---------------------------------------------------------------

    @Test
    fun `undo removes the letter and puts her back inside that row`() {
        val c = ScanController()
        c.choose(row = 1, col = 1) // A
        assertEquals("A", c.text)

        c.undo()
        assertEquals("", c.text)
        // Back to choosing within the same row, so she can pick again without
        // waiting for the scan to come all the way round.
        assertEquals(ScanState.Cell(row = 1, col = 0), c.state)
    }

    @Test
    fun `a second undo then steps out of the row`() {
        val c = ScanController()
        c.choose(row = 1, col = 1)
        c.undo()
        c.undo()
        assertEquals(ScanState.Row(1), c.state)
    }

    @Test
    fun `undo walks back through several letters`() {
        val c = ScanController()
        c.choose(row = 1, col = 1) // A
        c.choose(row = 1, col = 2) // E
        assertEquals("AE", c.text)

        c.undo()
        assertEquals("A", c.text)
        c.undo() // out of the row
        c.undo()
        assertEquals("", c.text)
    }

    @Test
    fun `undo brings back a sentence that was cleared by accident`() {
        val c = ScanController()
        c.choose(row = 1, col = 1) // A
        c.choose(row = 5, col = 4) // the clear key
        assertEquals("", c.text)

        c.undo()
        assertEquals("A", c.text)
    }

    @Test
    fun `undo takes back a whole suggested word`() {
        val c = ScanController()
        c.choose(row = 0, col = 0) // the first suggestion
        assertTrue(c.text.isNotEmpty())

        c.undo()
        assertEquals("", c.text)
    }

    // ---------------------------------------------------------------
    // When there is nothing to undo
    // ---------------------------------------------------------------

    @Test
    fun `undo does nothing at all when there is nothing to undo`() {
        val c = ScanController()
        c.undo()
        assertEquals(ScanState.Row(0), c.state)
        assertEquals("", c.text)
        assertFalse(c.canUndo)
    }

    @Test
    fun `a row that gave up on its own is not somewhere undo returns to`() {
        val c = ScanController()
        c.press() // row 0, six cells
        repeat(12) { c.tick() } // two full passes, so the row releases her
        assertEquals(ScanState.Row(0), c.state)

        // She is already out. Undo must not put her back in.
        assertFalse(c.canUndo)
        c.undo()
        assertEquals(ScanState.Row(0), c.state)
    }

    @Test
    fun `canUndo says whether the button would do anything`() {
        val c = ScanController()
        assertFalse(c.canUndo)
        c.press()
        assertTrue(c.canUndo)
        c.undo()
        assertFalse(c.canUndo)
    }

    // ---------------------------------------------------------------
    // Undo and what the app has learned about her
    // ---------------------------------------------------------------

    @Test
    fun `a word taken back is also taken out of what was learned`() {
        val c = ScanController()
        var learned: Pair<String, String>? = null
        var forgotten: Pair<String, String>? = null
        c.onWordFinished = { previous, word -> learned = previous to word }
        c.onWordUndone = { previous, word -> forgotten = previous to word }

        c.choose(row = 0, col = 0) // a suggestion, which finishes a word
        assertEquals(c.text.trim(), learned?.second)

        c.undo()
        assertEquals(learned, forgotten)
    }

    // ---------------------------------------------------------------
    // Settings, which a carer opens by touch
    // ---------------------------------------------------------------

    @Test
    fun `either switch closes settings`() {
        val c = ScanController()

        c.openSettings()
        assertTrue(c.inSettings)
        c.press()
        assertFalse(c.inSettings)

        c.openSettings()
        c.undo()
        assertFalse(c.inSettings)
    }

    @Test
    fun `nothing scans while settings are open`() {
        val c = ScanController()
        c.openSettings()
        repeat(5) { c.tick() }
        assertEquals(ScanState.Row(0), c.state)
    }

    @Test
    fun `closing settings starts again from the top`() {
        val c = ScanController()
        c.tick()
        c.tick()
        c.openSettings()
        c.closeSettings()
        assertEquals(ScanState.Row(0), c.state)
    }

    @Test
    fun `the speed slider cannot be dragged past its limits`() {
        val c = ScanController()

        c.changeInterval(1)
        assertEquals(ScanController.MIN_INTERVAL_MS, c.scanIntervalMs)

        c.changeInterval(999_999)
        assertEquals(ScanController.MAX_INTERVAL_MS, c.scanIntervalMs)
    }

    @Test
    fun `a speed change is reported so it can be saved`() {
        val c = ScanController()
        var saved: Long? = null
        c.onIntervalChanged = { saved = it }

        c.changeInterval(1500)
        assertEquals(1500L, saved)
    }

    // ---------------------------------------------------------------
    // Touch input, for trying it without a switch box
    // ---------------------------------------------------------------

    @Test
    fun `turning touch input off is reported so it can be saved`() {
        val c = ScanController(initialTouchInput = true)
        var saved: Boolean? = null
        c.onTouchInputChanged = { saved = it }

        c.useTouchInput(false)
        assertFalse(c.touchInput)
        assertEquals(false, saved)
    }

    @Test
    fun `setting touch input to what it already is changes nothing`() {
        val c = ScanController(initialTouchInput = false)
        var reported = false
        c.onTouchInputChanged = { reported = true }

        c.useTouchInput(false)
        assertFalse(reported)
    }

    @Test
    fun `the tap halves drive the same writing and undo as the switches`() {
        // The screen taps call exactly press and undo, so touch and switches
        // are the same two actions and cannot drift apart.
        val c = ScanController()
        c.press() // right half: open row 0
        c.press() // right half: choose its first cell
        assertTrue(c.text.isNotEmpty())

        c.undo()  // left half
        assertEquals("", c.text)
    }
}
