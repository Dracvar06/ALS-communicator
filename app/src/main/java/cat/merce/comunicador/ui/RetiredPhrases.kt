package cat.merce.comunicador.ui

/**
 * Phrase lists that used to be the defaults.
 *
 * The phrases live in an editable file, seeded once from the language's
 * defaults, and after that the file wins — which is right, because a helper's
 * edits must never be overwritten by an update. But it also means that changing
 * the defaults does nothing at all on a device that has already run the app,
 * which is every device that matters.
 *
 * So an update rewrites the file only when it still holds, word for word, a
 * list this app once shipped. That is proof nobody has edited it. Anything else
 * — one phrase changed, one added, one removed — is somebody's work and is left
 * exactly alone.
 *
 * When the defaults change again, the list being retired is added here.
 */
object RetiredPhrases {

    /** Shipped until 2026-08-28, when Mercè's own phrases replaced them. */
    private val FIRST = listOf(
        listOf(
            "Tinc dolor",
            "Si us plau, gira'm",
            "Tinc set",
            "Tinc gana",
            "Tinc fred",
            "Tinc calor",
            "Necessito anar al lavabo",
            "Truca a la infermera",
            "Espera un moment",
            "Estic bé",
            "Gràcies",
            "T'estimo",
        ),
        listOf(
            "I am in pain",
            "Please turn me",
            "I am thirsty",
            "I am hungry",
            "I am cold",
            "I am too warm",
            "I need the toilet",
            "Please call the nurse",
            "Wait a moment",
            "I am fine",
            "Thank you",
            "I love you",
        ),
        listOf(
            "Tengo dolor",
            "Por favor, gírame",
            "Tengo sed",
            "Tengo hambre",
            "Tengo frío",
            "Tengo calor",
            "Necesito ir al baño",
            "Llama a la enfermera",
            "Espera un momento",
            "Estoy bien",
            "Gracias",
            "Te quiero",
        ),
    )

    private val ALL: List<List<String>> = FIRST

    /**
     * Whether [lines] are exactly a list this app once shipped, and so have
     * never been touched by anyone.
     */
    fun neverEdited(lines: List<String>): Boolean =
        ALL.any { retired -> retired.size == lines.size && retired == lines }
}
