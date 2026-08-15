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
 * three word suggestions, yes and no, and delete. A suggestion can replace five
 * or six letters with a single press, and yes and no answer a whole question,
 * so nothing else earns that row. Delete sits there because a mistake she
 * cannot cheaply undo is worse than a letter she reaches slowly.
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
        Key.Yes, Key.No, Key.Delete,
    ),
    listOf(Key.Space, l("A"), l("E"), l("S"), l("R"), l("L")),
    listOf(l("I"), l("T"), l("N"), l("O"), l("U"), l("C")),
    listOf(l("D"), l("M"), l("P"), l("Q"), l("B"), l("G")),
    listOf(l("F"), l("V"), l("H"), l("X"), l("J"), l("Z")),
    listOf(l("Ç"), l("Y"), l("K"), l("W"), Key.Clear),
)
