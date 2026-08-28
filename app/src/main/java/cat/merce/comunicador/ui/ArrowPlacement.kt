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

    /** A strip across the bottom of the screen. */
    Bottom,
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
    val alongTheBottom: Boolean get() = this == Bottom

    companion object {

        /**
         * The saved value, tolerating ones that no longer exist.
         *
         * "BottomRight" and "BottomLeft" were a single setting that said both
         * where the pad went and which way round it was. Which end choose sits
         * at turned out to be a separate question — it applies just as much to
         * a column, where it means top or bottom — so it became its own
         * setting, and both old names now simply mean the strip.
         */
        fun fromCode(code: String?): ArrowPlacement = when (code) {
            null -> Right
            "BottomRight", "BottomLeft" -> Bottom
            else -> entries.firstOrNull { it.name == code } ?: Right
        }
    }
}
