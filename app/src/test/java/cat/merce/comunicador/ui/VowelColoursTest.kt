package cat.merce.comunicador.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tinting the vowel keys. */
class VowelColoursTest {

    @Test
    fun `off by default, so a known grid does not change`() {
        val c = ScanController()
        assertFalse(c.isVowel(Key.Letter("A")))
    }

    @Test
    fun `when on, vowels are tinted and nothing else is`() {
        val c = ScanController()
        c.useVowelColours(true)
        assertTrue(c.isVowel(Key.Letter("A")))
        assertTrue(c.isVowel(Key.Letter("u")))
        assertFalse(c.isVowel(Key.Letter("S")))
        assertFalse(c.isVowel(Key.Clear))
    }

    @Test
    fun `every language's vowels are keys on its own grid`() {
        for (language in LANGUAGES) {
            val keys = language.letterRows.flatten().toSet()
            for (vowel in language.vowels) {
                assertTrue("${language.code}: $vowel", vowel in keys)
            }
        }
    }
}
