package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The two cells sí and no sit in.
 *
 * They are only ever an answer, so they are only ever the first thing in a
 * sentence. Once she is a letter in, they are lent to prediction for the rest
 * of it — five suggested words instead of three, on the fastest row of the grid.
 */
class SuggestionSlotsTest {

    private fun direct() = ScanController(initialInputMode = InputMode.Direct)

    private fun ScanController.topRow() = rows[0]

    @Test
    fun `an empty sentence keeps yes and no`() {
        val c = direct()
        assertTrue(c.topRow().contains(Key.Yes))
        assertTrue(c.topRow().contains(Key.No))
    }

    @Test
    fun `writing a letter lends those two cells to prediction`() {
        val c = direct()
        c.touchKey(1, 1) // A

        val top = c.topRow()
        assertTrue(top.none { it == Key.Yes })
        assertTrue(top.none { it == Key.No })
        assertTrue(top.contains(Key.Suggestion(3)))
        assertTrue(top.contains(Key.Suggestion(4)))
    }

    @Test
    fun `the two cells come back when the sentence is emptied`() {
        val c = direct()
        c.touchKey(1, 1)
        assertTrue(c.topRow().none { it == Key.Yes })

        c.undo()
        assertEquals("", c.text)
        // She has answered whatever was asked and is ready for the next thing.
        assertTrue(c.topRow().contains(Key.Yes))
        assertTrue(c.topRow().contains(Key.No))
    }

    @Test
    fun `the grid never changes shape`() {
        val c = direct()
        val before = c.rows.map { it.size }
        c.touchKey(1, 1)
        // Whatever the cells say, the scanner and the cursor must be working
        // over exactly the same shape. Nothing may move under her.
        assertEquals(before, c.rows.map { it.size })
    }

    @Test
    fun `yes and no keep their positions, only their meaning changes`() {
        val c = direct()
        val yesAt = c.topRow().indexOf(Key.Yes)
        val noAt = c.topRow().indexOf(Key.No)

        c.touchKey(1, 1)

        assertEquals(Key.Suggestion(3), c.topRow()[yesAt])
        assertEquals(Key.Suggestion(4), c.topRow()[noAt])
    }

    @Test
    fun `five words are offered once she is writing`() {
        val c = direct()
        c.touchKey(1, 1) // A, which most models have plenty of follow-ons for
        assertTrue(
            "offered ${c.suggestions}",
            c.suggestions.size > SUGGESTION_SLOTS,
        )
        assertTrue(c.suggestions.size <= SUGGESTION_SLOTS + EXTRA_SUGGESTION_SLOTS)
    }

    @Test
    fun `an empty sentence offers only three`() {
        val c = direct()
        assertTrue(c.suggestions.size <= SUGGESTION_SLOTS)
    }

    @Test
    fun `the phrases key is untouched by any of this`() {
        val c = direct()
        assertTrue(c.topRow().contains(Key.OpenPhrases))
        c.touchKey(1, 1)
        assertTrue(c.topRow().contains(Key.OpenPhrases))
    }

    @Test
    fun `touching a lent cell writes the word it is offering`() {
        val c = direct()
        c.touchKey(1, 1)

        val slot = c.topRow().indexOfFirst { it == Key.Suggestion(3) }
        val offered = c.label(Key.Suggestion(3))
        c.touchKey(0, slot)

        if (offered.isNotEmpty()) {
            assertEquals("$offered ", c.text)
        } else {
            // An empty slot is a wasted touch, never a crash.
            assertEquals("A", c.text)
        }
    }

    @Test
    fun `the lent cells work while scanning too`() {
        val c = ScanController()
        c.tick(); c.press()          // into the letter row
        c.tick(); c.press()          // take a letter
        assertNotEquals("", c.text)

        val top = c.rows[0]
        assertTrue(top.contains(Key.Suggestion(3)))
        assertTrue(top.contains(Key.Suggestion(4)))
    }
}
