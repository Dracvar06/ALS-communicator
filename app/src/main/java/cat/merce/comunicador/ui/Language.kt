package cat.merce.comunicador.ui

import cat.merce.comunicador.prediction.CATALAN_OPENERS

/**
 * Everything about the app that changes with the language.
 *
 * Not only the words on screen. The letters themselves differ, and so does the
 * order they are in, because that order is what makes scanning fast: the
 * commonest letters have to be reached first, and the commonest letters in
 * English are not the commonest in Catalan. The prediction model, the words
 * offered on a blank screen, the starting phrases and the voice all change too.
 *
 * Adding a language means adding one of these and nothing else.
 */
class Language(
    /** Used to name the saved setting and the model file. */
    val code: String,

    /** Shown in settings, in its own language. */
    val displayName: String,

    /** The prediction model in app assets, built by tools/build_model.py. */
    val modelAsset: String,

    /** What to ask the speech engine for. */
    val ttsLocale: String,

    /**
     * The letter rows, most used first. Row 0 of the finished grid is built
     * separately, since it holds the suggestions and yes/no rather than letters.
     */
    val letterRows: List<List<String>>,

    /** Offered when nothing has been typed; see CATALAN_OPENERS for why. */
    val openers: List<String>,

    /** The phrases a new install starts with, before a helper edits them. */
    val defaultPhrases: List<String>,

    // The words on the keys.
    val spaceLabel: String,
    val yesLabel: String,
    val noLabel: String,
    val phrasesLabel: String,
    val backLabel: String,

    // The words in settings, which a helper reads.
    val settingsSpeed: String,
    val settingsSecondsPerStep: String,
    val settingsFaster: String,
    val settingsSlower: String,
    val settingsFirstLetterExtra: String,
    val settingsMinBetweenPresses: String,
    val settingsTouchTitle: String,
    val settingsTouchDetail: String,
    val settingsTremorTitle: String,
    val settingsTremorDetail: String,
    val settingsButtons: String,
    val settingsWrite: String,
    val settingsUndo: String,
    val settingsAssignWrite: String,
    val settingsAssignUndo: String,
    val settingsClose: String,
    val settingsCheckButtons: String,
    val settingsEitherCloses: String,
    val settingsLanguage: String,
    val settingsLockedTitle: String,
    val settingsLockedDetail: String,
    val settingsBootTitle: String,
    val settingsBootDetail: String,

    // The words on the button-binding screen.
    val bindPressFor: String,
    val bindButtonFor: String,
    val bindWrite: String,
    val bindUndo: String,
    val bindAssigned: String,
    val bindPressNow: String,
    val bindAnother: String,
    val bindAddAnother: String,
    val bindDone: String,
    val bindCancel: String,

    // The words on the button-check screen.
    val checkTitle: String,
    val checkHint: String,
    val checkWaiting: String,
    val checkCode: String,
    val checkRoleWrite: String,
    val checkRoleUndo: String,
    val checkRoleUnassigned: String,
)

