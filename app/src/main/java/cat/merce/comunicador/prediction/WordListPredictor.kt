package cat.merce.comunicador.prediction

import java.text.Normalizer

/**
 * Suggests words by matching the start of the word being typed against a fixed
 * list.
 *
 * Matching ignores accents and case, which is not a nicety. The grid has no
 * accented keys, so without this she could never reach *més*, *què* or *demà*
 * at all. Typing M E S offers *més*, and choosing it inserts the accent she
 * cannot type. That makes this the only route to correctly spelled Catalan.
 *
 * The search is a scan of a few hundred short strings, which is fast enough to
 * sit on the same thread as the cursor. That stops being true the moment this
 * is replaced by a real model, and at that point the work has to move off the
 * scan thread and its result be picked up on a later tick.
 */
class WordListPredictor(words: List<String> = CATALAN_WORDS) : Predictor {

    /** Duplicates would show the same suggestion in two slots. */
    private val words: List<String> = words.distinct()

    /** Accent and case stripped once at startup, not on every keystroke. */
    private val folded: List<String> = this.words.map { fold(it) }

    override fun predict(context: String, limit: Int): List<String> {
        require(limit >= 0) { "limit cannot be negative, was $limit" }
        if (limit == 0) return emptyList()

        val prefix = fold(partialWord(context))

        // Nothing typed yet, so there is nothing to match on. Offer the words
        // at the top of the list, which are the ones for opening a sentence.
        if (prefix.isEmpty()) return words.take(limit)

        val matches = ArrayList<String>(limit)
        for (i in words.indices) {
            if (folded[i].startsWith(prefix)) {
                matches += words[i]
                if (matches.size == limit) break
            }
        }
        return matches
    }

    companion object {

        /** The bit after the last space: what she is part way through typing. */
        fun partialWord(context: String): String = context.substringAfterLast(' ')

        private val COMBINING_MARKS = Regex("\\p{Mn}+")

        /**
         * Lower cases and removes accents, so À becomes a and Ç becomes c.
         * Splitting the characters apart first is what puts the accent into a
         * separate mark that can then be dropped.
         */
        fun fold(value: String): String =
            Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
                .replace(COMBINING_MARKS, "")
    }
}
