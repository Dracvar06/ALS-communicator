package cat.merce.comunicador.ui

/**
 * Italian. Letters are in order of frequency in Italian text (E, A, I, O, N
 * lead). Accents are reached through the suggestions, as in the other languages.
 */
val ITALIAN = Language(
    code = "it",
    experimentalLabel = "sperimentale",
    displayName = "Italiano",
    modelAsset = "it-model.txt",
    ttsLocale = "it",
    letterRows = listOf(
        listOf("E", "A", "O", "I", "N"),
        listOf("T", "R", "S", "L", "C", "U"),
        listOf("D", "M", "P", "V", "H", "G"),
        listOf("B", "F", "Q", "Z", "Y", "K"),
        listOf("W", "J", "X"),
    ),
    openers = listOf(
        "voglio", "ho", "bisogno", "aiuto", "per",
        "acqua", "dolore", "bagno", "mangiare", "freddo",
        "caldo", "riposo", "aspetta", "basta", "ciao",
        "sì", "grazie", "posso", "dove", "cosa",
    ),
    defaultPhrases = listOf(
        "Buongiorno",
        "Mi sento male",
        "L'acqua",
        "Voglio andare in giardino",
        "Voglio andare in sala da pranzo",
        "Ho freddo",
        "Ho caldo",
        "Ho bisogno di andare in bagno",
        "Avvisate la mia famiglia",
        "Chiamate aiuto, per favore",
        "Aspetta un momento",
        "Sto bene",
        "Grazie",
        "Ti voglio bene",
    ),
    spaceLabel = "spazio",
    clearLabel = "CANCELLA TUTTO",
    yesLabel = "SÌ",
    noLabel = "NO",
    phrasesLabel = "FRASI",
    backLabel = "INDIETRO",
    settingsSpeed = "Velocità della scansione",
    settingsSecondsPerStep = "secondi per passo",
    settingsFaster = "più veloce",
    settingsSlower = "più lento",
    settingsFirstLetterExtra = "Tempo extra sulla prima lettera",
    settingsMinBetweenPresses = "Tempo minimo tra le pressioni",
    settingsTouchTitle = "Toccare lo schermo per scrivere",
    settingsTouchDetail = "Destra: scrive. Sinistra: annulla.",
    settingsTremorTitle = "Ignora i tremori",
    settingsTremorDetail = "Una raffica di tocchi ravvicinati conta una sola volta.",
    settingsButtons = "Pulsanti",
    settingsWrite = "Scrivere",
    settingsUndo = "Annullare",
    settingsAssignWrite = "ASSEGNA SCRIVERE",
    settingsAssignUndo = "ASSEGNA ANNULLARE",
    settingsClose = "CHIUDI",
    settingsCheckButtons = "CONTROLLA I PULSANTI",
    settingsEitherCloses = "Anche uno qualsiasi dei due pulsanti chiude questa schermata.",
    settingsLanguage = "Lingua",
    settingsLockedTitle = "Modalità bloccata",
    settingsLockedDetail = "L'app resta sullo schermo e non si può uscire. Disattiva questa opzione qui per uscire.",
    settingsBootTitle = "Si apre all'accensione",
    settingsBootDetail = "L'app si apre da sola quando il dispositivo si riavvia.",
    settingsModeTitle = "Come si sceglie una lettera",
    settingsModeDetail = "Con la scansione l'evidenziazione si muove da sola e si aspetta. " +
        "Con le frecce la si muove da sé, senza mai aspettare.",
    settingsModeScan = "Scansione automatica",
    settingsModeArrows = "Frecce",
    settingsModeDirect = "Toccare le lettere",
    settingsArrowPlaceTitle = "Dove mettere le frecce",
    settingsArrowPlaceDetail = "Mettile dove il braccio non passa sopra " +
        "mentre va a premere. In basso, tra SCEGLI e le frecce c'è tutta " +
        "la larghezza dello schermo, ma le frecce risultano più piccole.",
    settingsArrowColumn = "In colonna, su un lato",
    settingsArrowBar = "In basso, in una striscia",
    settingsChooseTop = "SCEGLI in alto",
    settingsChooseLeft = "SCEGLI a sinistra",
    settingsChooseDetail = "Metti SCEGLI dove il braccio non passa sopra.",
    settingsArrowRight = "A destra",
    settingsArrowLeft = "A sinistra",
    settingsArrowBottom = "In basso",
    arrowChoose = "SCEGLI",
    sectionWriting = "COME SI SCRIVE",
    sectionWritingDetail = "Il modo di scegliere le lettere. Inizia da qui: tutto il resto di questa sezione dipende da questo.",
    sectionTiming = "RITMO E TREMORI",
    sectionTimingDetail = "Per una mano che trema, o che preme due volte senza volerlo.",
    sectionWords = "PAROLE E LETTURA",
    sectionWordsDetail = "Cosa compare nella riga delle parole e come appare la frase scritta.",
    sectionSwitches = "PULSANTI E TELECOMANDI",
    sectionSwitchesDetail = "Serve solo se è collegato un pulsante o un telecomando.",
    sectionDevice = "IL DISPOSITIVO",
    sectionDeviceDetail = "Si imposta una volta, mentre si prepara il dispositivo, e poi non si tocca più.",
    settingsForgiveTitle = "Perdona gli errori",
    settingsForgiveDetail = "Se una lettera viene ripetuta senza volerlo, o ne sfugge una, le parole escono comunque. Quelle che corrispondono esattamente escono sempre per prime.",
    settingsAiTitle = "Suggerimenti con IA (sperimentale)",
    settingsAiDetail = "Un piccolo modello linguistico, dentro il tablet e senza internet, riordina le parole suggerite in base a ciò che è stato scritto. Le parole sono le stesse; cambia solo l'ordine. Se il tablet non è abbastanza veloce, si spegne da solo.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Spento."
            AiStatus.Loading -> "Caricamento… La prima volta ci vuole un po'."
            AiStatus.On -> "Attivo."
            AiStatus.NotEnoughMemory -> "Questo tablet non ha abbastanza memoria. I suggerimenti normali funzionano come prima."
            AiStatus.Failed -> "Non è stato possibile caricarlo. I suggerimenti normali funzionano come prima."
            AiStatus.TooSlow -> "Si è spento da solo: questo tablet era troppo lento. I suggerimenti normali funzionano come prima."
        }
    },
    settingsBoldWritingTitle = "Frase in grassetto",
    settingsBoldWritingDetail = "La frase che si sta scrivendo, più spessa, per leggerla da lontano.",
    settingsArrowShapeTitle = "Come si vedono le frecce",
    settingsArrowShapeDetail = "Separate, ogni freccia ha il suo pulsante e si vede fin dove arriva. Unite, si vedono come un unico pezzo. Ciò che risponde al tocco è uguale nei due casi.",
    settingsArrowShapeJoined = "Unite",
    settingsArrowShapeSeparate = "Separate",
    settingsEraseKeysTitle = "Pulsanti di cancellazione accanto a SCEGLI",
    settingsEraseKeysDetail = "Cancella una lettera, o tutto, senza dover andare lì con le frecce. CANCELLA TUTTO chiede conferma.",
    arrowDeleteLetter = "⌫ LETTERA",
    arrowClearConfirm = "SICURO?",
    batteryLow = "Batteria scarica",
    settingsTutorial = "COME FUNZIONA",
    tutorialNext = "AVANTI",
    tutorialBack = "INDIETRO",
    tutorialActiveMode = "QUESTA È LA MODALITÀ IN USO",
    tutorialPressed = "PREMI",
    tutorial = listOf(
        TutorialPage(
            title = "A cosa serve",
            lines = listOf(
                "Permette a una persona che non può parlare né muovere le " +
                    "mani di scrivere e di dire le cose ad alta voce.",
                "A seconda di ciò che la persona riesce a fare, tocca le " +
                    "lettere, sposta un'evidenziazione con le frecce, o fa un " +
                    "solo movimento mentre l'app fa il resto.",
                "Sei tu a preparare l'app. Questa guida spiega come " +
                    "funziona, così potrai spiegarlo bene alla persona.",
            ),
        ),
        TutorialPage(
            title = "Le lettere non sono pulsanti",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "È la parte che si capisce meno, e vale la pena leggerla " +
                    "due volte.",
                "Le lettere sullo schermo NON sono pulsanti. Toccare la " +
                    "lettera desiderata non fa nulla. Servono per essere lette, " +
                    "non per essere premute.",
                "Con la scansione automatica i pulsanti sono due e occupano " +
                    "metà schermo ciascuno: tutta la metà DESTRA scrive, tutta " +
                    "la metà SINISTRA annulla.",
                "Sono invisibili di proposito: la griglia serve per essere " +
                    "guardata, e delle linee disegnate sopra coprirebbero solo " +
                    "delle lettere.",
                "Questo significa che si può toccare dove è più comodo " +
                    "nella metà giusta. Non c'è nulla da centrare e non si " +
                    "può sbagliare pulsante.",
                "Con le frecce è diverso: lì i pulsanti si vedono, sono le " +
                    "quattro frecce e SCEGLI. Le lettere continuano a non " +
                    "essere pulsanti.",
                "Si può disattivare nelle impostazioni, in TOCCARE LO " +
                    "SCHERMO PER SCRIVERE, se qualcuno tocca lo schermo senza " +
                    "volerlo.",
            ),
        ),
        TutorialPage(
            title = "Scansione automatica",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "L'evidenziazione rossa scende riga per riga, da sola.",
                "Quando è sulla riga desiderata, la persona preme. Ora " +
                    "l'evidenziazione si muove lettera per lettera lungo " +
                    "quella riga.",
                "Quando è sulla lettera desiderata, preme di nuovo. La " +
                    "lettera viene scritta e tutto ricomincia dall'alto.",
                "Se la lettera passa, non c'è bisogno di fare nulla: dopo due " +
                    "giri la riga si lascia da sola e l'evidenziazione " +
                    "ricomincia a scendere.",
                "Toccare la metà sinistra dello schermo annulla l'ultima cosa.",
            ),
        ),
        TutorialPage(
            title = "Frecce",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "L'evidenziazione resta ferma finché la persona non la muove. Non " +
                    "c'è nulla da aspettare e non si può arrivare in ritardo.",
                "Le quattro frecce la spostano di una casella alla volta. " +
                    "SCEGLI scrive la lettera evidenziata.",
                "Si torna al punto di partenza: dalla prima lettera di una " +
                    "riga, sinistra porta all'ultima. Dalla riga in alto, su " +
                    "porta a quella in basso. Niente è mai lontano.",
                "Dopo aver scritto, l'evidenziazione resta dov'è. Una lettera " +
                    "doppia sono due pressioni di SCEGLI, e nient'altro.",
                "Accanto a SCEGLI ci sono ⌫ LETTERA, che cancella l'ultima " +
                    "lettera, e CANCELLA TUTTO, che svuota l'intera frase. " +
                    "CANCELLA TUTTO si preme due volte: la prima diventa " +
                    "rosso e mostra SICURO?, e solo la seconda svuota la " +
                    "frase. Qualsiasi altra azione lo annulla.",
                "Anche il tasto ⌫ sulla griglia cancella una lettera.",
                "Contano anche gli spazi tra le frecce: un tocco che cade " +
                    "tra due va a quella più vicina.",
            ),
        ),
        TutorialPage(
            title = "Toccare le lettere",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Qui, e solo qui, le lettere sono pulsanti: basta toccare " +
                    "quella voluta e viene scritta.",
                "È di gran lunga il modo più veloce, ma solo finché la persona riesce a " +
                    "posare un dito su un tasto preciso. Se i tocchi " +
                    "cominciano a finire sul tasto accanto, prova con le " +
                    "frecce.",
                "Il tasto ⌫ sulla griglia cancella l'ultima lettera.",
                "IGNORA I TREMORI, nelle impostazioni, fa sì che una raffica " +
                    "di tocchi ravvicinati conti una volta sola.",
            ),
        ),
        TutorialPage(
            title = "La griglia",
            lines = listOf(
                "Riga in alto: parole che l'app suggerisce, poi SÌ, NO e " +
                    "FRASI.",
                "SÌ e NO ci sono solo quando la frase è vuota. Appena si " +
                    "inizia a scrivere, quelle due caselle diventano due " +
                    "parole suggerite in più: cinque invece di tre. Tornano da " +
                    "sole quando la frase viene cancellata.",
                "Le parole suggerite cambiano mentre si scrive. Sceglierne " +
                    "una scrive la parola intera e lo spazio dopo, e spesso è " +
                    "di gran lunga la strada più breve.",
                "Perdona anche gli errori: se una lettera viene ripetuta o " +
                    "ne sfugge una, le parole continuano a uscire. Sceglierne " +
                    "una corregge l'intera parola, senza cancellare nulla.",
                "Le lettere non sono in ordine alfabetico, ma in ordine di " +
                    "frequenza d'uso in italiano, così le più comuni sono " +
                    "vicine.",
                "× cancella tutta la frase, ed è per questo che si trova " +
                    "nell'ultimo angolo della griglia.",
            ),
        ),
        TutorialPage(
            title = "Frasi",
            lines = listOf(
                "FRASI apre una schermata con frasi intere.",
                "Sceglierne una la pronuncia ad alta voce. Non viene scritto " +
                    "nulla nella casella di testo.",
                "Per tornare a scrivere: INDIETRO, oppure toccare la metà " +
                    "sinistra dello schermo.",
            ),
        ),
        TutorialPage(
            title = "Le impostazioni che contano",
            lines = listOf(
                "VELOCITÀ DELLA SCANSIONE: quanto dura ogni passo. Se " +
                    "alla persona sfuggono delle lettere, rendila più lenta.",
                "TEMPO EXTRA SULLA PRIMA LETTERA: dà un momento in più " +
                    "per reagire subito dopo l'ingresso in una riga.",
                "TEMPO MINIMO TRA LE PRESSIONI: se una sola pressione scrive " +
                    "due lettere, aumentalo.",
                "IGNORA I TREMORI: una raffica di tocchi ravvicinati conta " +
                    "una sola volta.",
                "Cambia una cosa alla volta e provala. Cambiare tutto " +
                    "insieme non ti dice nulla su cosa abbia aiutato.",
            ),
        ),
        TutorialPage(
            title = "Modalità bloccata e batteria",
            lines = listOf(
                "Con la modalità bloccata l'app resta sullo schermo e non si " +
                    "esce per sbaglio.",
                "Per uscire: l'icona ⚙ in alto a destra, poi disattivala " +
                    "nelle impostazioni. Si può sempre.",
                "La batteria si vede in alto a destra. Diventa rossa sotto " +
                    "il 20%.",
                "Una batteria scarica è una persona senza voce. Conviene " +
                    "lasciarla in carica.",
            ),
        ),
        TutorialPage(
            title = "Ecco tutto",
            lines = listOf(
                "Chiudi questa guida e prova tu per qualche minuto " +
                    "prima di spiegarlo alla persona. È l'unico modo per " +
                    "spiegarlo bene.",
                "Se qualcosa non funziona per la persona, quasi sempre è " +
                    "un'impostazione e non l'app. Comincia dalla velocità.",
                "Puoi riaprire questa guida quando vuoi: ⚙ → COME " +
                    "FUNZIONA.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "La messa a punto",
        welcomeLines = listOf(
            "Questa app permette a una persona che non può parlare né " +
                "scrivere a mano di comporre frasi e dirle ad alta voce.",
            "Ci sono diversi modi di usarla. Due domande su chi la userà, e " +
                "l'app sarà impostata nel modo più adatto.",
            "Ogni risposta si può cambiare più tardi nelle impostazioni " +
                "(⚙, in alto a destra).",
        ),
        start = "INIZIA",
        methodTitle = "Come sceglierà una lettera la persona?",
        methodHint = "Non sai quale? Parti da quello che chiede di " +
            "meno alla persona. Se aspettare è faticoso, o se è difficile " +
            "cogliere il momento giusto, prova con le frecce.",
        methodDirectDetail = "Riesce a toccare con sicurezza un tasto " +
            "preciso. Il modo più veloce, finché dura.",
        methodArrowsDetail = "Riesce a toccare pochi pulsanti grandi, ma " +
            "non tasti piccoli. Niente si muove finché non lo si muove.",
        methodScanDetail = "Riesce a fare un solo movimento, con un " +
            "pulsante o con un tocco. Un'evidenziazione si muove da sola e " +
            "si preme quando arriva su ciò che si vuole.",
        pressTitle = "Cosa premerà?",
        pressHint = "Un pulsante esterno si collega al tablet via " +
            "Bluetooth. Prima va abbinato nelle impostazioni Bluetooth del " +
            "tablet.",
        pressScreen = "Lo schermo",
        pressScreenDetail = "La metà destra dello schermo scrive, la metà " +
            "sinistra annulla. Non serve nessun pulsante.",
        pressSwitches = "Pulsanti",
        pressSwitchesDetail = "Un pulsante scrive, un altro annulla. " +
            "Toccare lo schermo non fa nulla, quindi un braccio appoggiato " +
            "sopra non dà fastidio.",
        pressBoth = "Entrambi",
        pressBothDetail = "I pulsanti, e anche le metà dello schermo.",
        practiceWord = "CIAO",
        practiceWrite = "Prova: scrivere",
        practiceWriteScan = "Aspetta che l'evidenziazione arrivi sulla riga, " +
            "premi, poi premi di nuovo sulla lettera.",
        practiceWriteArrows = "Sposta l'evidenziazione con le frecce, poi premi " +
            "SCEGLI su ogni lettera.",
        practiceWriteDirect = "Tocca una lettera alla volta.",
        practiceUndo = "Ora torna indietro di una lettera",
        practiceUndoScan = "Premi il pulsante per annullare, oppure tocca la " +
            "metà sinistra dello schermo se il tocco è attivo.",
        practiceUndoArrows = "Premi ⌫ LETTERA accanto a SCEGLI, oppure il tasto " +
            "⌫ sulla griglia.",
        practiceUndoDirect = "Tocca il tasto ⌫.",
        practiceDone = "Fatto. È tutto ciò che la persona userà.",
        practiceFinish = "FINE",
        practiceAgain = "ESERCITATI",
    ),
    bindPressFor = "Premi il pulsante per",
    bindButtonFor = "Pulsante per",
    bindWrite = "SCRIVERE",
    bindUndo = "ANNULLARE",
    bindAssigned = "Assegnati",
    bindPressNow = "Ora premi, sul telecomando o sul pulsante, il tasto che vuoi usare.",
    bindAnother = "Aggiungere un altro pulsante per la stessa azione?",
    bindAddAnother = "AGGIUNGINE UN ALTRO",
    bindDone = "FATTO",
    bindCancel = "ANNULLA",
    checkTitle = "Controllo dei pulsanti",
    checkHint = "Premi ogni pulsante. Questa schermata si chiude da sola.",
    checkWaiting = "In attesa…",
    checkCode = "codice",
    checkRoleWrite = "scrive",
    checkRoleUndo = "annulla",
    checkRoleUnassigned = "non assegnato",
)
