package cat.merce.comunicador.input

/**
 * Collapses a stream of events from one input into single presses.
 *
 * An analog trigger does not report once. As it is squeezed it reports again
 * and again, dozens of times through the travel, because it is measuring
 * pressure rather than announcing a click. Left alone, one squeeze of a Stadia
 * pad's R2 arrives as a flood of presses.
 *
 * The rule is simply: events from the same input arriving in a steady stream
 * are one press. The first is taken and the rest of the burst is ignored, until
 * that input has been quiet long enough to count as released. Pressing again
 * then starts a new press.
 *
 * This is kept apart from [SwitchFilter], which answers a different question.
 * The filter is about how soon a *deliberate* second press may follow, and a
 * helper tunes it. This is about one physical action being reported many times,
 * which is a property of the hardware and not something anyone should have to
 * tune.
 *
 * Each input is tracked on its own, so squeezing R2 does not mask L2.
 *
 * Holds no clock: the caller passes the time in. Pure Kotlin, no Android.
 */
class PressCollapser(private val quietMs: Long = DEFAULT_QUIET_MS) {

    init {
        require(quietMs >= 0) { "quietMs cannot be negative, was $quietMs" }
    }

    private val lastSeen = HashMap<String, Long>()

    /**
     * @param token which input reported, so each is judged on its own.
     * @param atMillis when it reported, from a clock that only goes forwards.
     * @return true if this begins a new press rather than continuing one.
     */
    fun isNewPress(token: String, atMillis: Long): Boolean {
        val previous = lastSeen[token]
        // Every event refreshes the quiet timer, so a burst keeps the input
        // held for as long as it keeps reporting.
        lastSeen[token] = atMillis

        if (previous == null) return true
        val since = atMillis - previous
        // A clock that jumped backwards tells us nothing; treat it as new
        // rather than swallow a real press.
        if (since < 0) return true
        return since > quietMs
    }

    companion object {
        /**
         * Long enough to bridge the gaps within one squeeze of an analog
         * trigger, short enough that letting go and pressing again is a new
         * press rather than the same one.
         */
        const val DEFAULT_QUIET_MS = 120L
    }
}
