package cat.merce.comunicador.prediction

/**
 * Suggests words to finish or follow what has been typed so far.
 *
 * This seam exists so the engine underneath can be replaced without anything
 * else changing. Today it is a word list; later it is meant to be an n-gram
 * model built offline from a Catalan corpus and weighted towards her own
 * writing.
 */
interface Predictor {

    /**
     * @param context everything typed so far. The part after the last space is
     *   treated as a partly typed word.
     * @param limit how many suggestions are wanted. Fewer may be returned.
     */
    fun predict(context: String, limit: Int): List<String>
}
