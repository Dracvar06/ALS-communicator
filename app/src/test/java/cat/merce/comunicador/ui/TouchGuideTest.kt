package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The drawn-in write and undo halves that fade as they are learnt. */
class TouchGuideTest {

    @Test
    fun `not drawn unless asked for, so a known screen does not change`() {
        assertEquals(0f, ScanController().touchGuideStrength)
    }

    @Test
    fun `fades a step with every touch until it is gone`() {
        val c = ScanController(initialTouchGuideLeft = ScanController.TOUCH_GUIDE_TOUCHES)
        assertEquals(1f, c.touchGuideStrength)
        c.noteHalfTouched()
        assertTrue(c.touchGuideStrength < 1f)
        repeat(ScanController.TOUCH_GUIDE_TOUCHES + 5) { c.noteHalfTouched() }
        assertEquals(0f, c.touchGuideStrength)
        assertEquals(0, c.touchGuideLeft)
    }

    @Test
    fun `settings can bring it back at full strength`() {
        val c = ScanController()
        c.showTouchGuide()
        assertEquals(1f, c.touchGuideStrength)
    }
}
