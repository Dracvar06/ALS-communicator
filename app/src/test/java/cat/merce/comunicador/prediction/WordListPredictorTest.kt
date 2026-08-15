package cat.merce.comunicador.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class WordListPredictorTest {

    private val words = listOf("aigua", "ajuda", "més", "menjar", "metge", "demà", "ça")

    private fun predictor() = WordListPredictor(words)

    @Test
    fun `with nothing typed it offers the top of the list`() {
        assertEquals(listOf("aigua", "ajuda", "més"), predictor().predict("", limit = 3))
    }

    @Test
    fun `it matches on the start of the word`() {
        assertEquals(listOf("aigua", "ajuda"), predictor().predict("a", limit = 3))
    }

    @Test
    fun `it returns no more than asked for`() {
        assertEquals(listOf("més"), predictor().predict("m", limit = 1))
    }

    @Test
    fun `it may return fewer than asked for`() {
        assertEquals(listOf("demà"), predictor().predict("d", limit = 3))
    }

    @Test
    fun `a prefix that matches nothing gives nothing`() {
        assertEquals(emptyList<String>(), predictor().predict("zzz", limit = 3))
    }

    @Test
    fun `only the word being typed is matched, not the whole sentence`() {
        // The earlier words are finished. Suggestions are for the last one, so
        // 'vull' is ignored and only ME is matched. The two come back in list
        // order, which is the ranking.
        assertEquals(listOf("més", "menjar"), predictor().predict("vull ME", limit = 2))
    }

    @Test
    fun `a trailing space means a fresh word`() {
        assertEquals(listOf("aigua", "ajuda"), predictor().predict("vull ", limit = 2))
    }

    // ---------------------------------------------------------------
    // Accents, which the grid cannot type
    // ---------------------------------------------------------------

    @Test
    fun `typing without an accent finds the accented word`() {
        // There is no E-acute key, so this is the only way to reach 'més'.
        assertEquals(listOf("més"), predictor().predict("mes", limit = 1))
    }

    @Test
    fun `matching ignores case, since the grid is upper case`() {
        assertEquals(listOf("demà"), predictor().predict("DEM", limit = 3))
    }

    @Test
    fun `a c reaches a c-cedilla`() {
        assertEquals(listOf("ça"), predictor().predict("ca", limit = 3))
    }

    @Test
    fun `the suggestion keeps its accent`() {
        // The point of all this: she types plain letters and gets back a
        // correctly spelled Catalan word.
        assertEquals("demà", predictor().predict("dema", limit = 1).single())
    }

    // ---------------------------------------------------------------
    // Edges
    // ---------------------------------------------------------------

    @Test
    fun `asking for none gives none`() {
        assertEquals(emptyList<String>(), predictor().predict("a", limit = 0))
    }

    @Test
    fun `a negative limit is rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            predictor().predict("a", limit = -1)
        }
    }

    @Test
    fun `duplicates never fill two slots`() {
        val repeated = WordListPredictor(listOf("aigua", "aigua", "ajuda"))
        assertEquals(listOf("aigua", "ajuda"), repeated.predict("a", limit = 3))
    }

    @Test
    fun `the shipped list is usable`() {
        val real = WordListPredictor()
        assertEquals(3, real.predict("", limit = 3).size)
        assertTrue(real.predict("aj", limit = 3).contains("ajuda"))
    }
}
