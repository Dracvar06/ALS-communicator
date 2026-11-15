package cat.merce.comunicador.ui

// Romanian: every string, the letter grid and the starting phrases.

val ROMANIAN = Language(
    code = "ro",
    displayName = "Română",
    modelAsset = "ro-model.txt",
    ttsLocale = "ro",
    letterRows = listOf(
        listOf("E", "A", "I", "T", "U"),
        listOf("N", "R", "C", "S", "O", "M"),
        listOf("L", "D", "Ă", "P", "V", "F"),
        listOf("B", "Ș", "G", "Î", "Ț", "Z", "H"),
        listOf("Â", "J", "Y", "K", "X", "W", "Q"),
    ),
    openers = listOf(
        "vreau", "am", "ajutor", "te rog", "mulțumesc",
        "apă", "durere", "toaletă", "mâncare", "frig",
        "cald", "oboseală", "așteaptă", "stop", "bună",
        "sunt", "pot", "unde", "ce", "da",
    ),
    defaultPhrases = listOf(
        "Bună dimineața",
        "Nu mă simt bine",
        "Apă",
        "Vreau să merg în grădină",
        "Vreau să merg în sufragerie",
        "Mi-e frig",
        "Mi-e prea cald",
        "Trebuie să merg la toaletă",
        "Te rog să le spui celor din familia mea",
        "Te rog, cheamă ajutor",
        "Așteaptă o clipă",
        "Sunt bine",
        "Mulțumesc",
        "Te iubesc",
    ),
    spaceLabel = "spațiu",
    clearLabel = "ȘTERGE TOT",
    yesLabel = "DA",
    noLabel = "NU",
    phrasesLabel = "FRAZE",
    backLabel = "ÎNAPOI",
    settingsSpeed = "Viteza scanării",
    settingsSecondsPerStep = "secunde pe pas",
    settingsFaster = "mai repede",
    settingsSlower = "mai încet",
    settingsFirstLetterExtra = "Timp în plus la prima literă",
    settingsMinBetweenPresses = "Timp minim între apăsări",
    settingsTouchTitle = "Atinge ecranul ca să scrii",
    settingsTouchDetail = "Dreapta: scrie. Stânga: anulează.",
    settingsTremorTitle = "Ignoră tremurul",
    settingsTremorDetail = "O serie de atingeri la rând se numără o singură dată.",
    settingsButtons = "Butoane",
    settingsWrite = "Scrie",
    settingsUndo = "Anulează",
    settingsAssignWrite = "ATRIBUIE BUTONUL DE SCRIS",
    settingsAssignUndo = "ATRIBUIE BUTONUL DE ANULARE",
    settingsClose = "ÎNCHIDE",
    settingsCheckButtons = "VERIFICĂ BUTOANELE",
    settingsEitherCloses = "Oricare dintre cele două butoane închide și acest ecran.",
    settingsLanguage = "Limba",
    settingsLockedTitle = "Mod blocat",
    settingsLockedDetail = "Aplicația rămâne pe ecran și nu se poate ieși din ea. Dezactivează aici pentru a ieși.",
    settingsBootTitle = "Se deschide la pornire",
    settingsBootDetail = "Aplicația se deschide singură când dispozitivul repornește.",
    settingsModeTitle = "Cum se alege o literă",
    settingsModeDetail = "Cu scanare, marcajul se mișcă singur și aștepți. " +
        "Cu săgeți îl muți tu și nu trebuie să aștepți niciodată.",
    settingsModeScan = "Scanare automată",
    settingsModeArrows = "Săgeți",
    settingsModeDirect = "Atinge literele",
    settingsArrowPlaceTitle = "Unde stau săgețile",
    settingsArrowPlaceDetail = "Pune-le unde brațul nu trece peste ele în " +
        "drum spre apăsare. Jos pune între ALEGE și săgeți toată lățimea " +
        "ecranului, dar săgețile ies mai mici.",
    settingsArrowColumn = "Pe o coloană, pe o parte",
    settingsArrowBar = "Jos, pe o bandă",
    settingsChooseTop = "ALEGE sus",
    settingsChooseLeft = "ALEGE în stânga",
    settingsChooseDetail = "Pune ALEGE unde brațul nu trece peste el.",
    settingsArrowRight = "În dreapta",
    settingsArrowLeft = "În stânga",
    settingsArrowBottom = "Jos",
    arrowChoose = "ALEGE",
    sectionWriting = "CUM SE SCRIE",
    sectionWritingDetail = "Felul în care se aleg literele. Începe de aici: tot restul din această secțiune depinde de el.",
    sectionTiming = "RITM ȘI TREMUR",
    sectionTimingDetail = "Pentru o mână care tremură sau care apasă de două ori fără să vrea.",
    sectionWords = "CUVINTE ȘI CITIRE",
    sectionWordsDetail = "Ce apare pe rândul cu cuvinte și cum arată fraza scrisă.",
    sectionSwitches = "BUTOANE ȘI TELECOMENZI",
    sectionSwitchesDetail = "Este nevoie doar dacă este conectat un buton sau o telecomandă.",
    sectionDevice = "DISPOZITIVUL",
    sectionDeviceDetail = "Se setează o dată, la pregătirea dispozitivului, și apoi nu se mai schimbă.",
    settingsForgiveTitle = "Iartă greșelile",
    settingsForgiveDetail = "Dacă o literă se repetă fără să vrei sau lipsește, cuvintele tot apar. Cele care se potrivesc exact apar mereu primele.",
    settingsAiTitle = "Sugestii cu IA (experimental)",
    settingsAiDetail = "Un model de limbaj mic, aflat în tabletă și fără internet, reordonează cuvintele sugerate după ce s-a scris. Cuvintele sunt aceleași; se schimbă doar ordinea. Dacă tableta nu este destul de rapidă, se oprește singur.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Oprit."
            AiStatus.Loading -> "Se încarcă… Prima dată durează puțin."
            AiStatus.On -> "Funcționează."
            AiStatus.NotEnoughMemory -> "Această tabletă nu are destulă memorie. Sugestiile obișnuite funcționează la fel."
            AiStatus.Failed -> "Nu s-a putut încărca. Sugestiile obișnuite funcționează la fel."
            AiStatus.TooSlow -> "S-a oprit singur: această tabletă era prea lentă. Sugestiile obișnuite funcționează la fel."
        }
    },
    settingsBoldWritingTitle = "Fraza îngroșată",
    settingsBoldWritingDetail = "Fraza pe care o scrie persoana, mai groasă, ca să se citească de departe.",
    settingsArrowShapeTitle = "Cum arată săgețile",
    settingsArrowShapeDetail = "Separate, fiecare săgeată are butonul ei și se vede cât de mare este. Unite, sunt desenate ca o singură formă. Ce răspunde la atingere este la fel în ambele cazuri.",
    settingsArrowShapeJoined = "Unite",
    settingsArrowShapeSeparate = "Separate",
    settingsEraseKeysTitle = "Butoane de ștergere lângă ALEGE",
    settingsEraseKeysDetail = "Șterg o literă sau tot, fără să mai fie nevoie să ajungi la ele cu săgețile. ȘTERGE TOT întreabă înainte.",
    arrowDeleteLetter = "⌫ LITERĂ",
    arrowClearConfirm = "SIGUR?",
    batteryLow = "Baterie slabă",
    settingsTutorial = "CUM FUNCȚIONEAZĂ",
    tutorialNext = "URMĂTOAREA",
    tutorialBack = "ÎNAPOI",
    tutorialActiveMode = "ACESTA ESTE MODUL FOLOSIT",
    tutorialPressed = "APASĂ",
    tutorial = listOf(
        TutorialPage(
            title = "La ce folosește",
            lines = listOf(
                "Ajută o persoană care nu poate vorbi și nu își poate mișca " +
                    "mâinile să scrie și să spună lucruri cu voce tare.",
                "În funcție de ce poate face persoana, aceasta atinge " +
                    "literele, mută un chenar cu săgeți sau face o singură " +
                    "mișcare, în timp ce aplicația face restul.",
                "Tu pregătești aplicația. Acest ghid explică cum " +
                    "funcționează, ca să o poți explica bine.",
            ),
        ),
        TutorialPage(
            title = "Literele nu sunt butoane",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "Aici greșesc cei mai mulți și merită citit de " +
                    "două ori.",
                "Literele de pe ecran NU sunt butoane. Dacă persoana atinge " +
                    "litera pe care o vrea, nu se întâmplă nimic. Sunt acolo ca să " +
                    "fie citite, nu apăsate.",
                "La scanarea automată sunt două butoane și fiecare ocupă " +
                    "jumătate de ecran: toată jumătatea DREAPTĂ scrie, " +
                    "toată jumătatea STÂNGĂ anulează.",
                "Nu se văd, și e intenționat: grila este pentru privit, " +
                    "iar niște linii deasupra ar acoperi doar litere.",
                "Asta înseamnă că persoana poate atinge oriunde i se potrivește în " +
                    "jumătatea respectivă. Nu are ce ținti și nu poate rata butonul.",
                "La săgeți e altfel: acolo butoanele se văd, sunt cele " +
                    "patru săgeți și ALEGE. Literele tot nu sunt butoane.",
                "Poți dezactiva asta din setări, la ATINGE ECRANUL CA SĂ " +
                    "SCRII, dacă cineva atinge ecranul din greșeală.",
            ),
        ),
        TutorialPage(
            title = "Scanare automată",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "Chenarul roșu coboară singur, rând cu rând.",
                "Când chenarul ajunge la rândul pe care îl vrea persoana, " +
                    "aceasta apasă. Acum chenarul merge literă cu literă pe " +
                    "acel rând.",
                "Când chenarul ajunge la litera pe care o vrea, persoana " +
                    "apasă din nou. " +
                    "Litera se scrie și totul începe iar de sus.",
                "Dacă litera trece, persoana nu trebuie să facă nimic: după două " +
                    "treceri rândul se lasă singur și chenarul coboară din nou.",
                "Atingerea jumătății stângi a ecranului anulează ultimul lucru.",
            ),
        ),
        TutorialPage(
            title = "Săgeți",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "Chenarul stă pe loc până îl mută persoana. Nu e nimic de " +
                    "așteptat și nu se poate întârzia.",
                "Cele patru săgeți îl mută câte o căsuță. ALEGE scrie " +
                    "litera marcată.",
                "Se continuă de la capăt: de la prima literă a unui rând, " +
                    "stânga duce la ultima. De pe rândul de sus, în sus duce " +
                    "la cel de jos. Nimic nu e vreodată departe.",
                "După scriere, chenarul rămâne unde este. O literă dublă " +
                    "înseamnă două apăsări pe ALEGE și atât.",
                "Lângă ALEGE sunt ⌫ LITERĂ, care șterge ultima literă, și " +
                    "ȘTERGE TOT, care golește toată fraza. " +
                    "ȘTERGE TOT cere două apăsări: prima îl face roșu și " +
                    "scrie SIGUR?, iar abia a doua golește fraza. " +
                    "Orice altceva face persoana îl anulează.",
                "Tasta ⌫ de pe grilă șterge și ea o literă.",
                "Spațiile dintre săgeți contează și ele: o atingere care " +
                    "cade între două merge la cea mai apropiată.",
            ),
        ),
        TutorialPage(
            title = "Atinge literele",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Aici, și numai aici, literele sunt butoane: persoana o atinge " +
                    "pe cea pe care o vrea și se scrie.",
                "Este de departe cea mai rapidă cale, dar numai cât timp persoana " +
                    "poate pune degetul pe o anumită tastă. Dacă atingerile " +
                    "încep să nimerească tasta de alături, încearcă săgețile.",
                "Tasta ⌫ de pe grilă șterge ultima literă.",
                "IGNORĂ TREMURUL din setări face ca o serie de atingeri " +
                    "apropiate să se numere o singură dată.",
            ),
        ),
        TutorialPage(
            title = "Grila",
            lines = listOf(
                "Rândul de sus: cuvinte sugerate de aplicație, apoi " +
                    "DA, NU și FRAZE.",
                "DA și NU sunt acolo doar cât fraza este goală. Când " +
                    "persoana începe să scrie, acele două căsuțe devin două cuvinte " +
                    "sugerate în plus: cinci în loc de trei. Se întorc " +
                    "singure când fraza este ștearsă.",
                "Cuvintele sugerate se schimbă pe măsură ce persoana scrie. " +
                    "Alegerea unuia scrie tot cuvântul și spațiul de după el, " +
                    "ceea ce e adesea cel mai scurt drum.",
                "Iartă și greșelile: dacă o literă se repetă fără să vrea " +
                    "sau lipsește, cuvintele tot apar. Alegerea unuia " +
                    "repară tot cuvântul, fără nimic de șters.",
                "Literele nu sunt în ordine alfabetică, ci în ordinea " +
                    "folosirii în română, ca cele mai frecvente să fie aproape.",
                "× șterge toată fraza, de aceea este în ultimul colț al grilei.",
            ),
        ),
        TutorialPage(
            title = "Fraze",
            lines = listOf(
                "FRAZE deschide un ecran cu propoziții întregi.",
                "Alegerea uneia o spune cu voce tare. Nu se scrie nimic în " +
                    "caseta de text.",
                "Ca să te întorci la scris: ÎNAPOI sau atinge jumătatea " +
                    "stângă a ecranului.",
            ),
        ),
        TutorialPage(
            title = "Setările care contează",
            lines = listOf(
                "VITEZA SCANĂRII: cât durează fiecare pas. Dacă îi scapă " +
                    "literele, fă-o mai lentă.",
                "TIMP ÎN PLUS LA PRIMA LITERĂ: îi dă o clipă în plus ca să " +
                    "reacționeze imediat după ce a intrat pe un rând.",
                "TIMP MINIM ÎNTRE APĂSĂRI: dacă o singură apăsare scrie " +
                    "două litere, mărește-l.",
                "IGNORĂ TREMURUL: o serie de atingeri apropiate se numără o " +
                    "singură dată.",
                "Schimbă un singur lucru o dată și încearcă. Dacă schimbi " +
                    "totul deodată, nu afli ce a ajutat.",
            ),
        ),
        TutorialPage(
            title = "Modul blocat și bateria",
            lines = listOf(
                "În modul blocat, aplicația rămâne pe ecran și nu se poate " +
                    "ieși din ea din greșeală.",
                "Ca să ieși: rotița ⚙ din colțul din dreapta sus, apoi " +
                    "dezactivează-l din setări. Se poate oricând.",
                "Bateria se vede sus în dreapta. Se face roșie sub 20%.",
                "O baterie descărcată înseamnă o persoană fără voce. Merită " +
                    "să o lași la încărcat.",
            ),
        ),
        TutorialPage(
            title = "Asta e tot",
            lines = listOf(
                "Închide asta și încearcă tu câteva minute înainte să o " +
                    "explici persoanei. Este singurul mod de a o explica bine.",
                "Dacă ceva nu merge pentru persoană, aproape întotdeauna este o " +
                    "setare, nu aplicația. Începe cu viteza.",
                "Poți deschide din nou acest ghid oricând: ⚙ → CUM " +
                    "FUNCȚIONEAZĂ.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "Configurarea",
        welcomeLines = listOf(
            "Cu această aplicație, o persoană care nu poate vorbi și nu " +
                "poate scrie de mână poate alcătui propoziții și le poate " +
                "spune cu voce tare.",
            "Există mai multe moduri de a o folosi. Două întrebări despre " +
                "persoană și aplicația se configurează cum i se potrivește.",
            "Orice răspuns se poate schimba mai târziu din setări (⚙, în " +
                "dreapta sus).",
        ),
        start = "ÎNCEPE",
        methodTitle = "Cum va alege persoana o literă?",
        methodHint = "Dacă ai dubii, începe cu varianta care îi cere cel mai " +
            "puțin. Dacă așteptarea obosește persoana sau îi e greu să " +
            "nimerească momentul, încearcă săgețile.",
        methodDirectDetail = "Poate atinge sigur o anumită tastă. Cea mai " +
            "rapidă cale, cât timp se poate.",
        methodArrowsDetail = "Poate atinge câteva butoane mari, dar nu taste " +
            "mici. Nimic nu se mișcă până nu mută persoana.",
        methodScanDetail = "Poate face o singură mișcare, cu un buton sau cu " +
            "o atingere. Un marcaj se mișcă singur, iar persoana apasă când ajunge " +
            "la ce vrea.",
        pressTitle = "Ce va apăsa?",
        pressHint = "Butonul se conectează la tabletă prin Bluetooth. " +
            "Asociază-l mai întâi din setările Bluetooth ale tabletei.",
        pressScreen = "Ecranul",
        pressScreenDetail = "Jumătatea din dreapta a ecranului scrie, cea " +
            "din stânga anulează. Nu e nevoie de buton.",
        pressSwitches = "Butoane",
        pressSwitchesDetail = "Un buton scrie, altul anulează. Atingerea " +
            "ecranului nu face nimic, așa că un braț sprijinit pe el nu " +
            "deranjează.",
        pressBoth = "Amândouă",
        pressBothDetail = "Butoanele și, în plus, jumătățile ecranului.",
        practiceWord = "BUNĂ",
        practiceWrite = "Exercițiu: scrie",
        practiceWriteScan = "Așteaptă până când chenarul ajunge la rând, " +
            "apasă, apoi apasă din nou pe literă.",
        practiceWriteArrows = "Mută chenarul cu săgețile, apoi apasă ALEGE " +
            "pe fiecare literă.",
        practiceWriteDirect = "Atinge pe rând fiecare literă.",
        practiceUndo = "Acum anulează o literă",
        practiceUndoScan = "Apasă butonul de anulare sau atinge jumătatea " +
            "stângă a ecranului, dacă atingerea este activată.",
        practiceUndoArrows = "Apasă ⌫ LITERĂ lângă ALEGE sau tasta ⌫ de pe " +
            "grilă.",
        practiceUndoDirect = "Atinge tasta ⌫.",
        practiceDone = "Gata. Asta e tot ce va folosi persoana.",
        practiceFinish = "GATA",
        practiceAgain = "EXERSEAZĂ",
    ),
    bindPressFor = "Apasă butonul pentru",
    bindButtonFor = "Butonul pentru",
    bindWrite = "SCRIS",
    bindUndo = "ANULAT",
    bindAssigned = "Atribuite",
    bindPressNow = "Apasă acum, pe telecomandă sau pe buton, butonul pe care vrei să-l folosești.",
    bindAnother = "Adaugi încă un buton pentru aceeași acțiune?",
    bindAddAnother = "ADAUGĂ ÎNCĂ UNUL",
    bindDone = "GATA",
    bindCancel = "RENUNȚĂ",
    checkTitle = "Verificarea butoanelor",
    checkHint = "Apasă fiecare buton. Acest ecran se închide singur.",
    checkWaiting = "Se așteaptă…",
    checkCode = "cod",
    checkRoleWrite = "scrie",
    checkRoleUndo = "anulează",
    checkRoleUnassigned = "neatribuit",
)
