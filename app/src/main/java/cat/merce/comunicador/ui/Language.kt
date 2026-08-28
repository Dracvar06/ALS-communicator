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
    val clearLabel: String,
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

    // How she drives the grid: scanning, or steering with arrows.
    val settingsModeTitle: String,
    val settingsModeDetail: String,
    val settingsModeScan: String,
    val settingsModeArrows: String,
    val settingsModeDirect: String,
    val settingsArrowPlaceTitle: String,
    val settingsArrowPlaceDetail: String,
    val settingsArrowColumn: String,
    val settingsArrowBar: String,
    val settingsChooseTop: String,
    val settingsChooseLeft: String,
    val settingsChooseDetail: String,
    val settingsArrowRight: String,
    val settingsArrowLeft: String,
    val settingsArrowBottom: String,

    /** The word on the big choose button in arrow mode. */
    val arrowChoose: String,

    // The two erase buttons that can sit beside choose on the arrow pad.
    // The five headings this screen is grouped under. Without them it was one
    // flat scroll of nineteen controls, which reads as nineteen equally
    // important decisions when really there are about four.
    val sectionWriting: String,
    val sectionWritingDetail: String,
    val sectionTiming: String,
    val sectionTimingDetail: String,
    val sectionWords: String,
    val sectionWordsDetail: String,
    val sectionSwitches: String,
    val sectionSwitchesDetail: String,
    val sectionDevice: String,
    val sectionDeviceDetail: String,

    val settingsForgiveTitle: String,
    val settingsForgiveDetail: String,
    val settingsBoldWritingTitle: String,
    val settingsBoldWritingDetail: String,
    val settingsArrowShapeTitle: String,
    val settingsArrowShapeDetail: String,
    val settingsArrowShapeJoined: String,
    val settingsArrowShapeSeparate: String,
    val settingsEraseKeysTitle: String,
    val settingsEraseKeysDetail: String,
    val arrowDeleteLetter: String,

    /** What clear-all says once it is armed and waiting for a second press. */
    val arrowClearConfirm: String,

    /** Read aloud on the settings screen when the charge is getting low. */
    val batteryLow: String,

    // The walkthrough, which is for the helper rather than for her.
    val settingsTutorial: String,
    val tutorialNext: String,
    val tutorialBack: String,
    val tutorialActiveMode: String,
    val tutorialPressed: String,
    val tutorial: List<TutorialPage>,

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
        "Bon dia",
        "Em trobo malament",
        "L'aigua",
        "Vull anar al jardí",
        "Vull anar al menjador",
        "Tinc fred",
        "Tinc calor",
        "Necessito anar al lavabo",
        "Aviseu a les meves filles",
        "Truca a la infermera",
        "Espera un moment",
        "Estic bé",
        "Gràcies",
        "T'estimo",
    ),
    spaceLabel = "espai",
    clearLabel = "ESBORRA TOT",
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
    settingsModeTitle = "Com es tria una lletra",
    settingsModeDetail = "Amb escaneig el senyalador es mou sol i esperes. " +
        "Amb fletxes el mous tu i no has d'esperar mai.",
    settingsModeScan = "Escaneig automàtic",
    settingsModeArrows = "Fletxes",
    settingsModeDirect = "Tocar les lletres",
    settingsArrowPlaceTitle = "On van les fletxes",
    settingsArrowPlaceDetail = "Posa-les on el braç no hi passi per sobre en " +
        "anar a prémer. A baix separa TRIA de les fletxes tot l'ample de la " +
        "pantalla, però les fletxes queden més petites.",
    settingsArrowColumn = "En columna, a un costat",
    settingsArrowBar = "A baix, en una franja",
    settingsChooseTop = "TRIA a dalt",
    settingsChooseLeft = "TRIA a l'esquerra",
    settingsChooseDetail = "Posa TRIA on el braç no hi passi per sobre.",
    settingsArrowRight = "A la dreta",
    settingsArrowLeft = "A l'esquerra",
    settingsArrowBottom = "A baix",
    arrowChoose = "TRIA",
    sectionWriting = "COM S'ESCRIU",
    sectionWritingDetail = "La manera de triar les lletres. Comença per aquí: tota la resta d'aquest apartat en depèn.",
    sectionTiming = "RITME I TREMOLORS",
    sectionTimingDetail = "Per a una mà que tremola, o que prem dues vegades sense voler.",
    sectionWords = "PARAULES I LECTURA",
    sectionWordsDetail = "Què surt a la fila de paraules, i com es veu la frase escrita.",
    sectionSwitches = "POLSADORS I COMANDAMENTS",
    sectionSwitchesDetail = "Només cal si hi ha un polsador o un comandament connectat.",
    sectionDevice = "EL DISPOSITIU",
    sectionDeviceDetail = "Es configura un cop, en preparar l'aparell, i no s'hi torna.",
    settingsForgiveTitle = "Perdona les errades",
    settingsForgiveDetail = "Si es repeteix una lletra sense voler, o se n'escapa una, les paraules segueixen sortint. Les que encaixen exactament surten sempre primer.",
    settingsBoldWritingTitle = "Frase en negreta",
    settingsBoldWritingDetail = "La frase que escriu, més gruixuda, per llegir-la de lluny.",
    settingsArrowShapeTitle = "Com es veuen les fletxes",
    settingsArrowShapeDetail = "Separades, cada fletxa té el seu botó i es veu fins on arriba. En creu es veuen com una sola peça. El que es pot tocar és igual en els dos casos.",
    settingsArrowShapeJoined = "En creu",
    settingsArrowShapeSeparate = "Separades",
    settingsEraseKeysTitle = "Botons d'esborrar al costat de TRIA",
    settingsEraseKeysDetail = "Esborra una lletra, o tot, sense haver-hi d'anar amb les fletxes. ESBORRA TOT pregunta abans.",
    arrowDeleteLetter = "\u232b LLETRA",
    arrowClearConfirm = "SEGUR?",
    batteryLow = "Bateria baixa",
    settingsTutorial = "COM FUNCIONA",
    tutorialNext = "SEGÜENT",
    tutorialBack = "ENRERE",
    tutorialActiveMode = "AQUEST ÉS EL MODE ACTIU",
    tutorialPressed = "PREM",
    tutorial = listOf(
        TutorialPage(
            title = "Per a què serveix",
            lines = listOf(
                "Serveix perquè una persona que no pot parlar ni moure les mans " +
                    "pugui escriure i dir coses.",
                "Ella només fa un moviment: tocar la pantalla. Tota la resta " +
                    "l'ha de fer l'aplicació.",
                "Tu ets qui la prepara. Aquesta guia explica com funciona, " +
                    "perquè puguis explicar-l'hi bé.",
            ),
        ),
        TutorialPage(
            title = "Dues maneres de triar una lletra",
            lines = listOf(
                "N'hi ha dues, i es canvien als ajustos. Cap de les dues és " +
                    "millor: depèn de la persona.",
                "ESCANEIG AUTOMÀTIC: el requadre vermell es mou sol. Ella " +
                    "espera i prem quan arriba on vol.",
                "FLETXES: el requadre no es mou sol. Ella el mou amb quatre " +
                    "fletxes i prem TRIA.",
                "Si esperar la cansa, prova les fletxes. Si li costa encertar " +
                    "el moment exacte, també.",
            ),
        ),
        TutorialPage(
            title = "Les lletres no són botons",
            demo = TutorialDemo.Touch,
            lines = listOf(
                "Això és el que costa més d'entendre, i val la pena llegir-ho " +
                    "dues vegades.",
                "Les lletres de la pantalla NO són botons. Tocar la lletra que " +
                    "vol no fa res. Serveixen per llegir-les, no per prémer-les.",
                "Amb l'escaneig automàtic, els botons són dos i ocupen mitja " +
                    "pantalla cadascun: tota la meitat DRETA escriu, tota la " +
                    "meitat ESQUERRA desfà.",
                "No es veuen, i és a posta: la graella és per mirar-la, i unes " +
                    "línies al damunt només taparien lletres.",
                "Això vol dir que ella pot tocar on li vagi bé de la meitat " +
                    "que sigui. No ha d'encertar res, i no pot fallar el botó.",
                "Amb fletxes és diferent: allà els botons sí que es veuen, són " +
                    "les quatre fletxes i TRIA. Les lletres segueixen sense " +
                    "ser botons.",
                "Es pot desactivar als ajustos, a TOCAR LA PANTALLA PER " +
                    "ESCRIURE, si algú toca la pantalla sense voler.",
            ),
        ),
        TutorialPage(
            title = "Escaneig automàtic",
            demo = TutorialDemo.Scan,
            lines = listOf(
                "El requadre vermell baixa fila per fila, tot sol.",
                "Quan és a la fila que ella vol, prem. Ara el requadre es mou " +
                    "de lletra en lletra dins d'aquella fila.",
                "Quan és a la lletra que vol, torna a prémer. La lletra " +
                    "s'escriu i tot torna a començar de dalt.",
                "Si se li passa la lletra no cal fer res: després de dues " +
                    "voltes la fila es deixa sola i torna a baixar.",
                "Tocar la meitat esquerra de la pantalla desfà l'última cosa.",
            ),
        ),
        TutorialPage(
            title = "Fletxes",
            demo = TutorialDemo.Arrows,
            lines = listOf(
                "El requadre es queda quiet fins que ella el mou. No hi ha " +
                    "res a esperar i no es pot arribar tard.",
                "Les quatre fletxes el mouen una casella cada vegada. TRIA " +
                    "escriu la lletra que hi ha marcada.",
                "Dona la volta: des de la primera lletra d'una fila, " +
                    "l'esquerra porta a l'última. Des de la fila de dalt, " +
                    "amunt porta a la de baix. Res queda mai lluny.",
                "Després d'escriure, el requadre es queda on és. Dues lletres " +
                    "iguals seguides són dues premudes de TRIA.",
                "Al costat de TRIA hi ha ⌫ LLETRA, que esborra l'última " +
                    "lletra, i ESBORRA TOT, que buida la frase sencera. " +
                    "ESBORRA TOT es prem dues vegades: la primera passa a dir " +
                    "SEGUR? i es posa vermell, i només la segona buida. " +
                    "Qualsevol altra cosa que faci ho anul·la.",
                "La tecla ⌫ de la graella també esborra una lletra.",
                "Els espais entre les fletxes també compten: un toc que cau " +
                    "entre dues va a la més propera.",
            ),
        ),
        TutorialPage(
            title = "La graella",
            lines = listOf(
                "Fila de dalt: paraules que l'aplicació suggereix, i després " +
                    "SÍ, NO i FRASES.",
                "SÍ i NO només hi són amb la frase buida. En començar a " +
                    "escriure, aquelles dues caselles passen a ser dues " +
                    "paraules suggerides més: cinc en comptes de tres. Tornen " +
                    "soles quan la frase s'esborra.",
                "Les paraules suggerides canvien mentre ella escriu. Triar-ne " +
                    "una escriu la paraula sencera i l'espai: sovint és el " +
                    "camí més curt.",
                "També perdona errades: si una lletra es repeteix sense voler " +
                    "o se n'escapa una, les paraules continuen sortint. Triar-" +
                    "ne una arregla la paraula sencera, sense haver d'esborrar.",
                "Les lletres no estan per ordre alfabètic, sinó per ordre " +
                    "d'ús en català, perquè les més freqüents quedin a prop.",
                "× esborra tota la frase, i per això és a l'últim racó.",
            ),
        ),
        TutorialPage(
            title = "Frases",
            lines = listOf(
                "FRASES obre una pantalla amb frases senceres.",
                "Triar-ne una la diu en veu alta. No s'escriu res al requadre.",
                "Per tornar a escriure: TORNA, o tocar la meitat esquerra de " +
                    "la pantalla.",
            ),
        ),
        TutorialPage(
            title = "Els ajustos que importen",
            lines = listOf(
                "VELOCITAT DE L'ESCANEIG: quant dura cada pas. Si se li " +
                    "escapen lletres, fes-lo més lent.",
                "TEMPS EXTRA A LA PRIMERA LLETRA: en entrar a una fila, li " +
                    "dona un moment més per reaccionar.",
                "TEMPS MÍNIM ENTRE POLSACIONS: si una sola premuda escriu " +
                    "dues lletres, puja'l.",
                "IGNORA ELS TREMOLORS: una ràfega de tocs seguits compta una " +
                    "sola vegada.",
                "Canvia una cosa cada vegada i prova-la. Canviar-ho tot alhora " +
                    "no diu res sobre què ha ajudat.",
            ),
        ),
        TutorialPage(
            title = "Mode bloquejat i bateria",
            lines = listOf(
                "Amb el mode bloquejat, l'aplicació es queda a la pantalla i " +
                    "no se'n surt sense voler.",
                "Per sortir-ne: la rodeta ⚙ de dalt a la dreta, i desactiva'l " +
                    "als ajustos. Sempre es pot.",
                "La bateria es veu a dalt a la dreta. Es posa vermella per " +
                    "sota del 20 %.",
                "Si es queda sense bateria es queda sense veu. Val la pena " +
                    "deixar-la endollada.",
            ),
        ),
        TutorialPage(
            title = "Ja està",
            lines = listOf(
                "Tanca això i prova-ho tu, uns minuts, abans d'explicar-l'hi " +
                    "a ella. És la millor manera d'explicar-ho bé.",
                "Si alguna cosa no li va bé, gairebé sempre és un ajust i no " +
                    "l'aplicació. Comença per la velocitat.",
                "Pots tornar a obrir aquesta guia quan vulguis: ⚙ → COM FUNCIONA.",
            ),
        ),
    ),
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
        "Good morning",
        "I feel unwell",
        "Water",
        "I want to go to the garden",
        "I want to go to the dining room",
        "I am cold",
        "I am too warm",
        "I need the toilet",
        "Please tell my daughters",
        "Please call the nurse",
        "Wait a moment",
        "I am fine",
        "Thank you",
        "I love you",
    ),
    spaceLabel = "space",
    clearLabel = "CLEAR ALL",
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
    settingsModeTitle = "How a letter is chosen",
    settingsModeDetail = "Scanning moves the highlight for you and you wait. " +
        "Arrows let you move it yourself, with no waiting at all.",
    settingsModeScan = "Automatic scanning",
    settingsModeArrows = "Arrows",
    settingsModeDirect = "Touch the letters",
    settingsArrowPlaceTitle = "Where the arrows go",
    settingsArrowPlaceDetail = "Put them where the arm does not pass over them " +
        "on its way to press. Along the bottom puts the width of the screen " +
        "between CHOOSE and the arrows, but the arrows end up smaller.",
    settingsArrowColumn = "In a column, down one side",
    settingsArrowBar = "Along the bottom, in a strip",
    settingsChooseTop = "CHOOSE at the top",
    settingsChooseLeft = "CHOOSE on the left",
    settingsChooseDetail = "Put CHOOSE where the arm does not pass over it.",
    settingsArrowRight = "On the right",
    settingsArrowLeft = "On the left",
    settingsArrowBottom = "Along the bottom",
    arrowChoose = "CHOOSE",
    sectionWriting = "HOW WRITING WORKS",
    sectionWritingDetail = "How the letters are chosen. Start here: everything else in this section depends on it.",
    sectionTiming = "TIMING AND TREMORS",
    sectionTimingDetail = "For a hand that shakes, or that presses twice without meaning to.",
    sectionWords = "WORDS AND READING",
    sectionWordsDetail = "What appears in the word row, and how the written sentence looks.",
    sectionSwitches = "SWITCHES AND CONTROLLERS",
    sectionSwitchesDetail = "Only needed if a switch or a controller is connected.",
    sectionDevice = "THE DEVICE",
    sectionDeviceDetail = "Set once, while preparing the device, and then left alone.",
    settingsForgiveTitle = "Forgive mistakes",
    settingsForgiveDetail = "If a letter lands twice, or not at all, words still come up. Exact matches always come first.",
    settingsBoldWritingTitle = "Sentence in bold",
    settingsBoldWritingDetail = "The sentence she is writing, heavier, to be read from across a room.",
    settingsArrowShapeTitle = "How the arrows look",
    settingsArrowShapeDetail = "Separate gives each arrow its own button, so its size is visible. Joined draws them as one shape. What answers to a tap is the same either way.",
    settingsArrowShapeJoined = "Joined",
    settingsArrowShapeSeparate = "Separate",
    settingsEraseKeysTitle = "Erase buttons beside CHOOSE",
    settingsEraseKeysDetail = "Delete a letter, or everything, without steering there. CLEAR ALL asks first.",
    arrowDeleteLetter = "\u232b LETTER",
    arrowClearConfirm = "SURE?",
    batteryLow = "Battery low",
    settingsTutorial = "HOW IT WORKS",
    tutorialNext = "NEXT",
    tutorialBack = "BACK",
    tutorialActiveMode = "THIS IS THE MODE IN USE",
    tutorialPressed = "PRESS",
    tutorial = listOf(
        TutorialPage(
            title = "What it is for",
            lines = listOf(
                "It lets someone who cannot speak and cannot move their hands " +
                    "write things and say them out loud.",
                "She makes one movement: a touch on the screen. Everything " +
                    "else has to be done by the app.",
                "You are the person setting it up. This guide explains how it " +
                    "works, so that you can explain it to her properly.",
            ),
        ),
        TutorialPage(
            title = "Two ways to choose a letter",
            lines = listOf(
                "There are two, and you switch between them in settings. " +
                    "Neither is better: it depends on the person.",
                "AUTOMATIC SCANNING: the red highlight moves by itself. She " +
                    "waits, and presses when it reaches what she wants.",
                "ARROWS: the highlight does not move by itself. She moves it " +
                    "with four arrows and presses CHOOSE.",
                "If waiting tires her, try arrows. If getting the moment right " +
                    "is the hard part, try arrows too.",
            ),
        ),
        TutorialPage(
            title = "The letters are not buttons",
            demo = TutorialDemo.Touch,
            lines = listOf(
                "This is the part people get wrong, and it is worth reading " +
                    "twice.",
                "The letters on the screen are NOT buttons. Tapping the letter " +
                    "she wants does nothing. They are there to be read, not " +
                    "pressed.",
                "With automatic scanning there are two buttons and they are " +
                    "half the screen each: the whole RIGHT half writes, the " +
                    "whole LEFT half undoes.",
                "They are invisible on purpose: the grid is there to be looked " +
                    "at, and lines drawn over it would only cover letters.",
                "Which means she can touch wherever suits her within the right " +
                    "half. There is nothing to aim at, and she cannot miss.",
                "Arrows are different: there the buttons are visible — the " +
                    "four arrows and CHOOSE. The letters still are not buttons.",
                "You can switch this off in settings, under TOUCH THE SCREEN " +
                    "TO WRITE, if somebody keeps touching the screen by " +
                    "accident.",
            ),
        ),
        TutorialPage(
            title = "Automatic scanning",
            demo = TutorialDemo.Scan,
            lines = listOf(
                "The red highlight moves down the rows, one at a time, on its own.",
                "When it is on the row she wants, she presses. The highlight " +
                    "now moves letter by letter along that row.",
                "When it is on the letter she wants, she presses again. The " +
                    "letter is written and it all starts again from the top.",
                "If the letter goes past, she does not have to do anything: " +
                    "after two passes the row lets go by itself.",
                "Touching the left half of the screen undoes the last thing.",
            ),
        ),
        TutorialPage(
            title = "Arrows",
            demo = TutorialDemo.Arrows,
            lines = listOf(
                "The highlight stays still until she moves it. There is " +
                    "nothing to wait for and nothing to be late for.",
                "The four arrows move it one cell at a time. CHOOSE writes " +
                    "whichever letter is highlighted.",
                "It wraps around: from the first letter of a row, left goes to " +
                    "the last. From the top row, up goes to the bottom. " +
                    "Nothing is ever far away.",
                "After writing, the highlight stays where it is. A doubled " +
                    "letter is two presses of CHOOSE and nothing else.",
                "Beside CHOOSE there is ⌫ LETTER, which removes the last " +
                    "letter, and CLEAR ALL, which empties the whole sentence. " +
                    "CLEAR ALL takes two presses: the first turns it red and " +
                    "makes it say SURE?, and only the second empties it. " +
                    "Anything else she does cancels it.",
                "The ⌫ key on the grid removes one letter too.",
                "The gaps between the arrows count too: a tap that lands " +
                    "between two goes to the nearer one.",
            ),
        ),
        TutorialPage(
            title = "The grid",
            lines = listOf(
                "Top row: words the app is suggesting, then YES, NO and " +
                    "PHRASES.",
                "YES and NO are only there while the sentence is empty. Once " +
                    "she starts writing, those two cells become two more " +
                    "suggested words — five instead of three. They come back " +
                    "on their own when the sentence is cleared.",
                "The suggested words change as she writes. Taking one writes " +
                    "the whole word and the space after it, which is often " +
                    "much the shortest route.",
                "It also forgives mistakes: if a letter lands twice, or not at " +
                    "all, the words keep coming. Taking one repairs the whole " +
                    "word, with nothing to erase.",
                "The letters are not in alphabetical order. They are in order " +
                    "of how often they are used, so the common ones are near.",
                "× clears the whole sentence, which is why it sits in the last " +
                    "corner of the grid.",
            ),
        ),
        TutorialPage(
            title = "Phrases",
            lines = listOf(
                "PHRASES opens a screen of whole sentences.",
                "Choosing one speaks it out loud. Nothing is written into the " +
                    "text box.",
                "To get back to writing: BACK, or touch the left half of the " +
                    "screen.",
            ),
        ),
        TutorialPage(
            title = "The settings that matter",
            lines = listOf(
                "SCANNING SPEED: how long each step lasts. If letters are " +
                    "getting away from her, make it slower.",
                "EXTRA TIME ON THE FIRST LETTER: gives her a moment longer to " +
                    "react just after she has entered a row.",
                "MINIMUM TIME BETWEEN PRESSES: if one press is writing two " +
                    "letters, raise it.",
                "IGNORE TREMORS: a burst of taps close together counts once.",
                "Change one thing at a time and try it. Changing everything at " +
                    "once tells you nothing about what helped.",
            ),
        ),
        TutorialPage(
            title = "Locked mode and battery",
            lines = listOf(
                "In locked mode the app stays on the screen and cannot be left " +
                    "by accident.",
                "To get out: the ⚙ in the top right corner, then turn it off " +
                    "in settings. It is always possible.",
                "The battery is shown in the top right. It turns red below 20%.",
                "A flat battery is a person with no voice. It is worth leaving " +
                    "it plugged in.",
            ),
        ),
        TutorialPage(
            title = "That is all",
            lines = listOf(
                "Close this and try it yourself for a few minutes before you " +
                    "explain it to her. It is the only way to explain it well.",
                "If something is not working for her it is almost always a " +
                    "setting rather than the app. Start with the speed.",
                "You can open this guide again whenever you like: ⚙ → HOW IT " +
                    "WORKS.",
            ),
        ),
    ),
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
        "Buenos días",
        "Me encuentro mal",
        "El agua",
        "Quiero ir al jardín",
        "Quiero ir al comedor",
        "Tengo frío",
        "Tengo calor",
        "Necesito ir al baño",
        "Avisad a mis hijas",
        "Llama a la enfermera",
        "Espera un momento",
        "Estoy bien",
        "Gracias",
        "Te quiero",
    ),
    spaceLabel = "espacio",
    clearLabel = "BORRA TODO",
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
    settingsModeTitle = "Cómo se elige una letra",
    settingsModeDetail = "Con escaneo el señalador se mueve solo y hay que esperar. " +
        "Con flechas lo mueves tú y no esperas nunca.",
    settingsModeScan = "Escaneo automático",
    settingsModeArrows = "Flechas",
    settingsModeDirect = "Tocar las letras",
    settingsArrowPlaceTitle = "Dónde van las flechas",
    settingsArrowPlaceDetail = "Ponlas donde el brazo no pase por encima al ir " +
        "a pulsar. Abajo separa ELIGE de las flechas todo el ancho de la " +
        "pantalla, pero las flechas quedan más pequeñas.",
    settingsArrowColumn = "En columna, a un lado",
    settingsArrowBar = "Abajo, en una franja",
    settingsChooseTop = "ELIGE arriba",
    settingsChooseLeft = "ELIGE a la izquierda",
    settingsChooseDetail = "Pon ELIGE donde el brazo no pase por encima.",
    settingsArrowRight = "A la derecha",
    settingsArrowLeft = "A la izquierda",
    settingsArrowBottom = "Abajo",
    arrowChoose = "ELIGE",
    sectionWriting = "CÓMO SE ESCRIBE",
    sectionWritingDetail = "La manera de elegir las letras. Empieza por aquí: todo lo demás depende de esto.",
    sectionTiming = "RITMO Y TEMBLORES",
    sectionTimingDetail = "Para una mano que tiembla, o que pulsa dos veces sin querer.",
    sectionWords = "PALABRAS Y LECTURA",
    sectionWordsDetail = "Qué sale en la fila de palabras, y cómo se ve la frase escrita.",
    sectionSwitches = "PULSADORES Y MANDOS",
    sectionSwitchesDetail = "Solo hace falta si hay un pulsador o un mando conectado.",
    sectionDevice = "EL DISPOSITIVO",
    sectionDeviceDetail = "Se configura una vez, al preparar el aparato, y no se vuelve.",
    settingsForgiveTitle = "Perdona los errores",
    settingsForgiveDetail = "Si una letra se repite sin querer, o se escapa, las palabras siguen saliendo. Las que encajan exactamente salen siempre primero.",
    settingsBoldWritingTitle = "Frase en negrita",
    settingsBoldWritingDetail = "La frase que escribe, más gruesa, para leerla de lejos.",
    settingsArrowShapeTitle = "Cómo se ven las flechas",
    settingsArrowShapeDetail = "Separadas, cada flecha tiene su botón y se ve hasta dónde llega. En cruz se ven como una sola pieza. Lo que se puede tocar es igual en los dos casos.",
    settingsArrowShapeJoined = "En cruz",
    settingsArrowShapeSeparate = "Separadas",
    settingsEraseKeysTitle = "Botones de borrar junto a ELIGE",
    settingsEraseKeysDetail = "Borra una letra, o todo, sin tener que ir con las flechas. BORRA TODO pregunta antes.",
    arrowDeleteLetter = "\u232b LETRA",
    arrowClearConfirm = "¿SEGURO?",
    batteryLow = "Batería baja",
    settingsTutorial = "CÓMO FUNCIONA",
    tutorialNext = "SIGUIENTE",
    tutorialBack = "ATRÁS",
    tutorialActiveMode = "ESTE ES EL MODO ACTIVO",
    tutorialPressed = "PULSA",
    tutorial = listOf(
        TutorialPage(
            title = "Para qué sirve",
            lines = listOf(
                "Sirve para que una persona que no puede hablar ni mover las " +
                    "manos pueda escribir y decir cosas.",
                "Ella hace un solo movimiento: tocar la pantalla. Todo lo " +
                    "demás lo tiene que hacer la aplicación.",
                "Tú eres quien la prepara. Esta guía explica cómo funciona, " +
                    "para que puedas explicárselo bien.",
            ),
        ),
        TutorialPage(
            title = "Dos maneras de elegir una letra",
            lines = listOf(
                "Hay dos, y se cambian en los ajustes. Ninguna es mejor: " +
                    "depende de la persona.",
                "ESCANEO AUTOMÁTICO: el recuadro rojo se mueve solo. Ella " +
                    "espera y pulsa cuando llega adonde quiere.",
                "FLECHAS: el recuadro no se mueve solo. Ella lo mueve con " +
                    "cuatro flechas y pulsa ELIGE.",
                "Si esperar la cansa, prueba las flechas. Si le cuesta " +
                    "acertar el momento exacto, también.",
            ),
        ),
        TutorialPage(
            title = "Las letras no son botones",
            demo = TutorialDemo.Touch,
            lines = listOf(
                "Esto es lo que más cuesta entender, y vale la pena leerlo dos " +
                    "veces.",
                "Las letras de la pantalla NO son botones. Tocar la letra que " +
                    "ella quiere no hace nada. Están para leerlas, no para " +
                    "pulsarlas.",
                "Con el escaneo automático hay dos botones y ocupan media " +
                    "pantalla cada uno: toda la mitad DERECHA escribe, toda la " +
                    "mitad IZQUIERDA deshace.",
                "No se ven, y es a propósito: la cuadrícula está para mirarla, " +
                    "y unas líneas encima solo taparían letras.",
                "Eso quiere decir que puede tocar donde le vaya bien de la " +
                    "mitad que sea. No tiene que acertar nada, y no puede " +
                    "fallar el botón.",
                "Con flechas es distinto: allí los botones sí se ven, son las " +
                    "cuatro flechas y ELIGE. Las letras siguen sin ser botones.",
                "Se puede desactivar en los ajustes, en TOCAR LA PANTALLA PARA " +
                    "ESCRIBIR, si alguien toca la pantalla sin querer.",
            ),
        ),
        TutorialPage(
            title = "Escaneo automático",
            demo = TutorialDemo.Scan,
            lines = listOf(
                "El recuadro rojo baja fila por fila, él solo.",
                "Cuando está en la fila que ella quiere, pulsa. Ahora el " +
                    "recuadro se mueve de letra en letra dentro de esa fila.",
                "Cuando está en la letra que quiere, vuelve a pulsar. La letra " +
                    "se escribe y todo empieza otra vez desde arriba.",
                "Si se le pasa la letra no hace falta nada: después de dos " +
                    "vueltas la fila se suelta sola.",
                "Tocar la mitad izquierda de la pantalla deshace lo último.",
            ),
        ),
        TutorialPage(
            title = "Flechas",
            demo = TutorialDemo.Arrows,
            lines = listOf(
                "El recuadro se queda quieto hasta que ella lo mueve. No hay " +
                    "nada que esperar y no se puede llegar tarde.",
                "Las cuatro flechas lo mueven una casilla cada vez. ELIGE " +
                    "escribe la letra marcada.",
                "Da la vuelta: desde la primera letra de una fila, la " +
                    "izquierda lleva a la última. Desde la fila de arriba, " +
                    "arriba lleva a la de abajo. Nada queda nunca lejos.",
                "Después de escribir, el recuadro se queda donde está. Dos " +
                    "letras iguales seguidas son dos pulsaciones de ELIGE.",
                "Junto a ELIGE están ⌫ LETRA, que borra la última letra, " +
                    "y BORRA TODO, que vacía la frase entera. BORRA TODO se " +
                    "pulsa dos veces: la primera se pone roja y dice " +
                    "¿SEGURO?, y solo la segunda vacía. Cualquier otra cosa " +
                    "que haga lo anula.",
                "La tecla ⌫ de la cuadrícula también borra una letra.",
                "Los espacios entre las flechas también cuentan: un toque que " +
                    "cae entre dos va a la más cercana.",
            ),
        ),
        TutorialPage(
            title = "La cuadrícula",
            lines = listOf(
                "Fila de arriba: palabras que sugiere la aplicación, y " +
                    "después SÍ, NO y FRASES.",
                "SÍ y NO solo están con la frase vacía. Al empezar a escribir, " +
                    "esas dos casillas pasan a ser dos palabras sugeridas más: " +
                    "cinco en vez de tres. Vuelven solas cuando se borra la " +
                    "frase.",
                "Las palabras sugeridas cambian mientras ella escribe. Elegir " +
                    "una escribe la palabra entera y el espacio: casi siempre " +
                    "es el camino más corto.",
                "También perdona errores: si una letra se repite sin querer o " +
                    "se escapa, las palabras siguen saliendo. Elegir una " +
                    "arregla la palabra entera, sin tener que borrar.",
                "Las letras no están en orden alfabético, sino por orden de " +
                    "uso, para que las más frecuentes queden cerca.",
                "× borra la frase entera, y por eso está en el último rincón.",
            ),
        ),
        TutorialPage(
            title = "Frases",
            lines = listOf(
                "FRASES abre una pantalla con frases enteras.",
                "Elegir una la dice en voz alta. No se escribe nada en el " +
                    "recuadro.",
                "Para volver a escribir: VUELVE, o tocar la mitad izquierda de " +
                    "la pantalla.",
            ),
        ),
        TutorialPage(
            title = "Los ajustes que importan",
            lines = listOf(
                "VELOCIDAD DEL ESCANEO: cuánto dura cada paso. Si se le " +
                    "escapan letras, hazlo más lento.",
                "TIEMPO EXTRA EN LA PRIMERA LETRA: al entrar en una fila, le " +
                    "da un momento más para reaccionar.",
                "TIEMPO MÍNIMO ENTRE PULSACIONES: si una sola pulsación " +
                    "escribe dos letras, súbelo.",
                "IGNORA LOS TEMBLORES: una ráfaga de toques seguidos cuenta " +
                    "una sola vez.",
                "Cambia una cosa cada vez y pruébala. Cambiarlo todo a la vez " +
                    "no dice nada sobre qué ha ayudado.",
            ),
        ),
        TutorialPage(
            title = "Modo bloqueado y batería",
            lines = listOf(
                "Con el modo bloqueado, la aplicación se queda en la pantalla " +
                    "y no se sale de ella sin querer.",
                "Para salir: la rueda ⚙ de arriba a la derecha, y desactívalo " +
                    "en los ajustes. Siempre se puede.",
                "La batería se ve arriba a la derecha. Se pone roja por debajo " +
                    "del 20 %.",
                "Si se queda sin batería se queda sin voz. Vale la pena " +
                    "dejarla enchufada.",
            ),
        ),
        TutorialPage(
            title = "Ya está",
            lines = listOf(
                "Cierra esto y pruébalo tú unos minutos antes de explicárselo " +
                    "a ella. Es la mejor manera de explicarlo bien.",
                "Si algo no le va bien, casi siempre es un ajuste y no la " +
                    "aplicación. Empieza por la velocidad.",
                "Puedes volver a abrir esta guía cuando quieras: ⚙ → CÓMO " +
                    "FUNCIONA.",
            ),
        ),
    ),
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
 *
 * @param withDelete adds a backspace key. Arrow mode has one button and no undo
 *   switch, so without it there would be no way to take a letter back.
 */
fun keyboardFor(language: Language, withDelete: Boolean = false): List<List<Key>> {
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
    // Arrow mode has one button and no undo switch, so backspace has to be a
    // cell she can steer to. It sits beside the clear key, at the far end,
    // where nothing is ever reached by accident.
    if (withDelete) rows[rows.size - 1] = rows.last() + Key.Delete
    return rows
}
