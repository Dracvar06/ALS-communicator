package cat.merce.comunicador.prediction

/**
 * Suggests words from the shipped Catalan model and from what she has written
 * before.
 *
 * Sources are tried in a fixed order rather than having their scores mixed
 * together. Blending needs weights nobody can justify, and this way the reason
 * any word appears can be read straight off the list:
 *
 *   1. what she herself put after this word before
 *   2. what she herself writes, anywhere
 *   3. what Catalan usually puts after this word
 *   4. the commonest Catalan words
 *
 * Her own habits come first because they are far more informative about what
 * she means to say next than a million subtitles are.
 */
class NgramPredictor(
    private val model: NgramModel,
    val personal: PersonalModel = PersonalModel(),
    private val openers: List<String> = CATALAN_OPENERS,
) : Predictor {

    /**
     * Whether a fragment with a mistake in it still gets suggestions.
     *
     * Only ever *adds*: the exact matches are found first and keep their
     * places, and this fills slots that would otherwise have been left empty.
     * So the worst it can do is offer a word she did not mean, in a slot that
     * was showing nothing at all.
     */
    var forgiving: Boolean = true

    override fun predict(context: String, limit: Int): List<String> {
        require(limit >= 0) { "limit cannot be negative, was $limit" }
        if (limit == 0) return emptyList()

        val prefix = Words.fold(Words.partial(context))
        val previous = Words.fold(Words.previous(context))

        // Keeps insertion order and drops repeats, so a word offered by an
        // earlier source is never pushed down by a later one.
        val chosen = LinkedHashSet<String>()

        take(chosen, personal.successorsOf(previous), prefix, limit)

        if (prefix.isNotEmpty()) {
            // Only useful for finishing a word she has started. With nothing
            // typed, her most used words are not a guess at what comes next,
            // and the top one would be whatever she just wrote: VULL VULL.
            take(chosen, personal.words(), prefix, limit)
        }

        take(chosen, model.successorsOf(previous), prefix, limit)

        if (prefix.isEmpty()) {
            // Nothing to complete and nothing useful to follow on from, so fall
            // back to openers rather than to raw frequency, which would offer
            // 'no', 'que', 'de' on an empty screen.
            take(chosen, openers, prefix, limit)
        }

        if (chosen.size < limit) {
            // The only source that can search the whole vocabulary, so it is
            // asked last and only for what is still missing.
            take(chosen, model.startingWith(prefix, limit + chosen.size), prefix, limit)
        }

        // Everything above matched exactly. If the slots are still not full,
        // the likeliest reason is that there is a mistake in what she typed and
        // no word in Catalan begins that way — so ask again, forgivingly.
        //
        // This runs second and only on what is left over, which is the whole
        // point: a correctly typed fragment can never have a word she meant
        // pushed off the end by a guess at a word she did not.
        if (chosen.size < limit && forgiving && prefix.length >= MIN_FORGIVING_LENGTH) {
            val allowed = 1
            take(chosen, personal.successorsOf(previous), prefix, limit, allowed)
            take(chosen, personal.words(), prefix, limit, allowed)
            take(chosen, model.successorsOf(previous), prefix, limit, allowed)
            if (chosen.size < limit) {
                take(
                    into = chosen,
                    candidates = model.resembling(prefix, limit + chosen.size, allowed),
                    prefix = prefix,
                    limit = limit,
                    allowed = allowed,
                )
            }
        }

        return chosen.take(limit)
    }

    /**
     * @param allowed how many single character mistakes to forgive. Zero is an
     *   ordinary exact match on the beginning of the word.
     */
    private fun take(
        into: LinkedHashSet<String>,
        candidates: List<String>,
        prefix: String,
        limit: Int,
        allowed: Int = 0,
    ) {
        if (into.size >= limit) return
        for (candidate in candidates) {
            val folded = Words.fold(candidate)
            val fits = prefix.isEmpty() ||
                if (allowed == 0) folded.startsWith(prefix)
                else Words.resembles(prefix, folded, allowed)
            if (fits) {
                into += candidate
                if (into.size == limit) return
            }
        }
    }

    private companion object {

        /**
         * Below this, forgiving is worse than useless: with two letters typed,
         * one forgiven mistake matches most of the dictionary, and the slots
         * fill with noise at the exact moment she can still see her own word
         * coming.
         */
        const val MIN_FORGIVING_LENGTH = 3
    }
}
