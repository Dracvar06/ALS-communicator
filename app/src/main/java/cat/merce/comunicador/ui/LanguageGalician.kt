package cat.merce.comunicador.ui

// Galician: the words, letters, openers and phrases. Accents are reached
// through the suggestions; the Ñ has its own key, as in Spanish.
val GALICIAN = Language(
    code = "gl",
    displayName = "Galego",
    modelAsset = "gl-model.txt",
    ttsLocale = "gl",
    letterRows = listOf(
        listOf("E", "A", "O", "S", "N"),
        listOf("R", "I", "T", "U", "D", "C"),
        listOf("M", "L", "P", "Q", "B", "V"),
        listOf("G", "H", "F", "X", "Ñ", "Z"),
        listOf("Y", "K", "W", "J"),
    ),
    openers = listOf(
        "quero", "preciso", "axuda", "grazas", "por favor",
        "auga", "dor", "baño", "comer", "frío",
        "calor", "canso", "agarda", "para", "ola",
        "estou", "teño", "podo", "onde", "que",
    ),
    defaultPhrases = listOf(
        "Bos días",
        "Atópome mal",
        "A auga",
        "Quero ir ao xardín",
        "Quero ir ao comedor",
        "Teño frío",
        "Teño calor",
        "Necesito ir ao baño",
        "Avisade ás miñas fillas",
        "Chama á enfermeira",
        "Agarda un momento",
        "Estou ben",
        "Grazas",
        "Quérote",
    ),
    spaceLabel = "espazo",
    clearLabel = "BORRAR TUDO",
    yesLabel = "SI",
    noLabel = "NON",
    phrasesLabel = "FRASES",
    backLabel = "ATRÁS",
    settingsSpeed = "Velocidade do escaneo",
    settingsSecondsPerStep = "segundos por paso",
    settingsFaster = "máis rápido",
    settingsSlower = "máis lento",
    settingsFirstLetterExtra = "Tempo extra na primeira letra",
    settingsMinBetweenPresses = "Tempo mínimo entre pulsacións",
    settingsTouchTitle = "Tocar a pantalla para escribir",
    settingsTouchDetail = "Dereita: escribe. Esquerda: desfai.",
    settingsTremorTitle = "Ignorar os tremores",
    settingsTremorDetail = "Unha ráfaga de toques seguidos conta só unha vez.",
    settingsButtons = "Botóns",
    settingsWrite = "Escribir",
    settingsUndo = "Desfacer",
    settingsAssignWrite = "ASIGNAR ESCRIBIR",
    settingsAssignUndo = "ASIGNAR DESFACER",
    settingsClose = "PECHAR",
    settingsCheckButtons = "COMPROBAR OS PULSADORES",
    settingsEitherCloses = "Calquera dos dous pulsadores tamén pecha esta pantalla.",
    settingsLanguage = "Idioma",
    settingsLockedTitle = "Modo bloqueado",
    settingsLockedDetail = "A aplicación queda na pantalla e non se pode saír dela. Desactívao aquí para saír.",
    settingsBootTitle = "Abrir ao acender",
    settingsBootDetail = "A aplicación ábrese soa cando o dispositivo se reinicia.",
    settingsModeTitle = "Como se escolle unha letra",
    settingsModeDetail = "Co escaneo, o marcador móvese só e hai que esperar. " +
        "Coas frechas móvelo ti e nunca tes que esperar.",
    settingsModeScan = "Escaneo automático",
    settingsModeArrows = "Frechas",
    settingsModeDirect = "Tocar as letras",
    settingsArrowPlaceTitle = "Onde van as frechas",
    settingsArrowPlaceDetail = "Ponas onde o brazo non pase por riba ao ir " +
        "premer. Abaixo deixa todo o ancho da pantalla entre ESCOLLE e as " +
        "frechas, pero as frechas quedan máis pequenas.",
    settingsArrowColumn = "En columna, nun lado",
    settingsArrowBar = "Abaixo, nunha franxa",
    settingsChooseTop = "ESCOLLE arriba",
    settingsChooseLeft = "ESCOLLE á esquerda",
    settingsChooseDetail = "Pon ESCOLLE onde o brazo non pase por riba.",
    settingsArrowRight = "Á dereita",
    settingsArrowLeft = "Á esquerda",
    settingsArrowBottom = "Abaixo",
    arrowChoose = "ESCOLLE",
    sectionWriting = "COMO SE ESCRIBE",
    sectionWritingDetail = "A maneira de escoller as letras. Comeza por aquí: todo o demais deste apartado depende diso.",
    sectionTiming = "RITMO E TREMORES",
    sectionTimingDetail = "Para unha man que treme, ou que preme dúas veces sen querer.",
    sectionWords = "PALABRAS E LECTURA",
    sectionWordsDetail = "Que aparece na fila de palabras, e como se ve a frase escrita.",
    sectionSwitches = "PULSADORES E MANDOS",
    sectionSwitchesDetail = "Só fai falta se hai un pulsador ou un mando conectado.",
    sectionDevice = "O DISPOSITIVO",
    sectionDeviceDetail = "Configúrase unha vez, ao preparar o aparello, e non se volve tocar.",
    settingsForgiveTitle = "Perdoar os erros",
    settingsForgiveDetail = "Se unha letra se repite sen querer, ou se escapa unha, as palabras seguen saíndo. As que coinciden exactamente saen sempre primeiro.",
    settingsAiTitle = "Suxestións con IA (experimental)",
    settingsAiDetail = "Un pequeno modelo de linguaxe, dentro da tableta e sen internet, reordena as palabras suxeridas segundo o que se escribiu. As palabras son as mesmas; só cambia a orde. Se a tableta non é o bastante rápida, apágase soa.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Apagado."
            AiStatus.Loading -> "Cargando… A primeira vez tarda un pouco."
            AiStatus.On -> "Funcionando."
            AiStatus.NotEnoughMemory -> "Esta tableta non ten memoria dabondo. As suxestións normais funcionan igual."
            AiStatus.Failed -> "Non se puido cargar. As suxestións normais funcionan igual."
            AiStatus.TooSlow -> "Apagouse só: esta tableta era demasiado lenta. As suxestións normais funcionan igual."
        }
    },
    settingsBoldWritingTitle = "Frase en negriña",
    settingsBoldWritingDetail = "A frase que escribe, máis grosa, para lela de lonxe.",
    settingsArrowShapeTitle = "Como se ven as frechas",
    settingsArrowShapeDetail = "Separadas, cada frecha ten o seu botón e vese ata onde chega. En cruz vense como unha soa peza. O que responde ao toque é igual nos dous casos.",
    settingsArrowShapeJoined = "En cruz",
    settingsArrowShapeSeparate = "Separadas",
    settingsEraseKeysTitle = "Botóns de borrar ao carón de ESCOLLE",
    settingsEraseKeysDetail = "Borra unha letra, ou todo, sen ter que ir alí coas frechas. BORRAR TUDO pregunta antes.",
    arrowDeleteLetter = "⌫ LETRA",
    arrowClearConfirm = "SEGURO?",
    batteryLow = "Batería baixa",
    settingsTutorial = "COMO FUNCIONA",
    tutorialNext = "SEGUINTE",
    tutorialBack = "ATRÁS",
    tutorialActiveMode = "ESTE É O MODO ACTIVO",
    tutorialPressed = "PREME",
    tutorial = listOf(
        TutorialPage(
            title = "Para que serve",
            lines = listOf(
                "Serve para que unha persoa que non pode falar nin mover as " +
                    "mans poida escribir e dicir cousas.",
                "Segundo o que ela poida facer, toca as letras, move un cadro " +
                    "coas frechas ou fai un só movemento mentres a aplicación " +
                    "fai o demais.",
                "Ti es quen a prepara. Esta guía explica como funciona, " +
                    "para que llo poidas explicar ben.",
            ),
        ),
        TutorialPage(
            title = "As letras non son botóns",
            demo = TutorialDemo.Touch,
            modes = setOf(InputMode.Scan, InputMode.Arrows),
            lines = listOf(
                "Esta é a parte que máis custa de entender, e paga a pena " +
                    "lela dúas veces.",
                "As letras da pantalla NON son botóns. Tocar a letra que " +
                    "quere non fai nada. Están para lelas, non para premelas.",
                "Co escaneo automático, os botóns son dous e ocupan media " +
                    "pantalla cada un: toda a metade DEREITA escribe, toda a " +
                    "metade ESQUERDA desfai.",
                "Non se ven, e é adrede: a grella é para mirala, e unhas " +
                    "liñas por riba só taparían letras.",
                "Isto quere dicir que ela pode tocar onde lle veña ben de " +
                    "cada metade. Non ten que acertar en nada, e non pode " +
                    "fallar o botón.",
                "Coas frechas é distinto: alí os botóns si se ven, son as " +
                    "catro frechas e ESCOLLE. As letras seguen sen ser botóns.",
                "Pódese desactivar nos axustes, en TOCAR A PANTALLA PARA " +
                    "ESCRIBIR, se alguén toca a pantalla sen querer.",
            ),
        ),
        TutorialPage(
            title = "Escaneo automático",
            demo = TutorialDemo.Scan,
            modes = setOf(InputMode.Scan),
            lines = listOf(
                "O cadro vermello baixa fila por fila, el só.",
                "Cando está na fila que ela quere, preme. Agora o cadro " +
                    "móvese de letra en letra dentro desa fila.",
                "Cando está na letra que quere, volve premer. A letra " +
                    "escríbese e todo comeza de novo desde arriba.",
                "Se se lle pasa a letra, non hai que facer nada: despois de " +
                    "dúas voltas a fila déixase soa e volve baixar.",
                "Tocar a metade esquerda da pantalla desfai a última cousa.",
            ),
        ),
        TutorialPage(
            title = "Frechas",
            demo = TutorialDemo.Arrows,
            modes = setOf(InputMode.Arrows),
            lines = listOf(
                "O cadro queda quieto ata que ela o move. Non hai nada que " +
                    "esperar e non se pode chegar tarde.",
                "As catro frechas móvenno unha casa cada vez. ESCOLLE " +
                    "escribe a letra que está marcada.",
                "Dá a volta: desde a primeira letra dunha fila, a esquerda " +
                    "leva á última. Desde a fila de arriba, cara arriba leva " +
                    "á de abaixo. Nada queda nunca lonxe.",
                "Despois de escribir, o cadro queda onde está. Dúas letras " +
                    "iguais seguidas son dúas pulsacións de ESCOLLE.",
                "Ao carón de ESCOLLE hai ⌫ LETRA, que borra a última letra, " +
                    "e BORRAR TUDO, que baleira a frase enteira. BORRAR TUDO " +
                    "prémese dúas veces: a primeira pasa a dicir SEGURO? e " +
                    "ponse vermella, e só a segunda baleira. Calquera outra " +
                    "cousa que faga a anula.",
                "A tecla ⌫ da grella tamén borra unha letra.",
                "Os espazos entre as frechas tamén contan: un toque que cae " +
                    "entre dúas vai á máis próxima.",
            ),
        ),
        TutorialPage(
            title = "Tocar as letras",
            modes = setOf(InputMode.Direct),
            lines = listOf(
                "Aquí, e só aquí, as letras son botóns: ela toca a que quere " +
                    "e escríbese.",
                "É con moito o xeito máis rápido, pero só mentres ela poida " +
                    "pousar o dedo nunha tecla concreta. Se os toques empezan " +
                    "a caer na tecla do lado, proba as frechas.",
                "A tecla ⌫ da grella borra a última letra.",
                "IGNORAR OS TREMORES nos axustes fai que unha ráfaga de " +
                    "toques seguidos conte só unha vez.",
            ),
        ),
        TutorialPage(
            title = "A grella",
            lines = listOf(
                "Fila de arriba: palabras que a aplicación suxire, e despois " +
                    "SI, NON e FRASES.",
                "SI e NON só están coa frase baleira. Ao comezar a escribir, " +
                    "esas dúas casas pasan a ser dúas palabras suxeridas máis: " +
                    "cinco en vez de tres. Volven soas cando a frase se borra.",
                "As palabras suxeridas cambian mentres ela escribe. Escoller " +
                    "unha escribe a palabra enteira e o espazo: moitas veces " +
                    "é o camiño máis curto.",
                "Tamén perdoa erros: se unha letra se repite sen querer ou " +
                    "se escapa unha, as palabras seguen saíndo. Escoller unha " +
                    "arranxa a palabra enteira, sen ter que borrar.",
                "As letras non están en orde alfabética, senón en orde de " +
                    "uso en galego, para que as máis frecuentes queden preto.",
                "× borra toda a frase, e por iso está no último currunchiño.",
            ),
        ),
        TutorialPage(
            title = "Frases",
            lines = listOf(
                "FRASES abre unha pantalla con frases enteiras.",
                "Escoller unha dícea en voz alta. Non se escribe nada no " +
                    "cadro de texto.",
                "Para volver escribir: ATRÁS, ou tocar a metade esquerda da " +
                    "pantalla.",
            ),
        ),
        TutorialPage(
            title = "Os axustes que importan",
            lines = listOf(
                "VELOCIDADE DO ESCANEO: canto dura cada paso. Se se lle " +
                    "escapan letras, faino máis lento.",
                "TEMPO EXTRA NA PRIMEIRA LETRA: ao entrar nunha fila, dálle " +
                    "un momento máis para reaccionar.",
                "TEMPO MÍNIMO ENTRE PULSACIÓNS: se unha soa pulsación " +
                    "escribe dúas letras, súbeo.",
                "IGNORAR OS TREMORES: unha ráfaga de toques seguidos conta " +
                    "só unha vez.",
                "Cambia unha cousa cada vez e próbaa. Cambialo todo á vez " +
                    "non di nada sobre o que axudou.",
            ),
        ),
        TutorialPage(
            title = "Modo bloqueado e batería",
            lines = listOf(
                "Co modo bloqueado, a aplicación queda na pantalla e non se " +
                    "sae dela sen querer.",
                "Para saír: a roda ⚙ de arriba á dereita, e desactívao nos " +
                    "axustes. Sempre se pode.",
                "A batería vese arriba á dereita. Ponse vermella por baixo " +
                    "do 20 %.",
                "Se se queda sen batería queda sen voz. Paga a pena deixala " +
                    "enchufada.",
            ),
        ),
        TutorialPage(
            title = "Isto é todo",
            lines = listOf(
                "Pecha isto e próbao ti, uns minutos, antes de explicarllo a " +
                    "ela. É a mellor maneira de explicalo ben.",
                "Se algo non lle vai ben, case sempre é un axuste e non a " +
                    "aplicación. Comeza pola velocidade.",
                "Podes volver abrir esta guía cando queiras: ⚙ → COMO " +
                    "FUNCIONA.",
            ),
        ),
    ),
    onboarding = OnboardingText(
        welcomeTitle = "Preparalo",
        welcomeLines = listOf(
            "Esta aplicación serve para que unha persoa que non pode falar " +
                "nin escribir a man constrúa frases e as diga en voz alta.",
            "Hai varias maneiras de usala. Dúas preguntas sobre ela, e " +
                "queda preparada como lle veña ben.",
            "Todas as respostas poden cambiarse despois nos axustes (⚙, " +
                "arriba á dereita).",
        ),
        start = "COMEZAR",
        skip = "SALTAR",
        methodTitle = "Como escollerá unha letra?",
        methodHint = "Non estás seguro? Comeza pola que lle pida menos. Se " +
            "esperar a cansa, ou lle custa acertar co momento, proba as " +
            "frechas.",
        methodDirectDetail = "Pode tocar con seguridade unha tecla concreta. " +
            "O xeito máis rápido, mentres dure.",
        methodArrowsDetail = "Pode tocar uns poucos botóns grandes, pero non " +
            "teclas pequenas. Nada se move ata que ela o move.",
        methodScanDetail = "Pode facer un só movemento, cun pulsador ou cun " +
            "toque. O marcador móvese só e ela preme cando chega ao que quere.",
        pressTitle = "Que premerá?",
        pressHint = "Un pulsador é un botón que se conecta á tableta por " +
            "Bluetooth. Primeiro emparéllao nos axustes de Bluetooth da tableta.",
        pressScreen = "A pantalla",
        pressScreenDetail = "A metade dereita da pantalla escribe e a " +
            "esquerda desfai. Non fai falta pulsador.",
        pressSwitches = "Pulsadores",
        pressSwitchesDetail = "Un pulsador escribe e outro desfai. Tocar a " +
            "pantalla non fai nada, así que un brazo apoiado nela non molesta.",
        pressBoth = "Os dous",
        pressBothDetail = "Pulsadores, e tamén as metades da pantalla.",
    ),
    bindPressFor = "Preme o botón para",
    bindButtonFor = "Botón para",
    bindWrite = "ESCRIBIR",
    bindUndo = "DESFACER",
    bindAssigned = "Asignados",
    bindPressNow = "Preme agora, no mando ou no pulsador, o botón que queiras usar.",
    bindAnother = "Queres engadir outro botón para a mesma acción?",
    bindAddAnother = "ENGADIR OUTRO",
    bindDone = "FEITO",
    bindCancel = "CANCELAR",
    checkTitle = "Comprobación dos pulsadores",
    checkHint = "Preme cada pulsador. Esta pantalla péchase soa.",
    checkWaiting = "Esperando…",
    checkCode = "código",
    checkRoleWrite = "escribe",
    checkRoleUndo = "desfai",
    checkRoleUnassigned = "sen asignar",
)
