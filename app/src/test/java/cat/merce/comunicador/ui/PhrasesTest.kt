package cat.merce.comunicador.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** The phrases screen, and letting an update reach a file that already exists. */
class PhrasesTest {

    @Test
    fun `the way back is the first cell rather than a row of its own`() {
        val grid = phrasesKeyboard(listOf("una", "dues", "tres", "quatre", "cinc"))
        assertEquals(Key.Back, grid[0][0])
        assertEquals(2, grid.size)
    }

    @Test
    fun `every row is the same width when the phrases fill it`() {
        // Fourteen phrases and the way back make fifteen cells, which is five
        // full rows of three. Every button is then the same size, which is what
        // Eloi asked for and what the layout gives away the moment a row is
        // short. If the default list changes, this is the check that the new
        // one still divides.
        for (language in listOf(CATALAN, ENGLISH, SPANISH)) {
            val cells = language.defaultPhrases.size + 1
            assertEquals(
                "${language.code} leaves a short row",
                0,
                cells % 3,
            )
        }
    }

    @Test
    fun `all three languages offer the same number of phrases`() {
        val sizes = listOf(CATALAN, ENGLISH, SPANISH).map { it.defaultPhrases.size }
        assertEquals(1, sizes.distinct().size)
    }

    @Test
    fun `no phrase is blank`() {
        for (language in listOf(CATALAN, ENGLISH, SPANISH)) {
            assertTrue(language.defaultPhrases.all { it.isNotBlank() })
        }
    }

    @Test
    fun `an untouched file is recognised so an update can reach it`() {
        // Exactly the list that shipped before, which is proof nobody edited it.
        val old = listOf(
            "Tinc dolor", "Si us plau, gira'm", "Tinc set", "Tinc gana",
            "Tinc fred", "Tinc calor", "Necessito anar al lavabo",
            "Truca a la infermera", "Espera un moment", "Estic bé",
            "Gràcies", "T'estimo",
        )
        assertTrue(RetiredPhrases.neverEdited(old))
    }

    @Test
    fun `a helper's edits are never treated as untouched`() {
        val edited = listOf(
            "Tinc dolor", "Si us plau, gira'm", "Tinc set", "Tinc gana",
            "Tinc fred", "Tinc calor", "Necessito anar al lavabo",
            "Truca a la infermera", "Espera un moment", "Estic bé",
            "Gràcies", "T'estimo", "Una frase que ha afegit algú",
        )
        assertFalse(RetiredPhrases.neverEdited(edited))
    }

    @Test
    fun `changing one word is enough to count as edited`() {
        val edited = listOf(
            "Tinc mal", "Si us plau, gira'm", "Tinc set", "Tinc gana",
            "Tinc fred", "Tinc calor", "Necessito anar al lavabo",
            "Truca a la infermera", "Espera un moment", "Estic bé",
            "Gràcies", "T'estimo",
        )
        assertFalse(RetiredPhrases.neverEdited(edited))
    }

    @Test
    fun `the current defaults are not treated as something to replace`() {
        // Otherwise every launch would rewrite the file for no reason, and the
        // next time the defaults changed it would silently overwrite a helper
        // who happened to agree with them.
        for (language in listOf(CATALAN, ENGLISH, SPANISH)) {
            assertFalse(RetiredPhrases.neverEdited(language.defaultPhrases))
        }
    }

    @Test
    fun `her own phrases are the ones on the grid`() {
        val c = ScanController()
        // Row 0, last cell is FRASES.
        c.press()
        repeat(c.rows[0].size - 1) { c.tick() }
        c.press()
        assertTrue(c.inPhrases)

        val spoken = c.rows.flatten().filterIsInstance<Key.Phrase>().map { it.text }
        assertTrue(spoken.contains("Bon dia"))
        assertTrue(spoken.contains("Aviseu a les meves filles"))
        assertTrue(spoken.contains("Vull anar al jardí"))
        assertTrue(spoken.contains("Vull anar al menjador"))
        assertTrue(spoken.contains("Em trobo malament"))
        assertTrue(spoken.contains("L'aigua"))
    }
}
