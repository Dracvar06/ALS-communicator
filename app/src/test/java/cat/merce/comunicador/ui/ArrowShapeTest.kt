package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** How the arrows are drawn, and the settings that survive around it. */
class ArrowShapeTest {

    @Test
    fun `an unknown or missing setting falls back to the separated arrows`() {
        // Whatever is stored on a device that has never seen this setting, it
        // has to open on something. Separate, because the joined triangles are
        // what turned out to be too small to aim at.
        assertEquals(ArrowShape.Separate, ArrowShape.fromCode(null))
        assertEquals(ArrowShape.Separate, ArrowShape.fromCode("Nonsense"))
    }

    @Test
    fun `every shape can be stored and read back`() {
        for (shape in ArrowShape.entries) {
            assertEquals(shape, ArrowShape.fromCode(shape.name))
        }
    }

    @Test
    fun `the shape can be changed and reported`() {
        val c = ScanController(initialInputMode = InputMode.Arrows)
        var saved: ArrowShape? = null
        c.onArrowShapeChanged = { saved = it }

        c.useArrowShape(ArrowShape.Joined)
        assertEquals(ArrowShape.Joined, c.arrowShape)
        assertEquals(ArrowShape.Joined, saved)
    }

    @Test
    fun `setting the shape it already has reports nothing`() {
        val c = ScanController(initialInputMode = InputMode.Arrows)
        var calls = 0
        c.onArrowShapeChanged = { calls++ }
        c.useArrowShape(c.arrowShape)
        assertEquals(0, calls)
    }

    @Test
    fun `the shape does not change what the arrows do`() {
        // It is drawing and nothing else. If this ever fails, something has
        // leaked from how the pad looks into how the pad works.
        val joined = ScanController(initialInputMode = InputMode.Arrows)
        joined.useArrowShape(ArrowShape.Joined)
        val separate = ScanController(initialInputMode = InputMode.Arrows)
        separate.useArrowShape(ArrowShape.Separate)

        for (controller in listOf(joined, separate)) {
            controller.moveDown()
            controller.moveRight()
            controller.press()
        }
        assertEquals(joined.text, separate.text)
    }

    @Test
    fun `every language names both shapes and both new switches`() {
        for (language in listOf(CATALAN, ENGLISH, SPANISH)) {
            assertTrue(language.settingsArrowShapeTitle.isNotBlank())
            assertTrue(language.settingsArrowShapeDetail.isNotBlank())
            assertNotEquals(
                language.settingsArrowShapeJoined,
                language.settingsArrowShapeSeparate,
            )
            assertTrue(language.settingsForgiveTitle.isNotBlank())
            assertTrue(language.settingsForgiveDetail.isNotBlank())
            assertTrue(language.settingsBoldWritingTitle.isNotBlank())
            assertTrue(language.settingsBoldWritingDetail.isNotBlank())
        }
    }

    @Test
    fun `both new switches default on and report their changes`() {
        val c = ScanController()
        assertTrue(c.forgiveMistakes)
        assertTrue(c.boldWriting)

        var forgiving: Boolean? = null
        var bold: Boolean? = null
        c.onForgiveMistakesChanged = { forgiving = it }
        c.onBoldWritingChanged = { bold = it }

        c.useForgiveMistakes(false)
        c.useBoldWriting(false)
        assertEquals(false, forgiving)
        assertEquals(false, bold)
    }
}
