package cat.merce.comunicador.ui

/**
 * Where the arrow pad sits, and which way round it is.
 *
 * Not decoration. A person reaching a mounted tablet brings a whole forearm
 * across it, and whatever the arm passes over on the way can be pressed by
 * accident — most expensively the choose button, since that is the one that
 * commits a letter she did not mean. Which parts of the screen the arm crosses
 * depends entirely on the chair, the mount and the arm, so none of this can be
 * decided here. Every arrangement is kept and offered, because the one that is
 * wrong for the person this was written for may be the only usable one for
 * somebody else.
 */
enum class ArrowPlacement {

    /** A column down the right-hand side, choose below the arrows. */
    Right,

    /** A column down the left-hand side, choose below the arrows. */
    Left,

    /** A strip across the bottom: arrows at the right end, choose at the left. */
    BottomRight,

    /** A strip across the bottom: arrows at the left end, choose at the right. */
    BottomLeft,
    ;

    /**
     * True for the two strip arrangements.
     *
     * A strip suits an arm that comes up from below rather than across, and it
     * puts the width of the screen between the arrows and choose. The arrows
     * are smaller in it: they have to stay congruent, so they are bounded by a
     * square, and a square in a horizontal strip can never be taller than the
     * strip.
     */
    val alongTheBottom: Boolean get() = this == BottomRight || this == BottomLeft

    /** True when the arrows are drawn before choose, reading left to right. */
    val arrowsFirst: Boolean get() = this == BottomLeft || this == Left

    companion object {

        /**
         * The saved value, tolerating one that no longer exists.
         *
         * "Bottom" is read as [BottomRight]: it is what the single strip
         * arrangement used to be called, back when the arrows were always at
         * the left end, and it was swapped round the day it was first tried on
         * a real arm.
         */
        fun fromCode(code: String?): ArrowPlacement = when (code) {
            null -> Right
            "Bottom" -> BottomRight
            else -> entries.firstOrNull { it.name == code } ?: Right
        }
    }
}
