package cat.merce.comunicador.prediction

/**
 * A starter Catalan word list, ordered by how useful each word is expected to
 * be, most useful first. The order is the whole of the ranking: when several
 * words match what she has typed, the earliest one wins.
 *
 * Two things about this list are worth knowing.
 *
 * First, it is hand written. It is not derived from a corpus and it has never
 * been measured. The roadmap replaces it with a model built offline from real
 * Catalan text and weighted towards her own writing, at which point this file
 * disappears. Until then it is a guess, and someone who knows her should read
 * it and change it.
 *
 * Second, the words at the very top are what she sees before typing anything,
 * so they are chosen for starting a sentence or asking for something, not for
 * raw frequency. Words further down exist mainly to be reached by their first
 * few letters.
 */
val CATALAN_WORDS: List<String> = listOf(
    // Asking for things, first because they open a sentence.
    "vull", "necessito", "ajuda", "si us plau", "gràcies",
    "aigua", "dolor", "lavabo", "menjar", "fred",
    "calor", "cansada", "bé", "malament", "espera",
    "para", "més", "menys", "ara", "després",

    // People around her.
    "mare", "pare", "fill", "filla", "germà",
    "germana", "família", "amic", "amiga", "metge",
    "infermera", "casa", "llit", "cadira", "finestra",
    "porta", "llum", "telèfon", "música", "televisió",
    "llibre", "manta", "coixí", "ulleres",

    // Very common Catalan words, for completing by prefix.
    "que", "què", "com", "quan", "qui",
    "on", "per", "però", "amb", "una",
    "uns", "unes", "els", "les", "del",
    "dels", "això", "aquest", "aquesta", "aquí",
    "allà", "molt", "molta", "poc", "tot",
    "tota", "tots", "res", "sempre", "mai",
    "també", "només", "encara", "abans", "avui",
    "demà", "ahir", "dia", "nit", "matí",
    "tarda", "hora", "temps", "any", "setmana",

    // Common verbs, in forms she is likely to want.
    "estic", "estàs", "està", "estem", "sóc",
    "ets", "és", "som", "són", "tinc",
    "tens", "té", "tenim", "puc", "pots",
    "pot", "podem", "vols", "vol", "volem",
    "faig", "fas", "fa", "fem", "vaig",
    "vas", "va", "anem", "dir", "digues",
    "veure", "mirar", "saber", "sé", "saps",
    "sap", "parlar", "escoltar", "seure", "aixecar",
    "dormir", "despertar", "obrir", "tancar", "posar",
    "treure", "portar", "donar", "agafar", "deixar",
    "venir", "vine", "anar", "sortir", "entrar",
    "canviar", "netejar", "trucar", "esperar", "acabar",

    // Everything else, roughly by frequency.
    "bon", "bona", "dia", "gran", "petit",
    "nou", "vella", "altre", "altra", "mateix",
    "cap", "sota", "sobre", "sense", "fins",
    "des", "entre", "cada", "algun", "alguna",
    "ningú", "algú", "alguna cosa", "cosa", "coses",
    "part", "lloc", "moment", "vegada", "manera",
    "mà", "mans", "cap", "ull", "ulls",
    "peu", "peus", "cara", "cos", "esquena",
    "vida", "món", "gent", "home", "dona",
    "nen", "nena", "sí", "no", "potser",
)
