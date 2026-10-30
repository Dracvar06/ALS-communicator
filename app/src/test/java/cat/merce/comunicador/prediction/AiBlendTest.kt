package cat.merce.comunicador.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AiBlendTest {

    @Test
    fun `no scores keeps the n-gram order`() {
        assertEquals(listOf("a", "b", "c"), AiBlend.reorder(listOf("a", "b", "c"), emptyMap()))
    }

    @Test
    fun `a much likelier word moves up`() {
        val order = AiBlend.reorder(
            listOf("llum", "llit", "llibre"),
            mapOf("llum" to -8.0, "llit" to -1.0, "llibre" to -9.0),
        )
        assertEquals("llit", order.first())
    }

    @Test
    fun `a slightly likelier word does not jump the n-gram's first choice`() {
        // log(2) * 3 is about 2.1, so a 1.0 lead is not enough to swap
        val order = AiBlend.reorder(listOf("vull", "vols"), mapOf("vull" to -2.0, "vols" to -1.0))
        assertEquals(listOf("vull", "vols"), order)
    }

    @Test
    fun `unscored words stay behind scored ones, in their own order`() {
        val order = AiBlend.reorder(listOf("x", "y", "z"), mapOf("z" to -3.0))
        assertEquals(listOf("z", "x", "y"), order)
    }

    @Test
    fun `nothing is added or lost`() {
        val words = listOf("aigua", "ajuda", "avui", "ara")
        val order = AiBlend.reorder(words, mapOf("ara" to -1.0, "avui" to -4.0))
        assertEquals(words.sorted(), order.sorted())
    }

    @Test
    fun `her own words at the top never move`() {
        val order = AiBlend.reorder(
            listOf("mercè", "llum", "llit"),
            mapOf("mercè" to -20.0, "llum" to -8.0, "llit" to -1.0),
            keepFirst = 1,
        )
        assertEquals(listOf("mercè", "llit", "llum"), order)
    }

    @Test
    fun `the start of a message is left to the n-gram`() {
        assertFalse(AiBlend.worthAsking(""))
        assertFalse(AiBlend.worthAsking("VU"))
        assertTrue(AiBlend.worthAsking("VULL "))
        assertTrue(AiBlend.worthAsking("VULL AN"))
    }

    @Test
    fun `previous words exclude the one being typed`() {
        assertEquals(listOf("vull", "anar"), AiBlend.previousWords("VULL ANAR AL"))
        assertEquals(listOf("vull", "anar", "al"), AiBlend.previousWords("VULL ANAR AL "))
    }
}
