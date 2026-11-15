package cat.merce.comunicador.ui

// German: the words, letters, openers and phrases. Umlauts are reached through
// the suggestions, like the accents of the other languages.
val GERMAN = Language(
    code = "de",
    displayName = "Deutsch",
    modelAsset = "de-model.txt",
    ttsLocale = "de",
    letterRows = listOf(
        listOf("E", "N", "I", "S", "A"),
        listOf("R", "H", "T", "D", "U", "C"),
        listOf("L", "M", "O", "G", "W", "B"),
        listOf("F", "K", "Z", "V", "P", "J"),
        listOf("Y", "X", "Q"),
    ),
    openers = listOf(
        "ich", "möchte", "brauche", "Hilfe", "bitte",
        "Wasser", "Schmerzen", "Toilette", "Essen", "kalt",
        "heiß", "müde", "warte", "stopp", "hallo",
        "ja", "danke", "kann", "wo", "was",
    ),
    defaultPhrases = listOf(
        "Guten Morgen",
        "Mir geht es nicht gut",
        "Wasser",
        "Ich möchte in den Garten",
        "Ich möchte ins Esszimmer",
        "Mir ist kalt",
        "Mir ist zu warm",
        "Ich muss auf die Toilette",
        "Sagt bitte meiner Familie Bescheid",
        "Bitte hol Hilfe",
        "Einen Moment bitte",
        "Mir geht es gut",
        "Danke",
        "Ich liebe dich",
    ),
    spaceLabel = "Leer",
    clearLabel = "ALLES LÖSCHEN",
    yesLabel = "JA",
    noLabel = "NEIN",
    phrasesLabel = "PHRASEN",
    backLabel = "ZURÜCK",
    settingsSpeed = "Scan-Geschwindigkeit",
    settingsSecondsPerStep = "Sekunden pro Schritt",
    settingsFaster = "schneller",
    settingsSlower = "langsamer",
    settingsFirstLetterExtra = "Extra-Zeit beim ersten Buchstaben",
    settingsMinBetweenPresses = "Mindestzeit zwischen zwei Tastendrücken",
    settingsTouchTitle = "Bildschirm berühren zum Schreiben",
    settingsTouchDetail = "Rechts: schreibt. Links: macht rückgängig.",
    settingsTremorTitle = "Zittern ignorieren",
    settingsTremorDetail = "Mehrere schnelle Berührungen hintereinander zählen nur einmal.",
    settingsButtons = "Tasten",
    settingsWrite = "Schreiben",
    settingsUndo = "Rückgängig",
    settingsAssignWrite = "SCHREIBEN ZUWEISEN",
    settingsAssignUndo = "RÜCKGÄNGIG ZUWEISEN",
    settingsClose = "SCHLIESSEN",
    settingsCheckButtons = "TASTER PRÜFEN",
    settingsEitherCloses = "Mit jedem der beiden Taster lässt sich dieser Bildschirm ebenfalls schließen.",
    settingsLanguage = "Sprache",
    settingsLockedTitle = "Gesperrter Modus",
    settingsLockedDetail = "Die App bleibt auf dem Bildschirm und kann nicht verlassen werden. Hier ausschalten, um sie zu verlassen.",
    settingsBootTitle = "Beim Neustart öffnen",
    settingsBootDetail = "Die App öffnet sich von selbst, wenn das Gerät neu startet.",
    settingsModeTitle = "Wie ein Buchstabe gewählt wird",
    settingsModeDetail = "Beim Scannen bewegt sich die Markierung von selbst und man wartet. " +
        "Mit Pfeilen bewegt man sie selbst und muss nie warten.",
    settingsModeScan = "Automatisches Scannen",
    settingsModeArrows = "Pfeile",
    settingsModeDirect = "Buchstaben berühren",
    settingsArrowPlaceTitle = "Wo die Pfeile sind",
    settingsArrowPlaceDetail = "Stelle sie dorthin, wo der Arm beim Drücken nicht " +
        "über sie hinwegstreicht. Unten liegt die ganze Breite des Bildschirms " +
        "zwischen WÄHLEN und den Pfeilen, aber die Pfeile werden kleiner.",
    settingsArrowColumn = "In einer Spalte an einer Seite",
    settingsArrowBar = "Unten, in einem Streifen",
    settingsChooseTop = "WÄHLEN oben",
    settingsChooseLeft = "WÄHLEN links",
    settingsChooseDetail = "Stelle WÄHLEN dorthin, wo der Arm nicht darüber hinwegstreicht.",
    settingsArrowRight = "Rechts",
    settingsArrowLeft = "Links",
    settingsArrowBottom = "Unten",
    arrowChoose = "WÄHLEN",
    sectionWriting = "SO WIRD GESCHRIEBEN",
    sectionWritingDetail = "Wie die Buchstaben gewählt werden. Fange hier an: alles andere in diesem Bereich hängt davon ab.",
    sectionTiming = "TEMPO UND ZITTERN",
    sectionTimingDetail = "Für eine Hand, die zittert oder ungewollt zweimal drückt.",
    sectionWords = "WÖRTER UND LESEN",
    sectionWordsDetail = "Was in der Wortzeile erscheint und wie der geschriebene Satz aussieht.",
    sectionSwitches = "TASTER UND STEUERUNGEN",
    sectionSwitchesDetail = "Nur nötig, wenn ein Taster oder eine Steuerung angeschlossen ist.",
    sectionDevice = "DAS GERÄT",
    sectionDeviceDetail = "Wird einmal beim Einrichten des Geräts eingestellt und dann nicht mehr angefasst.",
    settingsForgiveTitle = "Fehler verzeihen",
    settingsForgiveDetail = "Wenn ein Buchstabe doppelt oder gar nicht ankommt, erscheinen trotzdem Wörter. Genaue Treffer stehen immer zuerst.",
    settingsAiTitle = "KI-Vorschläge (experimentell)",
    settingsAiDetail = "Ein kleines Sprachmodell, direkt auf dem Tablet und ohne Internet, ordnet die vorgeschlagenen Wörter nach dem, was geschrieben wurde. Die Wörter bleiben dieselben, nur die Reihenfolge ändert sich. Wenn das Tablet nicht schnell genug ist, schaltet es sich von selbst aus.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Aus."
            AiStatus.Loading -> "Wird geladen… Beim ersten Mal dauert es etwas."
            AiStatus.On -> "Läuft."
            AiStatus.NotEnoughMemory -> "Dieses Tablet hat nicht genug Speicher. Die normalen Vorschläge funktionieren wie bisher."
            AiStatus.Failed -> "Es konnte nicht geladen werden. Die normalen Vorschläge funktionieren wie bisher."
            AiStatus.TooSlow -> "Es hat sich von selbst ausgeschaltet: dieses Tablet war zu langsam. Die normalen Vorschläge funktionieren wie bisher."
        }
    },
    settingsBoldWritingTitle = "Satz in Fettschrift",
    settingsBoldWritingDetail = "Der Satz, den die Person schreibt, in dickerer Schrift, damit man ihn aus der Ferne lesen kann.",
    settingsArrowShapeTitle = "Wie die Pfeile aussehen",
    settingsArrowShapeDetail = "Getrennt gibt jedem Pfeil eine eigene Taste, sodass man seine Größe sieht. Verbunden zeichnet sie als eine Form. Worauf eine Berührung reagiert, ist in beiden Fällen gleich.",
    settingsArrowShapeJoined = "Verbunden",
    settingsArrowShapeSeparate = "Getrennt",
    settingsEraseKeysTitle = "Löschtasten neben WÄHLEN",
    settingsEraseKeysDetail = "Einen Buchstaben oder alles löschen, ohne mit den Pfeilen dorthin zu steuern. ALLES LÖSCHEN fragt vorher nach.",
    arrowDeleteLetter = "⌫ ZEICHEN",
    arrowClearConfirm = "SICHER?",
    batteryLow = "Akku schwach",
    settingsTutorial = "SO FUNKTIONIERT ES",
    tutorialNext = "WEITER",
    tutorialBack = "ZURÜCK",
    tutorialActiveMode = "DIESER MODUS IST AKTIV",
    tutorialPressed = "DRÜCKEN",
    tutorial = listOf(
        TutorialPage(
            title = "Wofür die App da ist",
            lines = listOf(
                "Sie ermöglicht es einem Menschen, der nicht sprechen und die " +
                    "Hände nicht bewegen kann, zu schreiben und Dinge laut zu sagen.",
                "Je nachdem, was die Person kann, berührt sie die Buchstaben, " +
                    "lenkt eine Markierung mit Pfeilen oder macht eine " +
                    "einzige Bewegung, während die App den Rest übernimmt.",
                "Du richtest die App für die Person ein. Diese Anleitung " +
                    "erklärt, wie alles funktioniert, damit du es gut " +
                    "erklären kannst.",
            ),
        ),
        TutorialPage(
            title = "Die Buchstaben sind keine Tasten",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "Das ist der Teil, den man am leichtesten falsch versteht. " +
                    "Es lohnt sich, ihn zweimal zu lesen.",
                "Die Buchstaben auf dem Bildschirm sind KEINE Tasten. Den " +
                    "gewünschten Buchstaben zu berühren bewirkt nichts. Sie " +
                    "sind zum Lesen da, nicht zum Drücken.",
                "Beim automatischen Scannen gibt es zwei Tasten, jede ist " +
                    "eine halbe Bildschirmseite groß: die ganze RECHTE Hälfte " +
                    "schreibt, die ganze LINKE Hälfte macht rückgängig.",
                "Sie sind absichtlich unsichtbar: das Raster ist zum " +
                    "Anschauen da, und Linien darüber würden nur Buchstaben " +
                    "verdecken.",
                "Das heißt, die Person kann innerhalb der Hälfte berühren, " +
                    "wo es passt. Es gibt nichts zu treffen, und die Taste " +
                    "lässt sich nicht verfehlen.",
                "Bei Pfeilen ist es anders: dort sind die Tasten sichtbar, " +
                    "nämlich die vier Pfeile und WÄHLEN. Die Buchstaben sind " +
                    "trotzdem keine Tasten.",
                "In den Einstellungen kann man das unter BILDSCHIRM BERÜHREN " +
                    "ZUM SCHREIBEN ausschalten, wenn jemand den Bildschirm " +
                    "ungewollt berührt.",
            ),
        ),
        TutorialPage(
            title = "Automatisches Scannen",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "Die rote Markierung wandert von selbst Zeile für Zeile nach unten.",
                "Wenn die Markierung bei der gewünschten Zeile ist, drückt " +
                    "die Person. Jetzt wandert die Markierung Buchstabe für " +
                    "Buchstabe durch diese Zeile.",
                "Wenn die Markierung beim gewünschten Buchstaben ist, " +
                    "drückt die Person noch einmal. Der Buchstabe wird geschrieben und alles beginnt " +
                    "wieder oben.",
                "Wenn der Buchstabe vorbeigeht, muss die Person nichts tun: nach " +
                    "zwei Durchgängen verlässt die Markierung die Zeile von " +
                    "selbst und läuft wieder nach unten.",
                "Die linke Bildschirmhälfte zu berühren macht das Letzte rückgängig.",
            ),
        ),
        TutorialPage(
            title = "Pfeile",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "Die Markierung bleibt stehen, bis die Person sie bewegt. Es gibt " +
                    "nichts abzuwarten, und man kann nicht zu spät sein.",
                "Die vier Pfeile bewegen sie ein Feld weiter. WÄHLEN " +
                    "schreibt den Buchstaben, der markiert ist.",
                "Es geht rundherum: vom ersten Buchstaben einer Zeile führt " +
                    "links zum letzten. Von der obersten Zeile führt nach " +
                    "oben zur untersten. Nichts ist je weit weg.",
                "Nach dem Schreiben bleibt die Markierung, wo sie ist. Ein " +
                    "doppelter Buchstabe sind zwei Drücke auf WÄHLEN, sonst nichts.",
                "Neben WÄHLEN gibt es ⌫ ZEICHEN, das den letzten Buchstaben " +
                    "entfernt, und ALLES LÖSCHEN, das den ganzen Satz leert. " +
                    "ALLES LÖSCHEN braucht zwei Drücke: der erste färbt es " +
                    "rot und lässt es SICHER? anzeigen, erst der zweite leert " +
                    "den Satz. Alles andere, was die Person tut, bricht es ab.",
                "Die Taste ⌫ im Raster entfernt ebenfalls einen Buchstaben.",
                "Auch die Lücken zwischen den Pfeilen zählen: eine " +
                    "Berührung zwischen zwei Pfeilen geht an den näheren.",
            ),
        ),
        TutorialPage(
            title = "Buchstaben berühren",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Nur hier sind die Buchstaben Tasten: die Person berührt den, " +
                    "den sie möchte, und er wird geschrieben.",
                "Es ist mit Abstand der schnellste Weg, aber nur, solange " +
                    "die Person einen Finger genau auf eine bestimmte Taste setzen kann. " +
                    "Wenn die Berührungen anfangen, auf der Nachbartaste zu " +
                    "landen, probiere stattdessen Pfeile.",
                "Die Taste ⌫ im Raster entfernt den letzten Buchstaben.",
                "ZITTERN IGNORIEREN in den Einstellungen sorgt dafür, dass " +
                    "mehrere schnelle Berührungen hintereinander nur einmal " +
                    "zählen.",
            ),
        ),
        TutorialPage(
            title = "Das Raster",
            lines = listOf(
                "Oberste Zeile: Wörter, die die App vorschlägt, dann JA, " +
                    "NEIN und PHRASEN.",
                "JA und NEIN sind nur da, solange der Satz leer ist. Sobald " +
                    "die Person zu schreiben beginnt, werden diese beiden Felder zu " +
                    "zwei weiteren Wortvorschlägen: fünf statt drei. Sie " +
                    "kommen von selbst wieder, wenn der Satz gelöscht wird.",
                "Die vorgeschlagenen Wörter ändern sich, während die " +
                    "Person schreibt. Ein Wort zu wählen schreibt das ganze Wort und " +
                    "das Leerzeichen danach, was oft der kürzeste Weg ist.",
                "Es verzeiht auch Fehler: wenn ein Buchstabe doppelt oder " +
                    "gar nicht ankommt, erscheinen trotzdem weiter Wörter. " +
                    "Ein Wort zu wählen berichtigt das ganze Wort, ohne dass " +
                    "etwas gelöscht werden muss.",
                "Die Buchstaben stehen nicht in alphabetischer Reihenfolge, " +
                    "sondern danach, wie oft sie auf Deutsch vorkommen, damit " +
                    "die häufigen nah sind.",
                "× löscht den ganzen Satz und liegt deshalb in der letzten " +
                    "Ecke des Rasters.",
            ),
        ),
        TutorialPage(
            title = "Phrasen",
            lines = listOf(
                "PHRASEN öffnet einen Bildschirm mit ganzen Sätzen.",
                "Wird einer gewählt, wird er laut gesprochen. Ins Textfeld " +
                    "wird nichts geschrieben.",
                "Um zum Schreiben zurückzukehren: ZURÜCK oder die linke " +
                    "Bildschirmhälfte berühren.",
            ),
        ),
        TutorialPage(
            title = "Die wichtigen Einstellungen",
            lines = listOf(
                "SCAN-GESCHWINDIGKEIT: wie lange jeder Schritt dauert. Wenn " +
                    "der Person Buchstaben entgehen, stelle es langsamer.",
                "EXTRA-ZEIT BEIM ERSTEN BUCHSTABEN: gibt der Person einen " +
                    "Moment mehr zum Reagieren, gleich nachdem eine Zeile " +
                    "betreten wurde.",
                "MINDESTZEIT ZWISCHEN ZWEI TASTENDRÜCKEN: wenn ein einziger " +
                    "Druck zwei Buchstaben schreibt, erhöhe sie.",
                "ZITTERN IGNORIEREN: mehrere schnelle Berührungen " +
                    "hintereinander zählen nur einmal.",
                "Ändere immer nur eine Sache und probiere sie aus. Wenn man " +
                    "alles auf einmal ändert, weiß man nicht, was geholfen hat.",
            ),
        ),
        TutorialPage(
            title = "Gesperrter Modus und Akku",
            lines = listOf(
                "Im gesperrten Modus bleibt die App auf dem Bildschirm und " +
                    "kann nicht aus Versehen verlassen werden.",
                "Um herauszukommen: das ⚙ oben rechts, dann in den " +
                    "Einstellungen ausschalten. Das geht immer.",
                "Der Akku wird oben rechts angezeigt. Unter 20 % wird er rot.",
                "Ein leerer Akku bedeutet einen Menschen ohne Stimme. Es " +
                    "lohnt sich, das Gerät am Ladekabel zu lassen.",
            ),
        ),
        TutorialPage(
            title = "Das war alles",
            lines = listOf(
                "Schließe das hier und probiere es selbst ein paar Minuten " +
                    "aus, bevor du es der Person erklärst. Nur so erklärt man es gut.",
                "Wenn etwas bei der Person nicht klappt, liegt es fast immer an " +
                    "einer Einstellung und nicht an der App. Fange mit der " +
                    "Geschwindigkeit an.",
                "Du kannst diese Anleitung jederzeit wieder öffnen: ⚙ → SO " +
                    "FUNKTIONIERT ES.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "Einrichten",
        welcomeLines = listOf(
            "Mit dieser App kann jemand, der nicht sprechen und nicht mit " +
                "der Hand schreiben kann, Sätze bilden und laut sagen.",
            "Es gibt mehrere Wege, sie zu benutzen. Zwei Fragen zur Person, " +
                "und die App wird so eingerichtet, wie es zu ihr passt.",
            "Jede Antwort lässt sich später in den Einstellungen ändern " +
                "(⚙, oben rechts).",
        ),
        start = "LOS",
        methodTitle = "Wie wählt die Person einen Buchstaben?",
        methodHint = "Unsicher? Beginne mit dem, was der Person am wenigsten " +
            "abverlangt. Wenn das Warten sie ermüdet oder der richtige " +
            "Moment schwer zu treffen ist, probiere Pfeile.",
        methodDirectDetail = "Die Person kann zuverlässig eine bestimmte Taste " +
            "berühren. Der schnellste Weg, solange es geht.",
        methodArrowsDetail = "Die Person kann ein paar große Tasten berühren, " +
            "aber keine kleinen. Nichts bewegt sich, bis sie es bewegt.",
        methodScanDetail = "Die Person kann eine einzige Bewegung machen, " +
            "mit einem Taster oder durch Berühren. Eine Markierung bewegt " +
            "sich von selbst, und die Person drückt, wenn die Markierung " +
            "bei dem ist, was sie möchte.",
        pressTitle = "Was drückt die Person?",
        pressHint = "Ein Taster ist eine Taste, die per Bluetooth mit dem " +
            "Tablet verbunden wird. Koppele ihn zuerst in den " +
            "Bluetooth-Einstellungen des Tablets.",
        pressScreen = "Der Bildschirm",
        pressScreenDetail = "Die rechte Hälfte des Bildschirms schreibt, " +
            "die linke macht rückgängig. Kein Taster nötig.",
        pressSwitches = "Taster",
        pressSwitchesDetail = "Ein Taster schreibt, ein anderer macht " +
            "rückgängig. Eine Berührung des Bildschirms bewirkt nichts, ein " +
            "darauf ruhender Arm schadet also nicht.",
        pressBoth = "Beides",
        pressBothDetail = "Taster, und zusätzlich die Bildschirmhälften.",
        practiceWord = "HALLO",
        practiceWrite = "Übung: schreiben",
        practiceWriteScan = "Warte, bis die Markierung die Zeile erreicht, " +
            "drücke, und drücke dann noch einmal beim Buchstaben.",
        practiceWriteArrows = "Bewege die Markierung mit den Pfeilen und " +
            "drücke bei jedem Buchstaben WÄHLEN.",
        practiceWriteDirect = "Berühre nacheinander jeden Buchstaben.",
        practiceUndo = "Nimm jetzt einen Buchstaben zurück",
        practiceUndoScan = "Drücke den Taster für Rückgängig, oder berühre " +
            "die linke Bildschirmhälfte, wenn Berühren eingeschaltet ist.",
        practiceUndoArrows = "Drücke ⌫ ZEICHEN neben WÄHLEN oder die Taste " +
            "⌫ im Raster.",
        practiceUndoDirect = "Berühre die Taste ⌫.",
        practiceDone = "Geschafft. Das ist alles, was die Person benutzen wird.",
        practiceFinish = "FERTIG",
        practiceAgain = "NOCHMAL ÜBEN",
    ),
    bindPressFor = "Drücke die Taste für",
    bindButtonFor = "Taste für",
    bindWrite = "SCHREIBEN",
    bindUndo = "RÜCKGÄNGIG",
    bindAssigned = "Zugewiesen",
    bindPressNow = "Drücke jetzt an der Steuerung oder am Taster die Taste, die du verwenden möchtest.",
    bindAnother = "Eine weitere Taste für dieselbe Aktion hinzufügen?",
    bindAddAnother = "WEITERE HINZUFÜGEN",
    bindDone = "FERTIG",
    bindCancel = "ABBRECHEN",
    checkTitle = "Taster werden geprüft",
    checkHint = "Drücke jeden Taster. Dieser Bildschirm schließt sich von selbst.",
    checkWaiting = "Warte…",
    checkCode = "Code",
    checkRoleWrite = "schreibt",
    checkRoleUndo = "macht rückgängig",
    checkRoleUnassigned = "nicht zugewiesen",
)
