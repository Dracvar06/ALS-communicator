package cat.merce.comunicador.prediction

/**
 * What she herself actually writes.
 *
 * People say the same things to the same people every day, so her own past
 * words beat any general model of Catalan. This is small, and it outranks the
 * shipped model rather than being blended into it.
 *
 * Everything here stays on the tablet. It is a record of a person's private
 * speech and it must never leave the device.
 */
class PersonalModel {

    private val wordCounts = HashMap<String, Int>()

    /** Folded previous word to what she put after it, with counts. */
    private val followCounts = HashMap<String, HashMap<String, Int>>()

    /** Recomputed only after something changes, not on every keystroke. */
    private var rankedWords: List<String>? = null
    private val rankedFollowers = HashMap<String, List<String>>()

    /** Records that [word] was written, after [previous]. */
    fun learn(previous: String, word: String) {
        val learned = word.lowercase()
        if (learned.isBlank()) return

        wordCounts[learned] = (wordCounts[learned] ?: 0) + 1
        rankedWords = null

        val head = Words.fold(previous)
        if (head.isNotEmpty()) {
            val followers = followCounts.getOrPut(head) { HashMap() }
            followers[learned] = (followers[learned] ?: 0) + 1
            rankedFollowers.remove(head)
        }
    }

    /** Takes back what [learn] recorded, when undo removes a finished word. */
    fun unlearn(previous: String, word: String) {
        val learned = word.lowercase()

        wordCounts[learned]?.let { count ->
            if (count <= 1) wordCounts.remove(learned) else wordCounts[learned] = count - 1
            rankedWords = null
        }

        val head = Words.fold(previous)
        val followers = followCounts[head] ?: return
        followers[learned]?.let { count ->
            if (count <= 1) followers.remove(learned) else followers[learned] = count - 1
            if (followers.isEmpty()) followCounts.remove(head)
            rankedFollowers.remove(head)
        }
    }

    /** Her own words, most used first. */
    fun words(): List<String> =
        rankedWords ?: rank(wordCounts).also { rankedWords = it }

    /** What she herself tends to write after a word. */
    fun successorsOf(foldedPrevious: String): List<String> {
        if (foldedPrevious.isEmpty()) return emptyList()
        rankedFollowers[foldedPrevious]?.let { return it }
        val counts = followCounts[foldedPrevious] ?: return emptyList()
        return rank(counts).also { rankedFollowers[foldedPrevious] = it }
    }

    /**
     * One line per fact, so the file can be read by a person and repaired by
     * hand if it is ever corrupted.
     *
     *     w  <word>  <count>
     *     f  <previous>  <word>  <count>
     */
    fun toLines(): List<String> {
        val lines = ArrayList<String>()
        for ((word, count) in wordCounts) lines += "w\t$word\t$count"
        for ((head, followers) in followCounts) {
            for ((word, count) in followers) lines += "f\t$head\t$word\t$count"
        }
        return lines
    }

    private fun rank(counts: Map<String, Int>): List<String> =
        counts.entries
            // Most used first; alphabetical after that so the order never
            // wobbles between two words used the same number of times.
            .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
            .map { it.key }

    companion object {

        fun fromLines(lines: Iterable<String>): PersonalModel {
            val model = PersonalModel()
            for (line in lines) {
                val parts = line.split('\t')
                // A damaged line is skipped. Losing a little history is always
                // better than failing to start.
                when {
                    parts.size == 3 && parts[0] == "w" -> {
                        val count = parts[2].toIntOrNull() ?: continue
                        model.wordCounts[parts[1]] = count
                    }

                    parts.size == 4 && parts[0] == "f" -> {
                        val count = parts[3].toIntOrNull() ?: continue
                        model.followCounts.getOrPut(parts[1]) { HashMap() }[parts[2]] = count
                    }
                }
            }
            return model
        }
    }
}
