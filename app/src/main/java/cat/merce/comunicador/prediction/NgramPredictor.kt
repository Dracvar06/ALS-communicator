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

        return chosen.take(limit)
    }

    private fun take(
        into: LinkedHashSet<String>,
        candidates: List<String>,
        prefix: String,
        limit: Int,
    ) {
        if (into.size >= limit) return
        for (candidate in candidates) {
            if (prefix.isEmpty() || Words.fold(candidate).startsWith(prefix)) {
                into += candidate
                if (into.size == limit) return
            }
        }
    }
}
