package cat.merce.comunicador.ui

/** One cell of the grid, and what pressing it does. */
sealed interface Key {

    /** The text shown on the cell. */
    val label: String

    data class Letter(val char: String) : Key {
        override val label: String get() = char
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

/**
 * The grid, ordered so the things she needs most are reached soonest.
 *
 * Scanning starts at the top left, so position on this grid is time. Row 0 is
 * cheapest to reach, and within a row the left is cheaper than the right. That
 * is why space sits first: in ordinary writing the space is more frequent than
 * any letter. Delete is also near the front, because a mistake that is
 * expensive to undo is worse than a letter that is slow to reach.
 *
 * The letters after that run roughly in Catalan frequency order.
 *
 * Accented characters (à è é í ï ò ó ú ü) and l·l are deliberately absent for
 * now. Every extra cell costs scan time for every letter she ever types, so
 * whether they earn their place is a real decision, not an oversight.
 */
val CATALAN_KEYBOARD: List<List<Key>> = listOf(
    listOf(Key.Space, l("A"), l("E"), l("S"), l("R"), Key.Delete),
    listOf(l("L"), l("I"), l("T"), l("N"), l("O"), l("U")),
    listOf(l("C"), l("D"), l("M"), l("P"), l("Q"), l("B")),
    listOf(l("G"), l("F"), l("V"), l("H"), l("X"), l("J")),
    listOf(l("Z"), l("Ç"), l("Y"), l("K"), l("W"), Key.Clear),
)
