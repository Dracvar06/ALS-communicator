package cat.merce.comunicador.ui

/**
 * French. Letters are in order of frequency in French text (E, S, A, I, N, T
 * lead); accents are reached through the suggestions, and Ç is on the grid.
 */
val FRENCH = Language(
    code = "fr",
    displayName = "Français",
    modelAsset = "fr-model.txt",
    ttsLocale = "fr",
    letterRows = listOf(
        listOf("E", "S", "A", "I", "U"),
        listOf("T", "N", "R", "O", "L", "C"),
        listOf("M", "D", "P", "V", "J", "Q"),
        listOf("F", "B", "H", "G", "Z", "Y"),
        listOf("X", "Ç", "K", "W"),
    ),
    openers = listOf(
        "je", "veux", "besoin", "aide", "s'il",
        "eau", "mal", "toilettes", "manger", "froid",
        "chaud", "fatiguée", "attends", "stop", "bonjour",
        "oui", "merci", "peux", "où", "quoi",
    ),
    defaultPhrases = listOf(
        "Bonjour",
        "Je ne me sens pas bien",
        "De l'eau",
        "Je veux aller au jardin",
        "Je veux aller à la salle à manger",
        "J'ai froid",
        "J'ai chaud",
        "J'ai besoin d'aller aux toilettes",
        "Prévenez mes filles",
        "Appelez l'infirmière",
        "Attendez un instant",
        "Je vais bien",
        "Merci",
        "Je t'aime",
    ),
    spaceLabel = "espace",
    clearLabel = "TOUT EFFACER",
    yesLabel = "OUI",
    noLabel = "NON",
    phrasesLabel = "PHRASES",
    backLabel = "RETOUR",
    settingsSpeed = "Vitesse du balayage",
    settingsSecondsPerStep = "secondes par pas",
    settingsFaster = "plus rapide",
    settingsSlower = "plus lent",
    settingsFirstLetterExtra = "Temps supplémentaire sur la première lettre",
    settingsMinBetweenPresses = "Temps minimum entre deux appuis",
    settingsTouchTitle = "Toucher l'écran pour écrire",
    settingsTouchDetail = "Droite : écrit. Gauche : annule.",
    settingsTremorTitle = "Ignorer les tremblements",
    settingsTremorDetail = "Une rafale d'appuis rapprochés ne compte qu'une fois.",
    settingsButtons = "Boutons",
    settingsWrite = "Écrire",
    settingsUndo = "Annuler",
    settingsAssignWrite = "ASSIGNER ÉCRIRE",
    settingsAssignUndo = "ASSIGNER ANNULER",
    settingsClose = "FERMER",
    settingsCheckButtons = "VÉRIFIER LES CONTACTEURS",
    settingsEitherCloses = "L'un ou l'autre des contacteurs ferme aussi cet écran.",
    settingsLanguage = "Langue",
    settingsLockedTitle = "Mode verrouillé",
    settingsLockedDetail = "L'application reste à l'écran et on ne peut pas la quitter. Désactivez ceci ici pour en sortir.",
    settingsBootTitle = "S'ouvrir au redémarrage",
    settingsBootDetail = "L'application s'ouvre toute seule quand l'appareil redémarre.",
    settingsModeTitle = "Comment choisir une lettre",
    settingsModeDetail = "Avec le balayage, la surbrillance se déplace seule et on attend. " +
        "Avec les flèches, on la déplace soi-même, sans jamais attendre.",
    settingsModeScan = "Balayage automatique",
    settingsModeArrows = "Flèches",
    settingsModeDirect = "Toucher les lettres",
    settingsArrowPlaceTitle = "Où placer les flèches",
    settingsArrowPlaceDetail = "Placez-les là où le bras ne passe pas au-dessus " +
        "pour aller appuyer. En bas, toute la largeur de l'écran sépare " +
        "CHOISIR des flèches, mais les flèches sont plus petites.",
    settingsArrowColumn = "En colonne, sur un côté",
    settingsArrowBar = "En bas, en bandeau",
    settingsChooseTop = "CHOISIR en haut",
    settingsChooseLeft = "CHOISIR à gauche",
    settingsChooseDetail = "Placez CHOISIR là où le bras ne passe pas au-dessus.",
    settingsArrowRight = "À droite",
    settingsArrowLeft = "À gauche",
    settingsArrowBottom = "En bas",
    arrowChoose = "CHOISIR",
    sectionWriting = "COMMENT ON ÉCRIT",
    sectionWritingDetail = "La façon de choisir les lettres. Commencez ici : tout le reste de cette section en dépend.",
    sectionTiming = "RYTHME ET TREMBLEMENTS",
    sectionTimingDetail = "Pour une main qui tremble, ou qui appuie deux fois sans le vouloir.",
    sectionWords = "MOTS ET LECTURE",
    sectionWordsDetail = "Ce qui apparaît dans la rangée des mots, et l'aspect de la phrase écrite.",
    sectionSwitches = "CONTACTEURS ET MANETTES",
    sectionSwitchesDetail = "Nécessaire seulement si un contacteur ou une manette est branché.",
    sectionDevice = "L'APPAREIL",
    sectionDeviceDetail = "À régler une fois, en préparant l'appareil, puis à laisser tel quel.",
    settingsForgiveTitle = "Pardonner les erreurs",
    settingsForgiveDetail = "Si une lettre est répétée sans le vouloir, ou n'est pas saisie, les mots continuent d'apparaître. Les correspondances exactes passent toujours en premier.",
    settingsAiTitle = "Suggestions par IA (expérimental)",
    settingsAiDetail = "Un petit modèle de langage, dans la tablette et sans internet, réordonne les mots suggérés selon ce qui a été écrit. Les mots sont les mêmes ; seul leur ordre change. Si la tablette n'est pas assez rapide, il s'éteint tout seul.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Désactivé."
            AiStatus.Loading -> "Chargement… La première fois, cela prend un peu de temps."
            AiStatus.On -> "En marche."
            AiStatus.NotEnoughMemory -> "Cette tablette n'a pas assez de mémoire. Les suggestions normales fonctionnent comme avant."
            AiStatus.Failed -> "Le chargement a échoué. Les suggestions normales fonctionnent comme avant."
            AiStatus.TooSlow -> "Il s'est éteint tout seul : cette tablette était trop lente. Les suggestions normales fonctionnent comme avant."
        }
    },
    settingsBoldWritingTitle = "Phrase en gras",
    settingsBoldWritingDetail = "La phrase qu'elle écrit, en plus épais, pour la lire de loin.",
    settingsArrowShapeTitle = "Aspect des flèches",
    settingsArrowShapeDetail = "Séparées, chaque flèche a son propre bouton et on voit jusqu'où il s'étend. Réunies, elles forment une seule figure. Ce qui répond au toucher est identique dans les deux cas.",
    settingsArrowShapeJoined = "Réunies",
    settingsArrowShapeSeparate = "Séparées",
    settingsEraseKeysTitle = "Boutons d'effacement à côté de CHOISIR",
    settingsEraseKeysDetail = "Effacer une lettre, ou tout, sans avoir à s'y rendre avec les flèches. TOUT EFFACER demande confirmation.",
    arrowDeleteLetter = "⌫ LETTRE",
    arrowClearConfirm = "SÛR ?",
    batteryLow = "Batterie faible",
    settingsTutorial = "COMMENT ÇA MARCHE",
    tutorialNext = "SUIVANT",
    tutorialBack = "RETOUR",
    tutorialActiveMode = "C'EST LE MODE UTILISÉ",
    tutorialPressed = "APPUI",
    tutorial = listOf(
        TutorialPage(
            title = "À quoi ça sert",
            lines = listOf(
                "Cela permet à une personne qui ne peut ni parler ni bouger " +
                    "les mains d'écrire et de dire des choses à voix haute.",
                "Selon ce qu'elle peut faire, elle touche les lettres, déplace " +
                    "une surbrillance avec des flèches, ou fait un seul " +
                    "mouvement pendant que l'application fait le reste.",
                "C'est vous qui la préparez. Ce guide explique comment elle " +
                    "fonctionne, pour que vous puissiez bien la lui expliquer.",
            ),
        ),
        TutorialPage(
            title = "Les lettres ne sont pas des boutons",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "C'est la partie que l'on comprend le moins bien, et elle " +
                    "mérite d'être lue deux fois.",
                "Les lettres à l'écran ne sont PAS des boutons. Toucher la " +
                    "lettre qu'elle veut ne fait rien. Elles sont là pour être " +
                    "lues, pas pour être pressées.",
                "Avec le balayage automatique, il y a deux boutons qui " +
                    "occupent chacun la moitié de l'écran : toute la moitié " +
                    "DROITE écrit, toute la moitié GAUCHE annule.",
                "Ils sont invisibles exprès : la grille est faite pour être " +
                    "regardée, et des lignes tracées par-dessus ne feraient que " +
                    "cacher des lettres.",
                "Cela veut dire qu'elle peut toucher où cela lui convient dans " +
                    "la moitié voulue. Il n'y a rien à viser, et elle ne peut " +
                    "pas rater le bouton.",
                "Avec les flèches, c'est différent : là, les boutons sont " +
                    "visibles — les quatre flèches et CHOISIR. Les lettres ne " +
                    "sont toujours pas des boutons.",
                "On peut désactiver cela dans les réglages, sous TOUCHER " +
                    "L'ÉCRAN POUR ÉCRIRE, si quelqu'un touche l'écran sans le " +
                    "vouloir.",
            ),
        ),
        TutorialPage(
            title = "Balayage automatique",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "La surbrillance rouge descend rangée par rangée, toute seule.",
                "Quand elle est sur la rangée voulue, elle appuie. La " +
                    "surbrillance avance alors lettre par lettre le long de " +
                    "cette rangée.",
                "Quand elle est sur la lettre voulue, elle appuie de nouveau. " +
                    "La lettre est écrite et tout recommence par le haut.",
                "Si la lettre passe, elle n'a rien à faire : après deux " +
                    "passages, la rangée se relâche toute seule et la " +
                    "surbrillance redescend.",
                "Toucher la moitié gauche de l'écran annule la dernière action.",
            ),
        ),
        TutorialPage(
            title = "Flèches",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "La surbrillance reste immobile tant qu'elle ne la déplace " +
                    "pas. Il n'y a rien à attendre et on ne peut pas être en retard.",
                "Les quatre flèches la déplacent d'une case à la fois. " +
                    "CHOISIR écrit la lettre en surbrillance.",
                "Cela fait le tour : depuis la première lettre d'une rangée, " +
                    "gauche mène à la dernière. Depuis la rangée du haut, haut " +
                    "mène à celle du bas. Rien n'est jamais loin.",
                "Après avoir écrit, la surbrillance reste où elle est. Une " +
                    "lettre doublée, ce sont deux appuis sur CHOISIR, rien d'autre.",
                "À côté de CHOISIR, il y a ⌫ LETTRE, qui efface la dernière " +
                    "lettre, et TOUT EFFACER, qui vide la phrase entière. " +
                    "TOUT EFFACER demande deux appuis : le premier le fait " +
                    "passer au rouge et afficher SÛR ?, et seul le second vide " +
                    "la phrase. Toute autre action de sa part l'annule.",
                "La touche ⌫ de la grille efface aussi une lettre.",
                "Les espaces entre les flèches comptent aussi : un toucher qui " +
                    "tombe entre deux va vers la plus proche.",
            ),
        ),
        TutorialPage(
            title = "Toucher les lettres",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Ici, et seulement ici, les lettres sont des boutons : elle " +
                    "touche celle qu'elle veut et elle s'écrit.",
                "C'est de loin la façon la plus rapide, mais seulement tant " +
                    "qu'elle peut poser un doigt sur une touche précise. Si " +
                    "les appuis se mettent à tomber sur la touche voisine, " +
                    "essayez les flèches.",
                "La touche ⌫ de la grille efface la dernière lettre.",
                "IGNORER LES TREMBLEMENTS, dans les réglages, fait qu'une " +
                    "rafale d'appuis rapprochés ne compte qu'une fois.",
            ),
        ),
        TutorialPage(
            title = "La grille",
            lines = listOf(
                "Rangée du haut : les mots que l'application suggère, puis " +
                    "OUI, NON et PHRASES.",
                "OUI et NON ne sont là que lorsque la phrase est vide. Dès " +
                    "qu'elle commence à écrire, ces deux cases deviennent deux " +
                    "mots suggérés de plus : cinq au lieu de trois. Elles " +
                    "reviennent toutes seules quand la phrase est effacée.",
                "Les mots suggérés changent pendant qu'elle écrit. En choisir " +
                    "un écrit le mot entier et l'espace qui suit, ce qui est " +
                    "souvent de loin le chemin le plus court.",
                "Cela pardonne aussi les erreurs : si une lettre est répétée " +
                    "ou n'est pas saisie, les mots continuent d'apparaître. En " +
                    "choisir un corrige le mot entier, sans rien effacer.",
                "Les lettres ne sont pas dans l'ordre alphabétique. Elles sont " +
                    "classées selon leur fréquence d'emploi en français, pour " +
                    "que les plus courantes soient proches.",
                "× efface toute la phrase, c'est pourquoi il est dans le " +
                    "dernier coin de la grille.",
            ),
        ),
        TutorialPage(
            title = "Phrases",
            lines = listOf(
                "PHRASES ouvre un écran de phrases toutes faites.",
                "En choisir une la dit à voix haute. Rien n'est écrit dans la " +
                    "zone de texte.",
                "Pour revenir à l'écriture : RETOUR, ou toucher la moitié " +
                    "gauche de l'écran.",
            ),
        ),
        TutorialPage(
            title = "Les réglages qui comptent",
            lines = listOf(
                "VITESSE DU BALAYAGE : la durée de chaque pas. Si des lettres " +
                    "lui échappent, ralentissez-la.",
                "TEMPS SUPPLÉMENTAIRE SUR LA PREMIÈRE LETTRE : lui laisse un " +
                    "instant de plus pour réagir juste après être entrée dans " +
                    "une rangée.",
                "TEMPS MINIMUM ENTRE DEUX APPUIS : si un seul appui écrit deux " +
                    "lettres, augmentez-le.",
                "IGNORER LES TREMBLEMENTS : une rafale d'appuis rapprochés ne " +
                    "compte qu'une fois.",
                "Changez une seule chose à la fois et essayez. Tout changer " +
                    "d'un coup ne vous apprend rien sur ce qui a aidé.",
            ),
        ),
        TutorialPage(
            title = "Mode verrouillé et batterie",
            lines = listOf(
                "En mode verrouillé, l'application reste à l'écran et on ne " +
                    "peut pas la quitter par accident.",
                "Pour en sortir : le ⚙ en haut à droite, puis désactivez-le " +
                    "dans les réglages. C'est toujours possible.",
                "La batterie s'affiche en haut à droite. Elle devient rouge " +
                    "sous 20 %.",
                "Une batterie à plat, c'est une personne sans voix. Mieux " +
                    "vaut la laisser branchée.",
            ),
        ),
        TutorialPage(
            title = "C'est tout",
            lines = listOf(
                "Fermez ceci et essayez vous-même pendant quelques minutes " +
                    "avant de la lui expliquer. C'est la seule façon de bien " +
                    "l'expliquer.",
                "Si quelque chose ne lui convient pas, c'est presque toujours " +
                    "un réglage et non l'application. Commencez par la vitesse.",
                "Vous pouvez rouvrir ce guide quand vous voulez : ⚙ → COMMENT " +
                    "ÇA MARCHE.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "La préparer",
        welcomeLines = listOf(
            "Cette application permet à une personne qui ne peut ni parler " +
                "ni écrire à la main de composer des phrases et de les dire " +
                "à voix haute.",
            "Il y a plusieurs façons de l'utiliser. Deux questions sur " +
                "elle, et l'application sera réglée comme il lui convient.",
            "Toutes les réponses peuvent être modifiées plus tard dans les " +
                "réglages (⚙, en haut à droite).",
        ),
        start = "COMMENCER",
        skip = "PASSER",
        methodTitle = "Comment choisira-t-elle une lettre ?",
        methodHint = "Vous hésitez ? Commencez par ce qui lui demande le " +
            "moins. Si l'attente la fatigue, ou s'il est difficile de " +
            "viser le bon moment, essayez les flèches.",
        methodDirectDetail = "Elle peut toucher à coup sûr une touche " +
            "précise. La façon la plus rapide, tant que cela dure.",
        methodArrowsDetail = "Elle peut toucher quelques gros boutons, mais " +
            "pas de petites touches. Rien ne bouge tant qu'elle ne le " +
            "déplace pas.",
        methodScanDetail = "Elle peut faire un seul mouvement, avec un " +
            "contacteur ou en touchant l'écran. Une surbrillance avance " +
            "toute seule et elle appuie quand elle arrive sur ce qu'elle " +
            "veut.",
        pressTitle = "Sur quoi appuiera-t-elle ?",
        pressHint = "Un contacteur est un bouton relié à la tablette par " +
            "Bluetooth. Il faut d'abord l'appairer dans les réglages " +
            "Bluetooth de la tablette.",
        pressScreen = "L'écran",
        pressScreenDetail = "La moitié droite de l'écran écrit, la moitié " +
            "gauche annule. Aucun contacteur n'est nécessaire.",
        pressSwitches = "Contacteurs",
        pressSwitchesDetail = "Un contacteur écrit, un autre annule. " +
            "Toucher l'écran ne fait rien, donc un bras posé dessus ne " +
            "gêne pas.",
        pressBoth = "Les deux",
        pressBothDetail = "Les contacteurs, et aussi les moitiés de l'écran.",
    ),
    bindPressFor = "Appuyez sur le bouton pour",
    bindButtonFor = "Bouton pour",
    bindWrite = "ÉCRIRE",
    bindUndo = "ANNULER",
    bindAssigned = "Assignés",
    bindPressNow = "Appuyez maintenant, sur la manette ou le contacteur, sur le bouton que vous voulez utiliser.",
    bindAnother = "Ajouter un autre bouton pour la même action ?",
    bindAddAnother = "EN AJOUTER UN AUTRE",
    bindDone = "TERMINÉ",
    bindCancel = "ANNULER",
    checkTitle = "Vérification des contacteurs",
    checkHint = "Appuyez sur chaque contacteur. Cet écran se ferme tout seul.",
    checkWaiting = "En attente…",
    checkCode = "code",
    checkRoleWrite = "écrit",
    checkRoleUndo = "annule",
    checkRoleUnassigned = "non assigné",
)
