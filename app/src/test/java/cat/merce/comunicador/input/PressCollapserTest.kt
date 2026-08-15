package cat.merce.comunicador.input

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class PressCollapserTest {

    private fun collapser(quiet: Long = 120) = PressCollapser(quietMs = quiet)

    @Test
    fun `the first event is a press`() {
        assertTrue(collapser().isNewPress("s:313", atMillis = 0))
    }

    @Test
    fun `one squeeze of an analog trigger is one press`() {
        // What a Stadia trigger actually does: report all the way through the
        // travel, every few milliseconds.
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("s:313", 1000))

        var time = 1008L
        while (time < 1400) {
            assertFalse(c.isNewPress("s:313", time))
            time += 8
        }
    }

    @Test
    fun `letting go and squeezing again is a second press`() {
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("s:313", 1000))
        assertFalse(c.isNewPress("s:313", 1050))
        // Released: nothing reported for longer than the quiet period.
        assertTrue(c.isNewPress("s:313", 1400))
    }

    @Test
    fun `the quiet period is measured from the last event, not the first`() {
        // Otherwise a long squeeze would start counting as a new press part
        // way through, and one pull would type twice.
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("s:313", 0))
        var time = 50L
        repeat(20) {
            assertFalse(c.isNewPress("s:313", time))
            time += 50
        }
    }

    @Test
    fun `each input is judged on its own`() {
        // Squeezing one trigger must not swallow a press of the other.
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("s:313", 1000))
        assertTrue(c.isNewPress("s:312", 1005))
        assertFalse(c.isNewPress("s:313", 1010))
        assertFalse(c.isNewPress("s:312", 1015))
    }

    @Test
    fun `an ordinary button is unaffected`() {
        // A plain switch reports once per press, so nothing is collapsed.
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("k:62", 0))
        assertTrue(c.isNewPress("k:62", 500))
        assertTrue(c.isNewPress("k:62", 1000))
    }

    @Test
    fun `a clock that jumps backwards does not swallow a press`() {
        val c = collapser(quiet = 120)
        assertTrue(c.isNewPress("k:62", 10_000))
        assertTrue(c.isNewPress("k:62", 5))
    }

    @Test
    fun `a negative quiet period is rejected`() {
        assertThrows(IllegalArgumentException::class.java) { PressCollapser(quietMs = -1) }
    }
}
