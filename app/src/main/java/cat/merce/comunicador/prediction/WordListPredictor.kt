package cat.merce.comunicador.prediction

/**
 * A tiny predictor over a fixed hand written list.
 *
 * This is the fallback used only when the shipped model cannot be read. It is
 * far worse than [NgramPredictor] and knows nothing about what came before the
 * word being typed, but it means a damaged or missing asset costs her some
 * suggestions rather than the ability to speak.
 */
class WordListPredictor(words: List<String> = CATALAN_WORDS) : Predictor {

    /** Duplicates would show the same suggestion in two slots. */
    private val words: List<String> = words.distinct()

    /** Accent and case stripped once at startup, not on every keystroke. */
    private val folded: List<String> = this.words.map { Words.fold(it) }

    override fun predict(context: String, limit: Int): List<String> {
        require(limit >= 0) { "limit cannot be negative, was $limit" }
        if (limit == 0) return emptyList()

        val prefix = Words.fold(Words.partial(context))

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
}