val CATALAN = Language(
    code = "ca",
    displayName = "Català",
    modelAsset = "ca-model.txt",
    ttsLocale = "ca",
    letterRows = listOf(
        listOf("A", "E", "S", "R", "L"),
        listOf("I", "T", "N", "O", "U", "C"),
        listOf("D", "M", "P", "Q", "B", "G"),
        listOf("F", "V", "H", "X", "J", "Z"),
        listOf("Ç", "Y", "K", "W"),
    ),
    openers = CATALAN_OPENERS,
    defaultPhrases = listOf(
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
    spaceLabel = "espai",
    yesLabel = "SÍ",
    noLabel = "NO",
    phrasesLabel = "FRASES",
    backLabel = "TORNA",
    settingsSpeed = "Velocitat de l'escaneig",
    settingsSecondsPerStep = "segons per pas",
    settingsFaster = "més ràpid",
    settingsSlower = "més lent",
    settingsFirstLetterExtra = "Temps extra a la primera lletra",
    settingsMinBetweenPresses = "Temps mínim entre polsacions",
    settingsTouchTitle = "Tocar la pantalla per escriure",
    settingsTouchDetail = "Dreta: escriu. Esquerra: desfà.",
    settingsTremorTitle = "Ignora els tremolors",
    settingsTremorDetail = "Una ràfega de tocs seguits compta una sola vegada.",
    settingsButtons = "Botons",
    settingsWrite = "Escriure",
    settingsUndo = "Desfer",
    settingsAssignWrite = "ASSIGNA ESCRIURE",
    settingsAssignUndo = "ASSIGNA DESFER",
    settingsClose = "TANCA",
    settingsCheckButtons = "COMPROVA ELS POLSADORS",
    settingsEitherCloses = "Qualsevol dels dos polsadors també tanca aquesta pantalla.",
    settingsLanguage = "Idioma",
    settingsLockedTitle = "Mode bloquejat",
    settingsLockedDetail = "L'aplicació es queda a la pantalla i no se'n pot sortir. Desactiva-ho aquí per sortir.",
    settingsBootTitle = "Obre's en encendre",
    settingsBootDetail = "L'aplicació s'obre sola quan el dispositiu es reinicia.",
    bindPressFor = "Prem el botó per a",
    bindButtonFor = "Botó per a",
    bindWrite = "ESCRIURE",
    bindUndo = "DESFER",
    bindAssigned = "Assignats",
    bindPressNow = "Prem ara, al comandament o al polsador, el botó que vulguis fer servir.",
    bindAnother = "Vols afegir un altre botó per a la mateixa acció?",
    bindAddAnother = "AFEGEIX UN ALTRE",
    bindDone = "FET",
    bindCancel = "CANCEL·LA",
    checkTitle = "Comprovació dels polsadors",
    checkHint = "Prem cada polsador. Aquesta pantalla es tanca sola.",
    checkWaiting = "Esperant…",
    checkCode = "codi",
    checkRoleWrite = "escriu",
    checkRoleUndo = "desfà",
    checkRoleUnassigned = "sense assignar",
)

/**
 * English letters in frequency order, so the commonest are reached first. The
 * order is the familiar ETAOIN SHRDLU of English text, which is genuinely
 * different from Catalan: E and T lead here, where Catalan leads with A and E.
 */
val ENGLISH = Language(
    code = "en",
    displayName = "English",
    modelAsset = "en-model.txt",
    ttsLocale = "en",
    letterRows = listOf(
        listOf("E", "T", "A", "O", "I"),
        listOf("N", "S", "R", "H", "L", "D"),
        listOf("C", "U", "M", "W", "F", "G"),
        listOf("Y", "P", "B", "V", "K", "J"),
        listOf("X", "Q", "Z"),
    ),
    openers = listOf(
        "I", "want", "need", "help", "please",
        "water", "pain", "toilet", "food", "cold",
        "hot", "tired", "wait", "stop", "hello",
        "yes", "thank", "can", "where", "what",
    ),
    defaultPhrases = listOf(
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
    spaceLabel = "space",
    yesLabel = "YES",
    noLabel = "NO",
    phrasesLabel = "PHRASES",
    backLabel = "BACK",
    settingsSpeed = "Scanning speed",
    settingsSecondsPerStep = "seconds per step",
    settingsFaster = "faster",
    settingsSlower = "slower",
    settingsFirstLetterExtra = "Extra time on the first letter",
    settingsMinBetweenPresses = "Minimum time between presses",
    settingsTouchTitle = "Touch the screen to write",
    settingsTouchDetail = "Right: writes. Left: undoes.",
    settingsTremorTitle = "Ignore tremors",
    settingsTremorDetail = "A burst of presses counts only once.",
    settingsButtons = "Buttons",
    settingsWrite = "Write",
    settingsUndo = "Undo",
    settingsAssignWrite = "ASSIGN WRITE",
    settingsAssignUndo = "ASSIGN UNDO",
    settingsClose = "CLOSE",
    settingsCheckButtons = "CHECK THE SWITCHES",
    settingsEitherCloses = "Either switch also closes this screen.",
    settingsLanguage = "Language",
    settingsLockedTitle = "Locked mode",
    settingsLockedDetail = "The app stays on screen and cannot be left. Turn this off here to leave.",
    settingsBootTitle = "Open on restart",
    settingsBootDetail = "The app opens by itself when the device restarts.",
    bindPressFor = "Press the button for",
    bindButtonFor = "Button for",
    bindWrite = "WRITE",
    bindUndo = "UNDO",
    bindAssigned = "Assigned",
    bindPressNow = "Now press, on the controller or switch, the button you want to use.",
    bindAnother = "Add another button for the same action?",
    bindAddAnother = "ADD ANOTHER",
    bindDone = "DONE",
    bindCancel = "CANCEL",
    checkTitle = "Checking the switches",
    checkHint = "Press each switch. This screen closes by itself.",
    checkWaiting = "Waiting…",
    checkCode = "code",
    checkRoleWrite = "writes",
    checkRoleUndo = "undoes",
    checkRoleUnassigned = "unassigned",
)

/**
 * Spanish letters in frequency order. Close to Catalan but not the same: E and
 * A lead here as well, though O ranks far higher and the Ñ has to be on the
 * grid. Accents are reached through the suggestions, as in the other languages.
 */
val SPANISH = Language(
    code = "es",
    displayName = "Español",
    modelAsset = "es-model.txt",
    ttsLocale = "es",
    letterRows = listOf(
        listOf("E", "A", "O", "S", "R"),
        listOf("N", "I", "D", "L", "C", "T"),
        listOf("U", "M", "P", "B", "G", "V"),
        listOf("Y", "Q", "H", "F", "Z", "J"),
        listOf("Ñ", "X", "K", "W"),
    ),
    openers = listOf(
        "quiero", "necesito", "ayuda", "gracias", "por favor",
        "agua", "dolor", "baño", "comer", "frío",
        "calor", "cansada", "espera", "para", "hola",
        "estoy", "tengo", "puedo", "dónde", "qué",
    ),
    defaultPhrases = listOf(
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
    spaceLabel = "espacio",
    yesLabel = "SÍ",
    noLabel = "NO",
    phrasesLabel = "FRASES",
    backLabel = "VOLVER",
    settingsSpeed = "Velocidad del barrido",
    settingsSecondsPerStep = "segundos por paso",
    settingsFaster = "más rápido",
    settingsSlower = "más lento",
    settingsFirstLetterExtra = "Tiempo extra en la primera letra",
    settingsMinBetweenPresses = "Tiempo mínimo entre pulsaciones",
    settingsTouchTitle = "Tocar la pantalla para escribir",
    settingsTouchDetail = "Derecha: escribe. Izquierda: deshace.",
    settingsTremorTitle = "Ignorar los temblores",
    settingsTremorDetail = "Una ráfaga de toques seguidos cuenta una sola vez.",
    settingsButtons = "Botones",
    settingsWrite = "Escribir",
    settingsUndo = "Deshacer",
    settingsAssignWrite = "ASIGNAR ESCRIBIR",
    settingsAssignUndo = "ASIGNAR DESHACER",
    settingsClose = "CERRAR",
    settingsCheckButtons = "COMPROBAR LOS PULSADORES",
    settingsEitherCloses = "Cualquiera de los dos pulsadores también cierra esta pantalla.",
    settingsLanguage = "Idioma",
    settingsLockedTitle = "Modo bloqueado",
    settingsLockedDetail = "La aplicación se queda en la pantalla y no se puede salir. Desactívalo aquí para salir.",
    settingsBootTitle = "Abrirse al encender",
    settingsBootDetail = "La aplicación se abre sola cuando el dispositivo se reinicia.",
    bindPressFor = "Pulsa el botón para",
    bindButtonFor = "Botón para",
    bindWrite = "ESCRIBIR",
    bindUndo = "DESHACER",
    bindAssigned = "Asignados",
    bindPressNow = "Pulsa ahora, en el mando o en el pulsador, el botón que quieras usar.",
    bindAnother = "¿Quieres añadir otro botón para la misma acción?",
    bindAddAnother = "AÑADIR OTRO",
    bindDone = "HECHO",
    bindCancel = "CANCELAR",
    checkTitle = "Comprobación de los pulsadores",
    checkHint = "Pulsa cada pulsador. Esta pantalla se cierra sola.",
    checkWaiting = "Esperando…",
    checkCode = "código",
    checkRoleWrite = "escribe",
    checkRoleUndo = "deshace",
    checkRoleUnassigned = "sin asignar",
)

/** Every language the app ships with. Adding one means adding it here. */
val LANGUAGES = listOf(CATALAN, SPANISH, ENGLISH)

fun languageForCode(code: String?): Language =
    LANGUAGES.firstOrNull { it.code == code } ?: CATALAN

/**
 * Builds the writing grid for a language: the suggestions, yes, no and the
 * phrases key on the fastest row, then space and the letters below.
 */
fun keyboardFor(language: Language): List<List<Key>> {
    val rows = mutableListOf(
        listOf(
            Key.Suggestion(0), Key.Suggestion(1), Key.Suggestion(2),
            Key.Yes, Key.No, Key.OpenPhrases,
        )
    )
    language.letterRows.forEachIndexed { index, letters ->
        // Space rides at the front of the first letter row: it is the single
        // most used character of all, so it earns the fastest letter position.
        val row = if (index == 0) {
            listOf(Key.Space) + letters.map { Key.Letter(it) }
        } else {
            letters.map { Key.Letter(it) }
        }
        rows += row
    }
    // The clear key goes at the very end, the slowest place on the grid, since
    // wiping the sentence is both rare and the most costly thing to do by
    // accident.
    rows[rows.size - 1] = rows.last() + Key.Clear
    return rows
}
