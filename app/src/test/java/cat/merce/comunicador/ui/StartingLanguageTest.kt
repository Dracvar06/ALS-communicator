package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Test

/** Which language a fresh install, and a used one, opens in. */
class StartingLanguageTest {

    @Test
    fun `a fresh install follows the tablet's language`() {
        assertEquals(ENGLISH, startingLanguage(null, "en"))
        assertEquals(SPANISH, startingLanguage(null, "es"))
        assertEquals(CATALAN, startingLanguage(null, "ca"))
        assertEquals(FRENCH, startingLanguage(null, "fr"))
        assertEquals(ARABIC, startingLanguage(null, "ar"))
    }

    @Test
    fun `a tablet language the app does not ship falls back to Catalan`() {
        assertEquals(CATALAN, startingLanguage(null, "ja"))
    }

    @Test
    fun `a saved choice wins over the tablet's language`() {
        assertEquals(CATALAN, startingLanguage("ca", "en"))
    }
}
