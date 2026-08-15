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

    /** The last word she finished, which is what the next one is predicted from. */
    fun previous(context: String): String =
        context.substringBeforeLast(' ', missingDelimiterValue = "")
            .substringAfterLast(' ')
}
