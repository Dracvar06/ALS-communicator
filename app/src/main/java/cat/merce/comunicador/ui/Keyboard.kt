package cat.merce.comunicador.ui

/** One cell of the grid, and what pressing it does. */
sealed interface Key {

    /**
     * The text shown on the cell. A suggestion's word depends on what has been
     * typed, so its real label comes from [ScanController.label] instead.
     */
    val label: String

    data class Letter(val char: String) : Key {
        override val label: String get() = char
    }

    /** One of the word suggestion slots along the top. */
    data class Suggestion(val slot: Int) : Key {
        override val label: String get() = ""
    }

    data object Yes : Key {
        override val label: String get() = "SÍ"
    }

    data object No : Key {
        override val label: String get() = "NO"
    }

    data object Space : Key {
        override val label: String get() = "espai"
    }

    data object Delete : Key {
        override val label: String get() = "←"
    }

    data object Clear : Key {
        override val label: String get() = "×"
    }

    /** Opens the phrases screen, from the writing grid. */
    data object OpenPhrases : Key {
        override val label: String get() = "FRASES"
    }

    /** A whole saved phrase. Selecting it speaks the phrase aloud. */
    data class Phrase(val text: String) : Key {
        override val label: String get() = text
    }

    /** Returns from the phrases screen to writing. */
    data object Back : Key {
        override val label: String get() = "TORNA"
    }
}

private fun l(char: String) = Key.Letter(char)

/** How many word suggestions are shown. */
const val SUGGESTION_SLOTS = 3

/**
 * The writing grid, ordered so the things she needs most are reached soonest.
 *
 * Scanning starts at the top left, so position on this grid is time. Row 0 is
 * cheapest to reach, and within a row the left is cheaper than the right.
 *
 * Row 0 therefore holds everything that can save a lot of typing at once: the
 * three word suggestions, yes and no, and the phrases screen. A suggestion can
 * replace five or six letters with one press, yes and no answer a whole
 * question, and the phrases key opens a screen of whole sentences, so nothing
 * else earns that row.
 *
 * There is no delete key here. The undo switch removes the last thing she did,
 * faster than scanning to a delete key ever could, and it falls back to a plain
 * backspace when there is nothing left to undo, so nothing is lost by leaving it
 * out.
 *
 * The letters below run roughly in Catalan frequency order, space first.
 *
 * There is no settings key here on purpose. Anything she can reach by scanning,
 * she can reach by accident.
 *
 * Accented characters and l·l are still absent. Word suggestions are now the
 * route to them: typing M E S offers *més*, accent included.
 */
val CATALAN_KEYBOARD: List<List<Key>> = listOf(
    listOf(
        Key.Suggestion(0), Key.Suggestion(1), Key.Suggestion(2),
        Key.Yes, Key.No, Key.OpenPhrases,
    ),
    listOf(Key.Space, l("A"), l("E"), l("S"), l("R"), l("L")),
    listOf(l("I"), l("T"), l("N"), l("O"), l("U"), l("C")),
    listOf(l("D"), l("M"), l("P"), l("Q"), l("B"), l("G")),
    listOf(l("F"), l("V"), l("H"), l("X"), l("J"), l("Z")),
    listOf(l("Ç"), l("Y"), l("K"), l("W"), Key.Clear),
)

/**
 * The phrases shipped on a fresh install. A helper replaces these with the
 * person's own, so they are only a sensible starting point — the kind of thing
 * someone who cannot speak most needs to say quickly. Her daughter's list goes
 * here in the end.
 */
val DEFAULT_PHRASES: List<String> = listOf(
    "Tinc dolor",
    "Si us plau, gira'm",
    "Tinc set",
    "Tinc gana",
    "Tinc fred",
    "Tinc calor",
    "Necessito anar al lavabo",
    "Truca a la infermera",
    "Espera un moment",
    "Estic bé",
    "Gràcies",
    "T'estimo",
)

/**
 * Lays the phrases out as a scannable grid: a full-width TORNA to leave, then
 * the phrases three to a row. Built at runtime because the phrases are editable.
 */
fun phrasesKeyboard(phrases: List<String>): List<List<Key>> {
    val rows = mutableListOf<List<Key>>(listOf(Key.Back))
    phrases.chunked(PHRASES_PER_ROW).forEach { chunk ->
        rows += chunk.map { Key.Phrase(it) }
    }
    return rows
}

private const val PHRASES_PER_ROW = 3
