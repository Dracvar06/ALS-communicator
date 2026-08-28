package cat.merce.comunicador.ui

import cat.merce.comunicador.scan.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Direct mode: she touches the letter she wants.
 *
 * Row 0 is the three suggestions, sí, no and frases. Row 1 is espai, A, E, S,
 * R, L. The last row ends with the clear key and backspace.
 */
class DirectModeTest {

    private fun direct() = ScanController(initialInputMode = InputMode.Direct)

    @Test
    fun `touching a letter writes it`() {
        val c = direct()
        c.touchKey(1, 1)
        assertEquals("A", c.text)
    }

    @Test
    fun `touching the same letter twice writes it twice`() {
        val c = direct()
        c.touchKey(1, 1)
        c.touchKey(1, 1)
        assertEquals("AA", c.text)
    }

    @Test
    fun `nothing is highlighted until she has touched something`() {
        val c = direct()
        assertNull(c.directLast)
        // A highlight sitting on a cell she has not chosen is the thing that
        // makes people believe the app is doing something it is not.
        assertFalse(c.isLit(0, 0))
    }

    @Test
    fun `the cell she touched is the one that lights up`() {
        val c = direct()
        c.touchKey(2, 3)
        assertEquals(Position(2, 3), c.directLast)
        assertTrue(c.isLit(2, 3))
        assertFalse(c.isLit(2, 2))
    }

    @Test
    fun `the highlight never moves on its own`() {
        val c = direct()
        c.touchKey(1, 1)
        repeat(20) { c.tick() }
        assertTrue(c.isLit(1, 1))
        assertEquals("A", c.text)
    }

    @Test
    fun `the grid carries a backspace key`() {
        val c = direct()
        // One touch per letter and no undo switch, so removing a letter has to
        // be a cell she can reach.
        assertTrue(c.rows.any { row -> row.any { it == Key.Delete } })
    }

    @Test
    fun `backspace removes one letter`() {
        val c = direct()
        c.touchKey(1, 1)
        c.touchKey(1, 2)
        assertEquals("AE", c.text)

        val row = c.rows.indexOfFirst { it.contains(Key.Delete) }
        c.touchKey(row, c.rows[row].indexOf(Key.Delete))
        assertEquals("A", c.text)
    }

    @Test
    fun `the clear key says so in words`() {
        val c = direct()
        // A bare cross meant nothing to the people who had to explain it.
        assertEquals("ESBORRA TOT", c.label(Key.Clear))

        c.changeLanguage(ENGLISH)
        assertEquals("CLEAR ALL", c.label(Key.Clear))
    }

    @Test
    fun `every language names the clear key`() {
        for (language in LANGUAGES) {
            assertTrue(language.displayName, language.clearLabel.length > 1)
        }
    }

    @Test
    fun `touching the phrases key opens the phrases screen`() {
        val c = direct()
        c.touchKey(0, c.rows[0].indexOf(Key.OpenPhrases))
        assertTrue(c.inPhrases)
        // The cell she touched belongs to the grid that has just gone away.
        assertNull(c.directLast)
    }

    @Test
    fun `a touch outside the grid is ignored rather than fatal`() {
        val c = direct()
        c.touchKey(99, 0)
        c.touchKey(0, 99)
        c.touchKey(-1, -1)
        assertEquals("", c.text)
    }

    @Test
    fun `touches do nothing while settings or the guide are open`() {
        val c = direct()
        c.openSettings()
        c.touchKey(1, 1)
        assertEquals("", c.text)

        c.closeSettings()
        c.openTutorial()
        c.touchKey(1, 1)
        assertEquals("", c.text)
    }

    @Test
    fun `the other modes ignore a touch on a letter`() {
        for (mode in listOf(InputMode.Scan, InputMode.Arrows)) {
            val c = ScanController(initialInputMode = mode)
            c.touchKey(1, 1)
            // There, the letters are for reading. A hand resting on the screen
            // must not type.
            assertEquals(mode.name, "", c.text)
        }
    }

    @Test
    fun `undo takes back a letter she touched`() {
        val c = direct()
        c.touchKey(1, 1)
        c.undo()
        assertEquals("", c.text)
    }
}
