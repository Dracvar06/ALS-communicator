package cat.merce.comunicador.ui

/**
 * One cell of the grid, and what pressing it does.
 *
 * Most cells carry no words of their own: what a key says depends on the
 * language, so [ScanController.label] resolves it from the current [Language].
 * Only the cells whose text *is* their identity, a letter and a phrase, carry
 * it here.
 */
sealed interface Key {

    data class Letter(val char: String) : Key

    /** One of the word suggestion slots along the top. */
    data class Suggestion(val slot: Int) : Key

    data object Yes : Key

    data object No : Key

    data object Space : Key

    data object Clear : Key

    /**
     * Backspace. Only on the grid in arrow mode, where there is no second
     * switch to undo with and removing a letter has to be reachable by hand.
     */
    data object Delete : Key

    /** Opens the phrases screen, from the writing grid. */
    data object OpenPhrases : Key

    /** A whole saved phrase. Selecting it speaks the phrase aloud. */
    data class Phrase(val text: String) : Key

    /** Returns from the phrases screen to writing. */
    data object Back : Key
}

/** How many word suggestions are shown. */
const val SUGGESTION_SLOTS = 3

/**
 * The two further suggestions that appear once she has started writing.
 *
 * Sí and no are only ever an answer to something, which means they are only
 * ever the first thing in a sentence. Once she is a letter into a word they are
 * two of the six best cells on the grid sitting idle for the rest of it, so
 * they are lent to prediction until the sentence is sent and they are needed
 * again. Nothing about the grid's shape changes: the same two cells simply say
 * something else.
 */
const val EXTRA_SUGGESTION_SLOTS = 2

/**
 * Lays the phrases out as a scannable grid: a full-width TORNA to leave, then
 * the phrases three to a row. Built at runtime because the phrases are editable.
 */
fun phrasesKeyboard(phrases: List<String>): List<List<Key>> {
    val rows = mutableListOf<List<Key>>(listOf(Key.Back))
    phrases.chunked(PHRASES_PER_ROW).forEach { chunk ->
        rows += chunk.map { Key.Phrase(it) }
    }
    return rows
}

private const val PHRASES_PER_ROW = 3
