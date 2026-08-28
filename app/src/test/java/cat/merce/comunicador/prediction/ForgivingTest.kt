package cat.merce.comunicador.prediction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.StringReader

/**
 * Suggestions that survive a mistake.
 *
 * A press that lands twice, or not at all, or on the neighbouring cell, used to
 * empty the suggestion row completely — at the exact moment the row is worth
 * most, because picking the finished word is how she gets out of the mistake
 * without erasing back to it.
 */
class ForgivingTest {

    private fun model(vararg words: String): NgramModel =
        NgramModel.load(StringReader(words.joinToString("\n") { "u\t$it\t1" }))

    private fun predictor(vararg words: String) =
        NgramPredictor(model(*words), openers = emptyList())

    // --- the distance itself ---

    @Test
    fun `an exact beginning is no distance at all`() {
        assertEquals(0, Words.prefixDistance("aju", "ajuda", max = 2))
    }

    @Test
    fun `a whole word is still no distance from itself`() {
        assertEquals(0, Words.prefixDistance("ajuda", "ajuda", max = 2))
    }

    @Test
    fun `a letter typed twice is one mistake`() {
        assertEquals(1, Words.prefixDistance("ajju", "ajuda", max = 2))
        assertEquals(1, Words.prefixDistance("aaju", "ajuda", max = 2))
    }

    @Test
    fun `a letter that never landed is one mistake`() {
        assertEquals(1, Words.prefixDistance("aud", "ajuda", max = 2))
    }

    @Test
    fun `a letter that landed on the wrong cell is one mistake`() {
        assertEquals(1, Words.prefixDistance("akud", "ajuda", max = 2))
    }

    @Test
    fun `an unrelated word is far away`() {
        assertTrue(Words.prefixDistance("telefon", "ajuda", max = 2) > 2)
    }

    @Test
    fun `the empty fragment is at no distance from anything`() {
        assertEquals(0, Words.prefixDistance("", "ajuda", max = 2))
    }

    @Test
    fun `distance never runs past the end of a short word`() {
        // The candidate is shorter than the fragment, so every extra letter she
        // typed is another mistake. This used to be where an index went out of
        // bounds if the loop was written against the wrong string.
        assertEquals(3, Words.prefixDistance("ajuda", "aj", max = 5))
    }

    // --- what it does to the suggestions ---

    @Test
    fun `a doubled letter still finds the word`() {
        val p = predictor("ajuda", "ajudar", "telefon")
        assertTrue(p.predict("ajju", 3).contains("ajuda"))
    }

    @Test
    fun `a missing letter still finds the word`() {
        val p = predictor("ajuda", "telefon")
        assertTrue(p.predict("aud", 3).contains("ajuda"))
    }

    @Test
    fun `exact matches always come first`() {
        // ajuda is exactly what she typed the beginning of; ajjuda is only
        // reachable by forgiving. The one she actually typed must not be pushed
        // down the row by a guess at one she did not.
        val p = predictor("ajuda", "ajudar", "ajudant")
        val offered = p.predict("ajud", 3)
        assertEquals(listOf("ajuda", "ajudar", "ajudant"), offered)
    }

    @Test
    fun `forgiving never displaces a word that fits exactly`() {
        val p = predictor("casa", "cosa")
        // Both fit "cas" - casa exactly, cosa by one mistake. Asked for one, it
        // has to be the exact one.
        assertEquals(listOf("casa"), p.predict("cas", 1))
    }

    @Test
    fun `two letters are too few to forgive`() {
        // With two letters typed, one forgiven mistake matches most of the
        // dictionary, and the row would fill with noise while she can still see
        // her own word coming.
        val p = predictor("telefon")
        assertFalse(p.predict("aj", 3).contains("telefon"))
    }

    @Test
    fun `two mistakes are still too many`() {
        val p = predictor("ajuda")
        assertTrue(p.predict("akkud", 3).isEmpty())
    }

    @Test
    fun `it can be turned off`() {
        val p = predictor("ajuda")
        p.forgiving = false
        assertTrue(p.predict("ajju", 3).isEmpty())
        p.forgiving = true
        assertTrue(p.predict("ajju", 3).contains("ajuda"))
    }

    @Test
    fun `a correctly typed fragment gives the same answer either way`() {
        val p = predictor("ajuda", "ajudar", "ajudant", "telefon")
        val forgiving = p.predict("aju", 3)
        p.forgiving = false
        assertEquals(p.predict("aju", 3), forgiving)
    }

    @Test
    fun `the word she already finished still drives the next one`() {
        // Forgiving applies to the fragment being typed, not to the sentence.
        // "vull" is complete and correct here, and must not be re-guessed.
        val p = NgramPredictor(
            NgramModel.load(StringReader("u\tajuda\t1\nb\tvull\tajuda\t9")),
            openers = emptyList(),
        )
        assertTrue(p.predict("vull ajju", 3).contains("ajuda"))
    }

    @Test
    fun `accents are still folded through a forgiven mistake`() {
        val p = predictor("telèfon")
        assertTrue(p.predict("teleefon", 3).contains("telèfon"))
    }
}
