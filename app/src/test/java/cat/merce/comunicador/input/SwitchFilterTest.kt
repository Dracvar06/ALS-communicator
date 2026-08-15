package cat.merce.comunicador.input

import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Times are milliseconds on a made up clock. Nothing here waits for anything,
 * which is why a bouncing switch can be tested on a laptop.
 */
class SwitchFilterTest {

    private fun filter(debounce: Long = 150, settle: Long = 0) =
        SwitchFilter(debounceMs = debounce, settleMs = settle)

    // ---------------------------------------------------------------
    // Contact bounce
    // ---------------------------------------------------------------

    @Test
    fun `the first press is always accepted`() {
        assertTrue(filter().accept(Switch.Write, atMillis = 0))
    }

    @Test
    fun `a bounce a few milliseconds later is not a second press`() {
        val f = filter()
        assertTrue(f.accept(Switch.Write, 1000))
        assertFalse(f.accept(Switch.Write, 1003))
        assertFalse(f.accept(Switch.Write, 1009))
        assertFalse(f.accept(Switch.Write, 1021))
    }

    @Test
    fun `a real second press later is accepted`() {
        val f = filter(debounce = 150)
        assertTrue(f.accept(Switch.Write, 1000))
        assertTrue(f.accept(Switch.Write, 1150))
    }

    @Test
    fun `the window opens exactly when it should`() {
        val f = filter(debounce = 150)
        f.accept(Switch.Write, 1000)
        assertFalse(f.accept(Switch.Write, 1149))
        assertTrue(f.accept(Switch.Write, 1150))
    }

    @Test
    fun `a deliberate quick pair still works`() {
        // Opening a row and taking its first cell is two real presses close
        // together. Debounce must not eat the second one.
        val f = filter(debounce = 150)
        assertTrue(f.accept(Switch.Write, 0))
        assertTrue(f.accept(Switch.Write, 300))
    }

    // ---------------------------------------------------------------
    // The failure that would matter most
    // ---------------------------------------------------------------

    @Test
    fun `a switch that bounces without stopping does not lock her out`() {
        // Rejected presses must not push the window along, or a failing switch
        // would leave her unable to say anything at all.
        val f = filter(debounce = 150)
        assertTrue(f.accept(Switch.Write, 0))

        var time = 10L
        while (time < 150) {
            assertFalse(f.accept(Switch.Write, time))
            time += 10
        }

        // The window still opens on time, measured from the accepted press.
        assertTrue(f.accept(Switch.Write, 150))
    }

    @Test
    fun `zero debounce lets everything through`() {
        val f = filter(debounce = 0)
        assertTrue(f.accept(Switch.Write, 0))
        assertTrue(f.accept(Switch.Write, 0))
    }

    // ---------------------------------------------------------------
    // The two switches against each other
    // ---------------------------------------------------------------

    @Test
    fun `by default undo is not blocked by having just written`() {
        // Writing a letter and immediately undoing it is exactly what the undo
        // switch is for, so nothing may stand in the way of it.
        val f = filter(debounce = 150, settle = 0)
        assertTrue(f.accept(Switch.Write, 1000))
        assertTrue(f.accept(Switch.Undo, 1001))
    }

    @Test
    fun `a settle window blocks the other switch too`() {
        val f = filter(debounce = 150, settle = 400)
        assertTrue(f.accept(Switch.Write, 1000))
        assertFalse(f.accept(Switch.Undo, 1300))
        assertTrue(f.accept(Switch.Undo, 1400))
    }

    @Test
    fun `a settle window longer than debounce also governs the same switch`() {
        val f = filter(debounce = 150, settle = 400)
        assertTrue(f.accept(Switch.Write, 1000))
        assertFalse(f.accept(Switch.Write, 1200))
        assertTrue(f.accept(Switch.Write, 1400))
    }

    @Test
    fun `each switch is debounced in its own right`() {
        val f = filter(debounce = 150, settle = 0)
        assertTrue(f.accept(Switch.Undo, 0))
        assertFalse(f.accept(Switch.Undo, 50))
        assertTrue(f.accept(Switch.Write, 60))
    }

    // ---------------------------------------------------------------
    // Odd clocks and bad settings
    // ---------------------------------------------------------------

    @Test
    fun `a clock that jumps backwards does not lock her out`() {
        val f = filter(debounce = 150)
        assertTrue(f.accept(Switch.Write, 10_000))
        assertTrue(f.accept(Switch.Write, 5))
    }

    // ---------------------------------------------------------------
    // Tremor mode: every press restarts the window
    // ---------------------------------------------------------------

    @Test
    fun `in tremor mode a burst of presses counts only once`() {
        val f = SwitchFilter(debounceMs = 400, restartOnReject = true)
        assertTrue(f.accept(Switch.Write, 0))     // first counts
        assertFalse(f.accept(Switch.Write, 200))  // within window, rejected, restarts
        assertFalse(f.accept(Switch.Write, 350))  // within window of 200, rejected, restarts
        assertFalse(f.accept(Switch.Write, 500))  // within window of 350, rejected
        // Only after a full quiet window from the last press does one count.
        assertTrue(f.accept(Switch.Write, 900))
    }

    @Test
    fun `without tremor mode the window is measured from the accepted press`() {
        val f = SwitchFilter(debounceMs = 400, restartOnReject = false)
        assertTrue(f.accept(Switch.Write, 0))
        assertFalse(f.accept(Switch.Write, 200))  // rejected, does not restart
        assertFalse(f.accept(Switch.Write, 350))
        // 400ms after the accepted press at 0, so this counts despite the burst.
        assertTrue(f.accept(Switch.Write, 400))
    }

    @Test
    fun `negative windows are rejected`() {
        assertThrows(IllegalArgumentException::class.java) { SwitchFilter(debounceMs = -1) }
        assertThrows(IllegalArgumentException::class.java) { SwitchFilter(settleMs = -1) }
    }
}
