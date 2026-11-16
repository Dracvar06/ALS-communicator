package cat.merce.comunicador.prediction

import kotlin.math.ln

/**
 * Something that can say how likely each word is to come next. In the AI
 * build it is a small language model running on the tablet; in tests it is a
 * fake. No Android here, so the rules below are tested on a laptop.
 */
interface WordScorer {

    /**
     * @param previousWords the finished words of the message so far, in order
     * @param candidates words that fit what she has typed of the current word
     * @return a log-probability per candidate (higher is likelier), or null if
     *   the model could not answer. Null always means "keep the n-gram order".
     */
    fun score(previousWords: List<String>, candidates: List<String>): Map<String, Double>?
}

/**
 * Reorders the n-gram suggestions using the AI model's scores.
 *
 * The AI never replaces the n-gram list, it only reorders it. Measured on
 * held-out Catalan sentences (MerceV2-ai-eval, Oct 2026), letting the AI reorder
 * freely was worse than the n-gram alone (41% of presses saved against 50%),
 * because the n-gram's order carries real knowledge: her own words first, and
 * hand-picked openers at the start of a message. Blending the two, and leaving
 * the start of a message alone, was the only rule that did better on sentences
 * it had not been tuned on.
 */
object AiBlend {

    /**
     * How much the n-gram's own order counts against the AI's score. Each
     * place down the list costs [RANK_WEIGHT] times log(place). Chosen on the
     * tuning sentences, then checked on the others.
     */
    const val RANK_WEIGHT = 3.0

    /**
     * @param ngramOrder the n-gram's suggestions, best first
     * @param scores the AI's log-probabilities for (some of) those words
     * @param keepFirst how many words at the top are left exactly where they
     *   are: the ones from her own writing. They come first in the n-gram by
     *   design, and a general model of Catalan knows less about what she
     *   means than her own habits do. The measurement had no personal history
     *   to go on, so this is a rule, not a measured result.
     */
    fun reorder(ngramOrder: List<String>, scores: Map<String, Double>, keepFirst: Int = 0): List<String> {
        if (scores.isEmpty()) return ngramOrder
        if (keepFirst > 0) {
            val head = ngramOrder.take(keepFirst)
            return head + reorder(ngramOrder.drop(keepFirst), scores)
        }
        // A word the AI did not score keeps a score far below any real one, so
        // it falls behind the scored words but keeps its order among its kind.
        val floor = (scores.values.minOrNull() ?: 0.0) - 100.0
        return ngramOrder.withIndex()
            .sortedByDescending { (place, word) ->
                (scores[word] ?: floor) - RANK_WEIGHT * ln(place + 1.0)
            }
            .map { it.value }
    }

    /**
     * Whether the AI should be asked at all for this text. At the very start of
     * a message the n-gram offers openers chosen by hand for exactly this, and
     * the AI has nothing to go on, so it is not asked.
     */
    fun worthAsking(text: String): Boolean = previousWords(text).isNotEmpty()

    /** The finished words, without the one being typed. */
    fun previousWords(text: String): List<String> =
        text.substringBeforeLast(' ', missingDelimiterValue = "")
            .split(' ')
            .filter { it.isNotBlank() }
            .map { it.lowercase() }
}
