package cat.merce.comunicador.ui

/**
 * How the four arrows are drawn.
 *
 * Only how they are *drawn*. What can be tapped is the same either way — the
 * whole square, cut into quarters by its diagonals, so a tap that falls between
 * two arrows still goes to the nearer one. A hand that cannot be placed
 * precisely needs a pad with no dead space in it far more than it needs tidy
 * edges, and that is not something a change of appearance should quietly cost.
 */
enum class ArrowShape {

    /** Four triangles meeting at a middle, reading as one four-pointed shape. */
    Joined,

    /**
     * Four separated buttons in a plus, each with its arrow inside it.
     *
     * The triangles alone turned out to look far smaller than the area that
     * actually answers to them, and someone aiming at a small shape aims
     * carefully, which is tiring. Giving each arrow a button behind it shows
     * the size of the target rather than hiding it.
     */
    Separate,

    ;

    companion object {
        fun fromCode(code: String?): ArrowShape =
            entries.firstOrNull { it.name == code } ?: Separate
    }
}
