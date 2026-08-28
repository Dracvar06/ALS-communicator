package cat.merce.comunicador.prediction

import java.io.Reader

/**
 * The Catalan model built offline by tools/build_model.py and shipped as an
 * app asset.
 *
 * It holds two things: a vocabulary in frequency order, and, for the commoner
 * words, what tends to come after them. Counts are dropped on the way in
 * because only the order is ever used.
 *
 * No Android here. The caller opens the file and hands over a [Reader].
 */
class NgramModel private constructor(
    private val words: List<String>,
    private val foldedWords: List<String>,
    private val byFirstLetter: Map<Char, IntArray>,
    private val successors: Map<String, List<String>>,
) {

    val size: Int get() = words.size

    /** What usually follows a word, commonest first. Empty if it is unknown. */
    fun successorsOf(foldedPrevious: String): List<String> =
        if (foldedPrevious.isEmpty()) emptyList() else successors[foldedPrevious].orEmpty()

    /**
     * Words beginning with what she has typed, commonest first.
     *
     * Only the words sharing a first letter are examined rather than all thirty
     * thousand. This runs on the same thread as the cursor, so it has to stay
     * far below one scan step.
     */
    fun startingWith(foldedPrefix: String, limit: Int): List<String> {
        if (limit <= 0) return emptyList()
        if (foldedPrefix.isEmpty()) return words.take(limit)

        val bucket = byFirstLetter[foldedPrefix[0]] ?: return emptyList()
        val found = ArrayList<String>(limit)
        for (index in bucket) {
            if (foldedWords[index].startsWith(foldedPrefix)) {
                found += words[index]
                if (found.size == limit) break
            }
        }
        return found
    }

    /**
     * Words that could be what she meant, allowing for [allowed] mistakes.
     * Commonest first, like [startingWith].
     *
     * Only ever called once the exact search has come up short, because it is
     * the expensive one: every candidate is compared letter by letter rather
     * than looked up. The buckets keep that bounded — the first two letters she
     * typed, since one of them may be the mistake, and nothing else.
     */
    fun resembling(foldedPrefix: String, limit: Int, allowed: Int): List<String> {
        if (limit <= 0 || foldedPrefix.isEmpty()) return emptyList()

        val letters = LinkedHashSet<Char>()
        letters += foldedPrefix[0]
        if (foldedPrefix.length > 1) letters += foldedPrefix[1]

        val found = ArrayList<String>(limit)
        var examined = 0
        for (letter in letters) {
            for (index in byFirstLetter[letter] ?: IntArray(0)) {
                // A ceiling on the work rather than on the answer. The buckets
                // are in frequency order, so stopping early drops the rarest
                // words, which are the ones she was least likely to mean.
                if (examined++ > RESEMBLING_BUDGET) return found
                if (Words.resembles(foldedPrefix, foldedWords[index], allowed)) {
                    found += words[index]
                    if (found.size == limit) return found
                }
            }
        }
        return found
    }

    companion object {

        /** How many words a forgiving search may look at before giving up. */
        private const val RESEMBLING_BUDGET = 6000

        /**
         * Reads the asset. Lines are tab separated:
         *
         *     u  <word>  <count>
         *     b  <prev>  <next>  <count>  <next>  <count> ...
         *
         * Anything unrecognised is skipped rather than throwing. A model that
         * loads imperfectly is far better than an app that will not start.
         */
        fun load(reader: Reader): NgramModel {
            val words = ArrayList<String>()
            val folded = ArrayList<String>()
            val successors = HashMap<String, List<String>>()

            reader.forEachLine { line ->
                val parts = line.split('\t')
                when {
                    parts.size >= 2 && parts[0] == "u" -> {
                        words += parts[1]
                        folded += Words.fold(parts[1])
                    }

                    parts.size >= 4 && parts[0] == "b" -> {
                        val head = Words.fold(parts[1])
                        // Successors sit at 2, 4, 6...; the counts between them
                        // were only needed to put these in order.
                        val following = (2 until parts.size step 2).map { parts[it] }
                        // Two different spellings can fold to the same key. The
                        // first wins, and the file is in frequency order, so
                        // that is the commoner spelling.
                        successors.putIfAbsent(head, following)
                    }
                }
            }

            val buckets = HashMap<Char, MutableList<Int>>()
            for (index in folded.indices) {
                val first = folded[index].firstOrNull() ?: continue
                buckets.getOrPut(first) { ArrayList() } += index
            }

            return NgramModel(
                words = words,
                foldedWords = folded,
                byFirstLetter = buckets.mapValues { (_, list) -> list.toIntArray() },
                successors = successors,
            )
        }
    }
}
