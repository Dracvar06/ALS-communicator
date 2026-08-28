package cat.merce.comunicador.prediction

import java.text.Normalizer

/** Text handling shared by every predictor. */
object Words {

    private val COMBINING_MARKS = Regex("\\p{Mn}+")

    /**
     * Lower cases and removes accents, so À becomes a and Ç becomes c.
     * Splitting the characters apart first is what turns the accent into a
     * separate mark that can then be dropped.
     *
     * This is not a nicety. The grid has no accented keys, so without folding
     * she could never reach *més*, *demà* or *telèfon* at all.
     */
    fun fold(value: String): String =
        Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
            .replace(COMBINING_MARKS, "")

    /** The bit after the last space: what she is part way through typing. */
    fun partial(context: String): String = context.substringAfterLast(' ')

    /**
     * How far [typed] is from the nearest beginning of [candidate], in single
     * character mistakes, giving up once past [max].
     *
     * Ordinary edit distance answers "how far is this word from that word",
     * which is the wrong question here: she is part way through writing, so
     * *ajjud* has to be able to reach *ajudar* even though the two are five
     * letters apart. Only the beginning of the candidate is compared, and the
     * best beginning wins.
     *
     * One mistake covers everything that actually happens on this app: a press
     * that landed twice (**ajjuda**), a press that did not register at all
     * (**ajda**), and a press that landed on the wrong cell (**akuda**).
     */
    fun prefixDistance(typed: String, candidate: String, max: Int): Int {
        if (typed.isEmpty()) return 0
        if (max < 0) return max + 1

        // Never look further into the candidate than the typed fragment could
        // plausibly reach: past that, every extra letter is another mistake.
        val depth = minOf(candidate.length, typed.length + max)

        // One row of the table at a time, the row being "the candidate read
        // this far". Row zero is the empty beginning, which every letter she
        // typed has to be deleted to reach.
        var previous = IntArray(typed.length + 1) { it }
        var best = previous[typed.length]

        for (j in 1..depth) {
            val current = IntArray(typed.length + 1)
            current[0] = j
            for (i in 1..typed.length) {
                val substitute = previous[i - 1] +
                    if (typed[i - 1] == candidate[j - 1]) 0 else 1
                current[i] = minOf(substitute, previous[i] + 1, current[i - 1] + 1)
            }
            if (current[typed.length] < best) best = current[typed.length]
            previous = current
        }
        return best
    }

    /**
     * Whether [candidate] could be what she meant by [typed], allowing for
     * [allowed] mistakes. Both are expected to be folded already.
     */
    fun resembles(typed: String, candidate: String, allowed: Int): Boolean =
        prefixDistance(typed, candidate, allowed) <= allowed

    /** The last word she finished, which is what the next one is predicted from. */
    fun previous(context: String): String =
        context.substringBeforeLast(' ', missingDelimiterValue = "")
            .substringAfterLast(' ')
}
