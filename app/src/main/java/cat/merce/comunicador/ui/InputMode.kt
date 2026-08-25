package cat.merce.comunicador.ui

/**
 * How she drives the grid.
 *
 * Two genuinely different ways of using one hand, and which one suits a person
 * is not something that can be decided in advance. Scanning asks for patience
 * and one press; arrows ask for more presses and no patience at all. Both are
 * kept, and a helper switches between them in settings, because the same person
 * may want a different one as things change.
 */
enum class InputMode {

    /** The highlight moves on its own; a press takes what it is on. */
    Scan,

    /** The highlight stays put; she steers it with four arrows and chooses. */
    Arrows,
    ;

    companion object {
        fun fromCode(code: String?): InputMode =
            entries.firstOrNull { it.name == code } ?: Scan
    }
}
