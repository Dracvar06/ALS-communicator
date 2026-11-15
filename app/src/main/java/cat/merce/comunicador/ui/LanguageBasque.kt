package cat.merce.comunicador.ui

// Basque: the words, letters, openers and phrases. The Ñ has its own key, as
// in Spanish; Basque has no accented letters to reach through suggestions.
val BASQUE = Language(
    code = "eu",
    experimentalLabel = "esperimentala",
    experimental = true,
    displayName = "Euskara",
    modelAsset = "eu-model.txt",
    ttsLocale = "eu",
    letterRows = listOf(
        listOf("A", "E", "I", "N", "R"),
        listOf("T", "U", "Z", "O", "K", "D"),
        listOf("G", "B", "L", "S", "H", "M"),
        listOf("P", "J", "X", "F", "C", "Y"),
        listOf("W", "V", "Ñ", "Q"),
    ),
    openers = listOf(
        "ni", "nahi", "behar", "laguntza", "mesedez",
        "ura", "mina", "komuna", "jatekoa", "hotz",
        "bero", "nekatuta", "itxaron", "gelditu", "kaixo",
        "bai", "eskerrik asko", "zer", "non", "nola",
    ),
    defaultPhrases = listOf(
        "Egun on",
        "Gaizki sentitzen naiz",
        "Ura",
        "Lorategira joan nahi dut",
        "Jangelara joan nahi dut",
        "Hotz dut",
        "Bero dut",
        "Komunera joan behar dut",
        "Abisatu nire familiari",
        "Eskatu laguntza, mesedez",
        "Itxaron une bat",
        "Ondo nago",
        "Eskerrik asko",
        "Maite zaitut",
    ),
    spaceLabel = "tartea",
    clearLabel = "DENA EZABATU",
    yesLabel = "BAI",
    noLabel = "EZ",
    phrasesLabel = "ESALDIAK",
    backLabel = "ATZERA",
    settingsSpeed = "Eskaneatzearen abiadura",
    settingsSecondsPerStep = "segundo urratsko",
    settingsFaster = "azkarrago",
    settingsSlower = "mantsoago",
    settingsFirstLetterExtra = "Denbora gehigarria lehen letran",
    settingsMinBetweenPresses = "Sakatzeen arteko gutxieneko denbora",
    settingsTouchTitle = "Pantaila ukitu idazteko",
    settingsTouchDetail = "Eskuinean: idazten du. Ezkerrean: desegiten du.",
    settingsTremorTitle = "Dardarak ez kontuan hartu",
    settingsTremorDetail = "Segidako ukitu sorta batek behin bakarrik balio du.",
    settingsButtons = "Botoiak",
    settingsWrite = "Idatzi",
    settingsUndo = "Desegin",
    settingsAssignWrite = "ESLEITU IDATZI",
    settingsAssignUndo = "ESLEITU DESEGIN",
    settingsClose = "ITXI",
    settingsCheckButtons = "SAKAGAILUAK EGIAZTATU",
    settingsEitherCloses = "Bi sakagailuetako edozeinek pantaila hau ere ixten du.",
    settingsLanguage = "Hizkuntza",
    settingsLockedTitle = "Modu blokeatua",
    settingsLockedDetail = "Aplikazioa pantailan geratzen da eta ezin da handik atera. Hemen desaktibatu irteteko.",
    settingsBootTitle = "Piztean ireki",
    settingsBootDetail = "Aplikazioa bere kabuz irekitzen da gailua berrabiarazten denean.",
    settingsModeTitle = "Nola aukeratzen den letra bat",
    settingsModeDetail = "Eskaneatzean markatzailea bere kabuz mugitzen da eta itxaron egiten da. " +
        "Geziekin zuk mugitzen duzu, eta ez duzu inoiz itxaron behar.",
    settingsModeScan = "Eskaneatze automatikoa",
    settingsModeArrows = "Geziak",
    settingsModeDirect = "Letrak ukitu",
    settingsArrowPlaceTitle = "Non dauden geziak",
    settingsArrowPlaceDetail = "Jarri besoa sakatzera doanean haien gainetik pasatzen ez den lekuan. " +
        "Behean jartzen badira, AUKERATU eta geziak pantailaren zabalera osoak " +
        "banatzen ditu, baina geziak txikiagoak geratzen dira.",
    settingsArrowColumn = "Zutabean, alde batean",
    settingsArrowBar = "Behean, zerrenda batean",
    settingsChooseTop = "AUKERATU goian",
    settingsChooseLeft = "AUKERATU ezkerrean",
    settingsChooseDetail = "Jarri AUKERATU besoa gainetik pasatzen ez zaion lekuan.",
    settingsArrowRight = "Eskuinean",
    settingsArrowLeft = "Ezkerrean",
    settingsArrowBottom = "Behean",
    arrowChoose = "AUKERATU",
    sectionWriting = "NOLA IDATZ DAITEKEEN",
    sectionWritingDetail = "Letrak aukeratzeko modua. Hemendik hasi: atal honetako gainerako guztia honen mende dago.",
    sectionTiming = "RITMOA ETA DARDARAK",
    sectionTimingDetail = "Dardar egiten duen edo nahi gabe birritan sakatzen duen esku batentzat.",
    sectionWords = "HITZAK ETA IRAKURKETA",
    sectionWordsDetail = "Zer agertzen den hitzen lerroan, eta nola ikusten den idatzitako esaldia.",
    sectionSwitches = "SAKAGAILUAK ETA KONTROLAK",
    sectionSwitchesDetail = "Sakagailu bat edo kontrol bat konektatuta badago bakarrik behar da.",
    sectionDevice = "GAILUA",
    sectionDeviceDetail = "Behin konfiguratzen da, gailua prestatzean, eta ez zaio berriz ikutzen.",
    settingsForgiveTitle = "Akatsak barkatu",
    settingsForgiveDetail = "Letra bat nahi gabe errepikatzen bada, edo bat ihes egiten badu, hitzak ateratzen jarraitzen dute. Zehazki datozenak beti lehenengo ateratzen dira.",
    settingsAiTitle = "AA bidezko iradokizunak (esperimentala)",
    settingsAiDetail = "Hizkuntza-eredu txiki batek, tabletaren barruan eta internetik gabe, iradokitako hitzak berrordenatzen ditu idatzitakoaren arabera. Hitzak berberak dira; ordena bakarrik aldatzen da. Tableta nahikoa azkarra ez bada, bere kabuz itzaltzen da.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Itzalita."
            AiStatus.Loading -> "Kargatzen… Lehen aldian pixka bat luzatzen da."
            AiStatus.On -> "Martxan."
            AiStatus.NotEnoughMemory -> "Tableta honek ez du memoria nahikorik. Ohiko iradokizunek lehen bezala funtzionatzen dute."
            AiStatus.Failed -> "Ezin izan da kargatu. Ohiko iradokizunek lehen bezala funtzionatzen dute."
            AiStatus.TooSlow -> "Bere kabuz itzali da: tableta hau motelegia zen. Ohiko iradokizunek lehen bezala funtzionatzen dute."
        }
    },
    settingsBoldWritingTitle = "Esaldia letra lodiz",
    settingsBoldWritingDetail = "Idazten ari den esaldia, lodiago, urrutitik irakurtzeko.",
    settingsVowelsTitle = "Bokalak koloretan",
    settingsVowelsDetail = "Bokalak beste kolore batean, begi-kolpe batez aurkitzeko.",
    settingsShowHalves = "ERAKUTSI BI BOTOIAK BERRIRO",
    settingsArrowShapeTitle = "Nolakoak diren geziak",
    settingsArrowShapeDetail = "Bananduta, gezi bakoitzak bere botoia du eta noraino iristen den ikusten da. Elkartuta, pieza bakar gisa ikusten dira. Ukituari erantzuten diona berdina da bi kasuetan.",
    settingsArrowShapeJoined = "Elkartuta",
    settingsArrowShapeSeparate = "Bananduta",
    settingsEraseKeysTitle = "Ezabatzeko botoiak AUKERATUren ondoan",
    settingsEraseKeysDetail = "Letra bat, edo dena, ezabatzen du geziekin horra joan beharrik gabe. DENA EZABATU-k aurretik galdetzen du.",
    arrowDeleteLetter = "⌫ LETRA",
    arrowClearConfirm = "ZIUR?",
    batteryLow = "Bateria baxua",
    settingsTutorial = "NOLA FUNTZIONATZEN DEN",
    tutorialNext = "HURRENGOA",
    tutorialBack = "ATZERA",
    tutorialActiveMode = "MODU HAU ERABILTZEN ARI DA",
    tutorialPressed = "SAKATU",
    tutorial = listOf(
        TutorialPage(
            title = "Zertarako den",
            lines = listOf(
                "Hitz egin eta eskuak mugitu ezin dituen pertsona batek " +
                    "idatz dezan eta gauzak ozen esan ditzan balio du.",
                "Egin dezakeenaren arabera, letrak ukitzen ditu, koadro bat " +
                    "geziekin gidatzen du, edo mugimendu bakarra egiten du, " +
                    "aplikazioak gainerakoa egiten duen bitartean.",
                "Zu zara prestatzen duena. Gida honek nola funtzionatzen " +
                    "duen azaltzen du, hari ondo azaldu ahal diozun.",
            ),
        ),
        TutorialPage(
            title = "Letrak ez dira botoiak",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "Hau da ulertzeko gehien kostatzen den zatia, eta merezi du " +
                    "bi aldiz irakurtzea.",
                "Pantailako letrak EZ dira botoiak. Nahi duen letra ukitzeak " +
                    "ez du ezer egiten. Irakurtzeko daude, ez sakatzeko.",
                "Eskaneatze automatikoarekin bi botoi daude, eta bakoitzak " +
                    "pantailaren erdia hartzen du: ESKUINEKO erdi osoak " +
                    "idazten du, EZKERREKO erdi osoak desegiten du.",
                "Ez dira ikusten, nahita: sareta begiratzeko dago, eta " +
                    "gainean marrak jartzeak letrak estaliko lituzke bakarrik.",
                "Horrek esan nahi du hark nahi duen lekuan ukitu dezakeila " +
                    "erdi bakoitzean. Ez du ezer asmatu behar, eta ezin du " +
                    "botoia huts egin.",
                "Geziekin ezberdina da: han botoiak ikusten dira, lau " +
                    "geziak eta AUKERATU dira. Letrak, hala ere, ez dira botoiak.",
                "Ezarpenetan desaktibatu daiteke, PANTAILA UKITU IDAZTEKO " +
                    "atalean, norbaitek nahi gabe pantaila ukitzen badu.",
            ),
        ),
        TutorialPage(
            title = "Eskaneatze automatikoa",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "Koadro gorria lerroz lerro jaisten da, bere kabuz.",
                "Hark nahi duen lerroan dagoenean, sakatzen du. Orain " +
                    "koadroa lerro horretako letra batetik bestera mugitzen da.",
                "Nahi duen letran dagoenean, berriz sakatzen du. Letra " +
                    "idazten da eta dena goitik hasten da berriro.",
                "Letra pasatzen bazaio, ez dago ezer egin beharrik: bi " +
                    "itzuliren ondoren lerroa bere kabuz uzten da eta berriz " +
                    "jaisten da.",
                "Pantailaren ezkerreko erdia ukitzeak azken gauza desegiten du.",
            ),
        ),
        TutorialPage(
            title = "Geziak",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "Koadroa geldi geratzen da hark mugitu arte. Ez dago ezer " +
                    "itxaroterik eta ezin da berandu iritsi.",
                "Lau geziek koadroa laukitxo bat mugitzen dute aldi " +
                    "bakoitzean. AUKERATUk markatuta dagoen letra idazten du.",
                "Buelta ematen du: lerro bateko lehen letratik, ezkerrera " +
                    "joateak azkenera eramaten du. Goiko lerrotik, gora " +
                    "joateak beheko lerrora eramaten du. Ezer ez dago inoiz urruti.",
                "Idatzi ondoren, koadroa dagoen lekuan geratzen da. Bi letra " +
                    "berdin jarraian AUKERATU bi aldiz sakatzea dira.",
                "AUKERATUren ondoan ⌫ LETRA dago, azken letra ezabatzen " +
                    "duena, eta DENA EZABATU, esaldi osoa husten duena. " +
                    "DENA EZABATU birritan sakatzen da: lehenengoak ZIUR? " +
                    "esaten du eta gorri jartzen da, bigarrenak bakarrik " +
                    "husten du. Hark beste edozer gauza egiten badu, bertan behera uzten da.",
                "Saretako ⌫ teklak ere letra bat ezabatzen du.",
                "Gezien arteko tarteek ere balio dute: bi gezi artean " +
                    "erortzen den ukitu bat hurbilenera doa.",
            ),
        ),
        TutorialPage(
            title = "Letrak ukitu",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Hemen soilik dira letrak botoiak: nahi duena ukitzen du eta " +
                    "idatzi egiten da.",
                "Alde handiz modurik azkarrena da, baina hatza tekla jakin " +
                    "baten gainean jarri ahal duen bitartean soilik. Ukituak " +
                    "alboko teklan erortzen hasten badira, probatu geziak.",
                "Saretako ⌫ teklak azken letra ezabatzen du.",
                "Ezarpenetako DARDARAK EZ KONTUAN HARTU aukerak segidako " +
                    "ukitu sorta batek behin bakarrik balio dezan egiten du.",
            ),
        ),
        TutorialPage(
            title = "Sareta",
            lines = listOf(
                "Goiko lerroa: aplikazioak iradokitzen dituen hitzak, eta " +
                    "gero BAI, EZ eta ESALDIAK.",
                "BAI eta EZ esaldia hutsik dagoenean bakarrik daude. Idazten " +
                    "hasten denean, bi laukitxo horiek bi hitz iradoki gehiago " +
                    "bihurtzen dira: bost hiruren ordez. Beren kabuz " +
                    "itzultzen dira esaldia ezabatzen denean.",
                "Iradokitako hitzak aldatu egiten dira hark idazten duen " +
                    "bitartean. Bat aukeratzeak hitz osoa eta tartea idazten " +
                    "ditu: askotan bide laburrena da.",
                "Akatsak ere barkatzen ditu: letra bat nahi gabe errepikatzen " +
                    "bada edo bat ihes egiten badu, hitzak ateratzen " +
                    "jarraitzen dute. Bat aukeratzeak hitz osoa konpontzen " +
                    "du, ezer ezabatu beharrik gabe.",
                "Letrak ez daude ordena alfabetikoan, euskaraz erabiltzen " +
                    "diren ordenan baizik, ohikoenak gertu egon daitezen.",
                "× esaldi osoa ezabatzen du, eta horregatik dago azken izkinan.",
            ),
        ),
        TutorialPage(
            title = "Esaldiak",
            lines = listOf(
                "ESALDIAK botoiak esaldi osoak dituen pantaila bat irekitzen du.",
                "Bat aukeratzeak ozen esaten du. Ez da ezer idazten testu-koadroan.",
                "Idaztera itzultzeko: ATZERA, edo pantailaren ezkerreko erdia ukitu.",
            ),
        ),
        TutorialPage(
            title = "Axola duten ezarpenak",
            lines = listOf(
                "ESKANEATZEAREN ABIADURA: urrats bakoitzak zenbat irauten " +
                    "duen. Letrak ihes egiten badizkio, jarri mantsoago.",
                "DENBORA GEHIGARRIA LEHEN LETRAN: lerro batean sartzean, une " +
                    "bat gehiago ematen dio erreakzionatzeko.",
                "SAKATZEEN ARTEKO GUTXIENEKO DENBORA: sakatze bakar batek bi " +
                    "letra idazten baditu, igo.",
                "DARDARAK EZ KONTUAN HARTU: segidako ukitu sorta batek behin " +
                    "bakarrik balio du.",
                "Aldatu gauza bat aldi bakoitzean eta probatu. Dena batera " +
                    "aldatzeak ez du esaten zerk lagundu duen.",
            ),
        ),
        TutorialPage(
            title = "Modu blokeatua eta bateria",
            lines = listOf(
                "Modu blokeatuarekin, aplikazioa pantailan geratzen da eta " +
                    "ez da nahi gabe handik ateratzen.",
                "Irteteko: goian eskuinean dagoen ⚙, eta desaktibatu " +
                    "ezarpenetan. Beti ahal da.",
                "Bateria goian eskuinean ikusten da. Gorri jartzen da % 20tik behera.",
                "Bateriarik gabe gelditzen bada, ahotsik gabe gelditzen da. " +
                    "Merezi du entxufatuta uztea.",
            ),
        ),
        TutorialPage(
            title = "Hori da dena",
            lines = listOf(
                "Itxi hau eta probatu zuk minutu batzuetan, hari azaldu " +
                    "aurretik. Ondo azaltzeko modurik onena da.",
                "Zerbait ondo ez badabil, ia beti ezarpen bat da eta ez " +
                    "aplikazioa. Hasi abiaduratik.",
                "Gida hau berriz ireki dezakezu nahi duzunean: ⚙ → NOLA " +
                    "FUNTZIONATZEN DEN.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "Prestatzen",
        welcomeLines = listOf(
            "Aplikazio honekin, hitz egin eta eskuz idatzi ezin duen " +
                "pertsona batek esaldiak osatu eta ozen esan ditzake.",
            "Hainbat modutan erabil daiteke. Hari buruzko bi galdera, eta " +
                "berari egokitzen zaion moduan prestatuko da.",
            "Erantzun guztiak geroago alda daitezke ezarpenetan (⚙, goian " +
                "eskuinean).",
        ),
        phoneNotice = "Aplikazioa tableta batean erabiltzea gomendatzen dugu, ez mugikor batean: pantaila txiki batean teklak txikiegiak dira ondo sakatu eta irakurtzeko.",
        start = "HASI",
        methodTitle = "Nola aukeratuko du letra bat?",
        methodHint = "Ez zaude ziur? Hasi gutxien eskatzen diotenarekin. " +
            "Itxaroteak nekatzen badu, edo une egokia asmatzea zaila bada, " +
            "probatu geziak.",
        methodDirectDetail = "Tekla jakin bat ziurtasunez ukitu dezake. " +
            "Modurik azkarrena, balio duen bitartean.",
        methodArrowsDetail = "Botoi handi batzuk ukitu ditzake, baina ez " +
            "tekla txikiak. Ezer ez da mugitzen berak mugitu arte.",
        methodScanDetail = "Mugimendu bakarra egin dezake, sakagailu batekin " +
            "edo ukitu batekin. Markatzailea bere kabuz mugitzen da, eta " +
            "nahi duenera iristen denean sakatzen du.",
        pressTitle = "Zer sakatuko du?",
        pressHint = "Sakagailua Bluetooth bidez tabletarekin konektatzen den " +
            "botoi bat da. Lehenik parekatu tabletaren Bluetooth " +
            "ezarpenetan.",
        pressScreen = "Pantaila",
        pressScreenDetail = "Pantailaren eskuineko erdiak idazten du, " +
            "ezkerrekoak desegiten du. Ez da sakagailurik behar.",
        pressSwitches = "Sakagailuak",
        pressSwitchesDetail = "Sakagailu batek idazten du, beste batek " +
            "desegiten du. Pantaila ukitzeak ez du ezer egiten, beraz " +
            "gainean pausatutako besoak ez du kalterik egiten.",
        pressBoth = "Biak",
        pressBothDetail = "Sakagailuak, eta pantailaren erdiak ere bai.",
        practiceWord = "KAIXO",
        practiceWrite = "Praktika: idatzi",
        practiceWriteScan = "Itxaron koadroa lerrora iritsi arte, sakatu, eta " +
            "gero sakatu berriz letran.",
        practiceWriteArrows = "Mugitu koadroa geziekin, eta sakatu AUKERATU " +
            "letra bakoitzean.",
        practiceWriteDirect = "Ukitu letra bakoitza, banan-banan.",
        practiceUndo = "Orain desegin letra bat",
        practiceUndoScan = "Sakatu desegiteko sakagailua, edo ukitu " +
            "pantailaren ezkerreko erdia, ukitzea aktibatuta badago.",
        practiceUndoArrows = "Sakatu AUKERATUren ondoko ⌫ LETRA, edo " +
            "saretako ⌫ tekla.",
        practiceUndoDirect = "Ukitu ⌫ tekla.",
        practiceDone = "Eginda. Pertsonak erabiliko duen guztia da hori.",
        practiceFinish = "AMAITU",
        practiceAgain = "PRAKTIKATU",
    ),
    bindPressFor = "Sakatu botoia honetarako:",
    bindButtonFor = "Botoia honetarako:",
    bindWrite = "IDATZI",
    bindUndo = "DESEGIN",
    bindAssigned = "Esleituta",
    bindPressNow = "Sakatu orain, kontrolean edo sakagailuan, erabili nahi duzun botoia.",
    bindAnother = "Beste botoi bat gehitu nahi duzu ekintza berdinerako?",
    bindAddAnother = "BESTE BAT GEHITU",
    bindDone = "EGINDA",
    bindCancel = "UTZI",
    checkTitle = "Sakagailuak egiaztatzen",
    checkHint = "Sakatu sakagailu bakoitza. Pantaila hau bere kabuz ixten da.",
    checkWaiting = "Zain…",
    checkCode = "kodea",
    checkRoleWrite = "idazten du",
    checkRoleUndo = "desegiten du",
    checkRoleUnassigned = "esleitu gabe",
)
