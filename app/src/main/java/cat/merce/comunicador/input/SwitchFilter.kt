package cat.merce.comunicador.input

/** Which of the two switches closed. */
enum class Switch { Write, Undo }

/**
 * Turns the mess a physical switch actually produces into single presses.
 *
 * A mechanical switch does not close once. The contacts bounce, and one press
 * arrives as a burst of closures a few milliseconds apart. On top of that, a
 * hand that cannot be held still can hit the same switch twice without meaning
 * to. Both look identical from here: a second press arriving too soon after the
 * first to have been meant.
 *
 * Two windows, because they answer different questions:
 *
 *  - [debounceMs] is how soon the *same* switch may be pressed again. This is
 *    the one that kills contact bounce and unintended repeats.
 *  - [settleMs] is how soon the *other* switch may be pressed. It is off by
 *    default, and exists for the case where a spasm hits both switches at once.
 *    Turning it up costs her the ability to write and immediately undo.
 *
 * Rejected presses do not extend either window. A switch that bounces
 * continuously would otherwise hold the window open forever and lock her out
 * entirely, which is the worst thing this class could possibly do.
 *
 * Holds no clock: the caller passes the time in. Pure Kotlin, no Android.
 */
class SwitchFilter(
    private val debounceMs: Long = DEFAULT_DEBOUNCE_MS,
    private val settleMs: Long = DEFAULT_SETTLE_MS,
) {

    init {
        require(debounceMs >= 0) { "debounceMs cannot be negative, was $debounceMs" }
        require(settleMs >= 0) { "settleMs cannot be negative, was $settleMs" }
    }

    private var lastAccepted: Switch? = null
    private var lastAcceptedAt: Long = 0

    /**
     * @param atMillis when the switch closed, from a clock that only goes
     *   forwards. Android's elapsedRealtime is the right one.
     * @return true if this is a real press and should be acted on.
     */
    fun accept(switch: Switch, atMillis: Long): Boolean {
        val previous = lastAccepted
        if (previous != null) {
            val since = atMillis - lastAcceptedAt
            // A clock that jumped backwards tells us nothing, so start again
            // rather than lock her out until it catches up.
            if (since >= 0) {
                val window = if (switch == previous) maxOf(debounceMs, settleMs) else settleMs
                if (since < window) return false
            }
        }

        lastAccepted = switch
        lastAcceptedAt = atMillis
        return true
    }

    companion object {
        /**
         * Long enough to swallow contact bounce and most unintended doubles,
         * short enough to leave a deliberate quick pair alone: opening a row
         * and immediately taking its first cell is two real presses close
         * together, and must keep working.
         */
        const val DEFAULT_DEBOUNCE_MS = 150L

        /** Off. The two switches do not block each other unless asked to. */
        const val DEFAULT_SETTLE_MS = 0L

        /** Above this, a deliberate quick pair of presses becomes impossible. */
        const val MAX_DEBOUNCE_MS = 1000L
    }
}
