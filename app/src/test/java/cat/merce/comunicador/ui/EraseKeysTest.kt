package cat.merce.comunicador.ui

import cat.merce.comunicador.scan.ScanState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The two erase buttons on the arrow pad.
 *
 * Steering to the corner for a backspace costs several presses at the one
 * moment she is least happy with the app, so erasing gets its own buttons. The
 * cost of that is a destructive button within reach of a hand that cannot
 * always be placed, and in arrow mode there is no undo switch to take a wipe
 * back with — hence the second press.
 */
class EraseKeysTest {

    private fun arrows() = ScanController(initialInputMode = InputMode.Arrows)

    /** Writes a few letters the way she would, by steering and choosing. */
    private fun ScanController.write(times: Int) {
        repeat(times) {
            moveDown()
            moveRight()
            press()
        }
    }

    @Test
    fun `the erase buttons are on by default`() {
        assertTrue(arrows().eraseKeys)
    }

    @Test
    fun `backspace removes one letter`() {
        val c = arrows()
        c.write(2)
        val before = c.text
        assertEquals(2, before.length)
        c.eraseLetter()
        assertEquals(before.dropLast(1), c.text)
    }

    @Test
    fun `backspace on an empty sentence does nothing at all`() {
        val c = arrows()
        c.eraseLetter()
        assertEquals("", c.text)
        assertFalse(c.canUndo)
    }

    @Test
    fun `clear all needs a second press`() {
        val c = arrows()
        c.write(3)
        val written = c.text

        c.eraseAll()
        assertTrue("the first press only arms it", c.clearArmed)
        assertEquals("and must not touch the sentence", written, c.text)

        c.eraseAll()
        assertEquals("", c.text)
        assertFalse(c.clearArmed)
    }

    @Test
    fun `moving an arrow cancels the pending clear`() {
        val c = arrows()
        c.write(2)
        val written = c.text

        c.eraseAll()
        c.moveRight()
        assertFalse(c.clearArmed)

        // The next press of it arms again rather than wiping. This is the whole
        // point: a stray first press costs nothing.
        c.eraseAll()
        assertEquals(written, c.text)
    }

    @Test
    fun `choosing a letter cancels the pending clear`() {
        val c = arrows()
        c.write(1)
        c.eraseAll()
        c.write(1)
        assertFalse(c.clearArmed)
        assertEquals(2, c.text.length)
    }

    @Test
    fun `backspace cancels the pending clear`() {
        val c = arrows()
        c.write(2)
        c.eraseAll()
        c.eraseLetter()
        assertFalse(c.clearArmed)
        assertEquals(1, c.text.length)
    }

    @Test
    fun `clear all does not arm on an empty sentence`() {
        val c = arrows()
        c.eraseAll()
        assertFalse("nothing to confirm, so nothing to arm", c.clearArmed)
    }

    @Test
    fun `a wipe can be taken back with undo`() {
        val c = arrows()
        c.write(3)
        val written = c.text
        c.eraseAll()
        c.eraseAll()
        assertEquals("", c.text)
        c.undo()
        assertEquals(written, c.text)
    }

    @Test
    fun `a backspace can be taken back with undo`() {
        val c = arrows()
        c.write(2)
        val written = c.text
        c.eraseLetter()
        c.undo()
        assertEquals(written, c.text)
    }

    @Test
    fun `erasing does not move the highlight`() {
        val c = arrows()
        c.write(2)
        val where = c.state
        c.eraseLetter()
        assertEquals(where, c.state)
        c.eraseAll()
        c.eraseAll()
        assertEquals(where, c.state)
    }

    @Test
    fun `the buttons do nothing while they are turned off`() {
        val c = arrows()
        c.write(2)
        val written = c.text
        c.useEraseKeys(false)

        c.eraseLetter()
        c.eraseAll()
        c.eraseAll()
        assertEquals(written, c.text)
        assertFalse(c.clearArmed)
    }

    @Test
    fun `turning the buttons off disarms a pending clear`() {
        val c = arrows()
        c.write(1)
        c.eraseAll()
        c.useEraseKeys(false)
        assertFalse(c.clearArmed)
    }

    @Test
    fun `erasing is ignored on the phrases screen`() {
        val c = arrows()
        c.write(2)
        val written = c.text

        // Steer to frases and open it. The sentence is still there underneath,
        // and a stray press on the pad must not quietly empty it.
        val row = c.rows.indexOfFirst { it.contains(Key.OpenPhrases) }
        val col = c.rows[row].indexOf(Key.OpenPhrases)
        var guard = 0
        while ((c.state as ScanState.Cell).row != row && guard++ < 100) c.moveDown()
        while ((c.state as ScanState.Cell).col != col && guard++ < 100) c.moveRight()
        c.press()
        assertTrue(c.inPhrases)

        c.eraseLetter()
        c.eraseAll()
        c.eraseAll()
        assertEquals(written, c.text)
    }

    @Test
    fun `erasing is ignored while settings are open`() {
        val c = arrows()
        c.write(2)
        val written = c.text
        c.openSettings()
        c.eraseLetter()
        c.eraseAll()
        c.eraseAll()
        assertEquals(written, c.text)
    }

    @Test
    fun `opening settings disarms a pending clear`() {
        val c = arrows()
        c.write(1)
        c.eraseAll()
        c.openSettings()
        assertFalse(c.clearArmed)
    }

    @Test
    fun `erasing brings the first suggestions back`() {
        val c = arrows()
        c.write(1)
        val whileWriting = c.suggestions
        c.eraseLetter()
        assertEquals("", c.text)
        assertNotEquals(whileWriting, c.suggestions)
    }

    @Test
    fun `every language names both erase buttons and the confirmation`() {
        for (language in listOf(CATALAN, ENGLISH, SPANISH)) {
            assertTrue(language.arrowDeleteLetter.isNotBlank())
            assertTrue(language.arrowClearConfirm.isNotBlank())
            assertTrue(language.settingsEraseKeysTitle.isNotBlank())
            assertTrue(language.settingsEraseKeysDetail.isNotBlank())

            // The armed label has to read as a different button, or the second
            // press is indistinguishable from the first not having worked.
            assertNotEquals(language.clearLabel, language.arrowClearConfirm)
        }
    }

    @Test
    fun `the grid keeps its own erase keys`() {
        // The pad's buttons are a shortcut, not a replacement: switch users and
        // direct-touch users never see the pad at all.
        val c = arrows()
        assertTrue(c.rows.any { it.contains(Key.Clear) })
        assertTrue(c.rows.any { it.contains(Key.Delete) })
    }
}
