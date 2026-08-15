package cat.merce.comunicador.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringReader

class NgramPredictorTest {

    /** A miniature model in the same format as the shipped asset. */
    private val modelText = listOf(
        "u\tque\t100",
        "u\tvull\t90",
        "u\taigua\t80",
        "u\tanar\t70",
        "u\tmés\t60",
        "u\tmenjar\t50",
        "u\tdemà\t40",
        "u\tdormir\t30",
        "b\tvull\taigua\t20\tanar\t15\tdormir\t10",
        "b\tdemà\tvull\t5",
    ).joinToString("\n")

    private fun model() = NgramModel.load(StringReader(modelText))

    /** Fixed openers, so the test does not depend on the shipped list. */
    private val openers = listOf("vull", "aigua")

    private fun predictor(personal: PersonalModel = PersonalModel()) =
        NgramPredictor(model(), personal, openers)

    // ---------------------------------------------------------------
    // Predicting the next word, which the old predictor could not do
    // ---------------------------------------------------------------

    @Test
    fun `after a word it offers what usually follows that word`() {
        assertEquals(
            listOf("aigua", "anar", "dormir"),
            predictor().predict("vull ", limit = 3)
        )
    }

    @Test
    fun `what follows is filtered by what she has started typing`() {
        // Only successors of 'vull' that also start with A.
        assertEquals(listOf("aigua", "anar"), predictor().predict("vull a", limit = 3))
    }

    @Test
    fun `an unknown previous word falls back to openers, not raw frequency`() {
        // 'que' is the commonest word in the model but nobody starts there.
        assertEquals(listOf("vull", "aigua", "que"), predictor().predict("xyz ", limit = 3))
    }

    @Test
    fun `with nothing typed at all it offers openers`() {
        assertEquals(listOf("vull", "aigua", "que"), predictor().predict("", limit = 3))
    }

    @Test
    fun `openers only apply when there is nothing to complete`() {
        // Typing D must not drag in an opener that does not start with D.
        assertEquals(listOf("demà", "dormir"), predictor().predict("d", limit = 2))
    }

    @Test
    fun `successors are topped up from the vocabulary when there are too few`() {
        // 'demà' has only one known successor, so the rest come from frequency.
        val suggestions = predictor().predict("demà ", limit = 3)
        assertEquals("vull", suggestions.first())
        assertEquals(3, suggestions.size)
    }

    @Test
    fun `a word is never suggested twice`() {
        val suggestions = predictor().predict("vull ", limit = 8)
        assertEquals(suggestions.size, suggestions.distinct().size)
    }

    // ---------------------------------------------------------------
    // Accents, which the grid cannot type
    // ---------------------------------------------------------------

    @Test
    fun `typing without an accent finds the accented word`() {
        assertEquals("més", predictor().predict("mes", limit = 1).single())
    }

    @Test
    fun `the previous word matches even when she typed it without accents`() {
        // She typed DEMA on the grid; the model knows it as demà.
        assertEquals("vull", predictor().predict("dema ", limit = 1).single())
    }

    @Test
    fun `matching ignores case, since the grid is upper case`() {
        assertEquals(listOf("aigua", "anar"), predictor().predict("VULL A", limit = 2))
    }

    // ---------------------------------------------------------------
    // Learning from her own writing
    // ---------------------------------------------------------------

    @Test
    fun `her own follow-on word is offered before the model's`() {
        val personal = PersonalModel()
        repeat(1) { personal.learn(previous = "vull", word = "dormir") }

        // The model would say aigua first. She says dormir, so she wins.
        assertEquals("dormir", predictor(personal).predict("vull ", limit = 3).first())
    }

    @Test
    fun `her own words are offered before common Catalan ones`() {
        val personal = PersonalModel()
        personal.learn(previous = "", word = "Mercè")

        assertEquals("mercè", predictor(personal).predict("me", limit = 3).first())
    }

    @Test
    fun `her own words are still filtered by the prefix`() {
        val personal = PersonalModel()
        personal.learn(previous = "", word = "dormir")

        // She has used 'dormir', but she is typing A, so it must not appear.
        assertTrue(predictor(personal).predict("a", limit = 3).none { it == "dormir" })
    }

    @Test
    fun `the word she just wrote is not offered as the next one`() {
        val personal = PersonalModel()
        // Writing 'vull' teaches the model that she uses it, which must not
        // then come back as a suggestion to follow itself.
        personal.learn(previous = "", word = "vull")

        assertTrue(predictor(personal).predict("vull ", limit = 3).none { it == "vull" })
    }

    @Test
    fun `the word she uses most comes first`() {
        val personal = PersonalModel()
        personal.learn("", "anar")
        repeat(3) { personal.learn("", "aigua") }

        assertEquals("aigua", predictor(personal).predict("a", limit = 2).first())
    }

    // ---------------------------------------------------------------
    // Surviving the tablet being switched off
    // ---------------------------------------------------------------

    @Test
    fun `what she has learned survives a save and reload`() {
        val original = PersonalModel()
        original.learn("vull", "dormir")
        original.learn("vull", "dormir")
        original.learn("", "mercè")

        val restored = PersonalModel.fromLines(original.toLines())

        assertEquals(listOf("dormir"), restored.successorsOf("vull"))
        assertEquals(listOf("dormir", "mercè"), restored.words())
    }

    @Test
    fun `a damaged line is skipped rather than losing everything`() {
        val restored = PersonalModel.fromLines(
            listOf("w\taigua\t3", "this is not a real line", "w\tbroken\tnotanumber")
        )
        assertEquals(listOf("aigua"), restored.words())
    }

    // ---------------------------------------------------------------
    // The shipped asset itself
    // ---------------------------------------------------------------

    @Test
    fun `an empty model still offers openers`() {
        val empty = NgramPredictor(NgramModel.load(StringReader("")), PersonalModel(), openers)
        assertEquals(listOf("vull", "aigua"), empty.predict("", limit = 3))
    }

    @Test
    fun `an empty model with no openers does not crash`() {
        val bare = NgramPredictor(NgramModel.load(StringReader("")), PersonalModel(), emptyList())
        assertEquals(emptyList<String>(), bare.predict("vull ", limit = 3))
    }

    @Test
    fun `rubbish in the model file is ignored`() {
        val messy = NgramModel.load(StringReader("u\tvull\t9\nnonsense\n\nb\tonly-two-fields"))
        assertEquals(1, messy.size)
    }
}
