package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The walkthrough a helper reads before explaining the app to her. */
class TutorialTest {

    @Test
    fun `it is closed to start with`() {
        assertFalse(ScanController().inTutorial)
    }

    @Test
    fun `opening it starts at the first page`() {
        val c = ScanController()
        c.nextTutorialPage()
        c.nextTutorialPage()
        c.openTutorial()
        // Opened again later, it starts from the beginning rather than from
        // wherever somebody left off weeks ago.
        assertEquals(0, c.tutorialPage)
        assertTrue(c.inTutorial)
    }

    @Test
    fun `it pages forwards and back`() {
        val c = ScanController()
        c.openTutorial()
        c.nextTutorialPage()
        assertEquals(1, c.tutorialPage)
        c.previousTutorialPage()
        assertEquals(0, c.tutorialPage)
    }

    @Test
    fun `back on the first page stays put`() {
        val c = ScanController()
        c.openTutorial()
        c.previousTutorialPage()
        assertEquals(0, c.tutorialPage)
    }

    @Test
    fun `going past the last page closes it`() {
        val c = ScanController()
        c.openTutorial()
        repeat(c.tutorialPages.size) { c.nextTutorialPage() }
        assertFalse(c.inTutorial)
    }

    @Test
    fun `every language has a walkthrough and every page has something on it`() {
        for (language in LANGUAGES) {
            assertTrue(language.displayName, language.tutorial.isNotEmpty())
            for (page in language.tutorial) {
                assertTrue(page.title, page.title.isNotEmpty())
                assertTrue(page.title, page.lines.isNotEmpty())
            }
        }
    }

    @Test
    fun `every language shows every picture`() {
        for (language in LANGUAGES) {
            val demos = language.tutorial.mapNotNull { it.demo }.toSet()
            // Both modes, because a helper shown only the one that happens to
            // be on cannot tell whether the other would suit her better; and
            // the touch halves, because that is the thing people actually get
            // wrong.
            assertEquals(
                language.displayName,
                TutorialDemo.entries.toSet(),
                demos,
            )
        }
    }

    @Test
    fun `the touch halves are explained before scanning is`() {
        for (language in LANGUAGES) {
            val touch = language.tutorial.indexOfFirst { it.demo == TutorialDemo.Touch }
            val scan = language.tutorial.indexOfFirst { it.demo == TutorialDemo.Scan }
            // The scanning page talks about touching the left half of the
            // screen, which means nothing to somebody who still believes the
            // letters are buttons.
            assertTrue(language.displayName, touch in 0 until scan)
        }
    }

    @Test
    fun `nothing scans while it is open`() {
        val c = ScanController()
        c.openTutorial()
        val before = c.state
        repeat(10) { c.tick() }
        assertEquals(before, c.state)
    }

    @Test
    fun `a switch press closes it rather than typing`() {
        val c = ScanController()
        c.openTutorial()
        c.press()
        // The same escape settings has: a screen she cannot leave without
        // somebody else's hands is a screen that can strand her.
        assertFalse(c.inTutorial)
        assertEquals("", c.text)
    }

    @Test
    fun `the undo switch closes it too`() {
        val c = ScanController()
        c.openTutorial()
        c.undo()
        assertFalse(c.inTutorial)
    }

    @Test
    fun `the arrows do nothing while it is open`() {
        val c = ScanController(initialInputMode = InputMode.Arrows)
        c.openTutorial()
        val before = c.state
        c.moveDown()
        c.moveRight()
        assertEquals(before, c.state)
    }

    @Test
    fun `closing it leaves settings as well`() {
        val c = ScanController()
        c.openSettings()
        c.openTutorial()
        c.closeTutorial()
        // The last page asks the helper to go and try it, so it puts them on
        // the grid rather than back in a settings screen.
        assertFalse(c.inSettings)
        assertFalse(c.inTutorial)
    }
}
