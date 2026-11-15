package cat.merce.comunicador.ui

/**
 * European Portuguese. Letters are in order of frequency in Portuguese text
 * (A, E, O, S, R lead). Accents are reached through the suggestions, and Ç is
 * on the grid.
 */
val PORTUGUESE = Language(
    code = "pt",
    displayName = "Português",
    modelAsset = "pt-model.txt",
    ttsLocale = "pt",
    letterRows = listOf(
        listOf("A", "E", "O", "S", "R"),
        listOf("I", "M", "T", "U", "N", "D"),
        listOf("C", "L", "P", "V", "Q", "H"),
        listOf("G", "B", "F", "Z", "J", "Ç"),
        listOf("X", "Y", "K", "W"),
    ),
    openers = listOf(
        "quero", "preciso", "ajuda", "obrigada", "por favor",
        "água", "dor", "casa de banho", "comer", "frio",
        "calor", "cansada", "espera", "pára", "olá",
        "estou", "tenho", "posso", "onde", "o que",
    ),
    defaultPhrases = listOf(
        "Bom dia",
        "Sinto-me mal",
        "A água",
        "Quero ir ao jardim",
        "Quero ir à sala de jantar",
        "Tenho frio",
        "Tenho calor",
        "Preciso de ir à casa de banho",
        "Avisem as minhas filhas",
        "Chamem a enfermeira",
        "Espera um momento",
        "Estou bem",
        "Obrigada",
        "Amo-te",
    ),
    spaceLabel = "espaço",
    clearLabel = "APAGAR TUDO",
    yesLabel = "SIM",
    noLabel = "NÃO",
    phrasesLabel = "FRASES",
    backLabel = "VOLTAR",
    settingsSpeed = "Velocidade da varredura",
    settingsSecondsPerStep = "segundos por passo",
    settingsFaster = "mais rápido",
    settingsSlower = "mais lento",
    settingsFirstLetterExtra = "Tempo extra na primeira letra",
    settingsMinBetweenPresses = "Tempo mínimo entre toques",
    settingsTouchTitle = "Tocar no ecrã para escrever",
    settingsTouchDetail = "Direita: escreve. Esquerda: desfaz.",
    settingsTremorTitle = "Ignorar tremores",
    settingsTremorDetail = "Uma rajada de toques seguidos conta apenas uma vez.",
    settingsButtons = "Botões",
    settingsWrite = "Escrever",
    settingsUndo = "Desfazer",
    settingsAssignWrite = "ATRIBUIR ESCREVER",
    settingsAssignUndo = "ATRIBUIR DESFAZER",
    settingsClose = "FECHAR",
    settingsCheckButtons = "VERIFICAR OS INTERRUPTORES",
    settingsEitherCloses = "Qualquer um dos dois interruptores também fecha este ecrã.",
    settingsLanguage = "Idioma",
    settingsLockedTitle = "Modo bloqueado",
    settingsLockedDetail = "A aplicação fica no ecrã e não é possível sair. Desative isto aqui para sair.",
    settingsBootTitle = "Abrir ao ligar",
    settingsBootDetail = "A aplicação abre-se sozinha quando o dispositivo reinicia.",
    settingsModeTitle = "Como se escolhe uma letra",
    settingsModeDetail = "Com a varredura, o destaque move-se sozinho e espera-se. " +
        "Com as setas, move-se o destaque à mão, sem nunca esperar.",
    settingsModeScan = "Varredura automática",
    settingsModeArrows = "Setas",
    settingsModeDirect = "Tocar nas letras",
    settingsArrowPlaceTitle = "Onde ficam as setas",
    settingsArrowPlaceDetail = "Ponha-as onde o braço não passe por cima " +
        "ao ir carregar. Em baixo, fica a largura toda do ecrã entre " +
        "ESCOLHER e as setas, mas as setas ficam mais pequenas.",
    settingsArrowColumn = "Em coluna, num dos lados",
    settingsArrowBar = "Em baixo, numa faixa",
    settingsChooseTop = "ESCOLHER em cima",
    settingsChooseLeft = "ESCOLHER à esquerda",
    settingsChooseDetail = "Ponha ESCOLHER onde o braço não passe por cima.",
    settingsArrowRight = "À direita",
    settingsArrowLeft = "À esquerda",
    settingsArrowBottom = "Em baixo",
    arrowChoose = "ESCOLHER",
    sectionWriting = "COMO SE ESCREVE",
    sectionWritingDetail = "A forma de escolher as letras. Comece aqui: tudo o resto desta secção depende disto.",
    sectionTiming = "RITMO E TREMORES",
    sectionTimingDetail = "Para uma mão que treme, ou que carrega duas vezes sem querer.",
    sectionWords = "PALAVRAS E LEITURA",
    sectionWordsDetail = "O que aparece na fila das palavras e o aspeto da frase escrita.",
    sectionSwitches = "INTERRUPTORES E COMANDOS",
    sectionSwitchesDetail = "Só é preciso se houver um interruptor ou um comando ligado.",
    sectionDevice = "O DISPOSITIVO",
    sectionDeviceDetail = "Configura-se uma vez, ao preparar o aparelho, e depois não se mexe mais.",
    settingsForgiveTitle = "Perdoar erros",
    settingsForgiveDetail = "Se uma letra se repete sem querer, ou se perde uma, as palavras continuam a aparecer. As que coincidem exatamente aparecem sempre primeiro.",
    settingsAiTitle = "Sugestões com IA (experimental)",
    settingsAiDetail = "Um pequeno modelo de linguagem, dentro do tablet e sem internet, reordena as palavras sugeridas conforme o que foi escrito. As palavras são as mesmas; só muda a ordem. Se o tablet não for suficientemente rápido, desliga-se sozinho.",
    settingsAiStatus = { status ->
        when (status) {
            AiStatus.Off -> "Desligado."
            AiStatus.Loading -> "A carregar… A primeira vez demora um pouco."
            AiStatus.On -> "A funcionar."
            AiStatus.NotEnoughMemory -> "Este tablet não tem memória suficiente. As sugestões normais funcionam como antes."
            AiStatus.Failed -> "Não foi possível carregá-lo. As sugestões normais funcionam como antes."
            AiStatus.TooSlow -> "Desligou-se sozinho: este tablet era demasiado lento. As sugestões normais funcionam como antes."
        }
    },
    settingsBoldWritingTitle = "Frase a negrito",
    settingsBoldWritingDetail = "A frase que ela escreve, mais grossa, para ler de longe.",
    settingsArrowShapeTitle = "Aspeto das setas",
    settingsArrowShapeDetail = "Separadas, cada seta tem o seu botão e vê-se até onde chega. Juntas, desenham-se como uma só peça. O que responde ao toque é igual nos dois casos.",
    settingsArrowShapeJoined = "Juntas",
    settingsArrowShapeSeparate = "Separadas",
    settingsEraseKeysTitle = "Botões de apagar ao lado de ESCOLHER",
    settingsEraseKeysDetail = "Apagam uma letra, ou tudo, sem ser preciso ir lá com as setas. APAGAR TUDO pergunta primeiro.",
    arrowDeleteLetter = "⌫ LETRA",
    arrowClearConfirm = "TEM A CERTEZA?",
    batteryLow = "Bateria fraca",
    settingsTutorial = "COMO FUNCIONA",
    tutorialNext = "SEGUINTE",
    tutorialBack = "VOLTAR",
    tutorialActiveMode = "ESTE É O MODO EM USO",
    tutorialPressed = "CARREGUE",
    tutorial = listOf(
        TutorialPage(
            title = "Para que serve",
            lines = listOf(
                "Serve para que uma pessoa que não pode falar nem mexer as " +
                    "mãos possa escrever e dizer coisas em voz alta.",
                "Ela faz um único movimento: tocar no ecrã. Tudo o resto " +
                    "tem de ser feito pela aplicação.",
                "É você quem a prepara. Este guia explica como funciona, " +
                    "para que lho possa explicar bem.",
            ),
        ),
        TutorialPage(
            title = "Duas formas de escolher uma letra",
            lines = listOf(
                "Há duas, e mudam-se nas definições. Nenhuma é melhor: " +
                    "depende da pessoa.",
                "VARREDURA AUTOMÁTICA: o destaque vermelho move-se sozinho. " +
                    "Ela espera e carrega quando chega ao que quer.",
                "SETAS: o destaque não se move sozinho. Ela move-o com " +
                    "quatro setas e carrega em ESCOLHER.",
                "Se esperar a cansa, experimente as setas. Se o difícil é " +
                    "acertar no momento, experimente também as setas.",
            ),
        ),
        TutorialPage(
            title = "As letras não são botões",
            demo = TutorialDemo.Touch,
            lines = listOf(
                "Esta é a parte que mais custa a perceber, e vale a pena lê-la " +
                    "duas vezes.",
                "As letras do ecrã NÃO são botões. Tocar na letra que ela " +
                    "quer não faz nada. Estão ali para serem lidas, não para " +
                    "serem carregadas.",
                "Com a varredura automática, os botões são dois e ocupam " +
                    "meio ecrã cada um: toda a metade DIREITA escreve, toda a " +
                    "metade ESQUERDA desfaz.",
                "Não se veem, de propósito: a grelha é para ser olhada, e " +
                    "linhas desenhadas por cima só tapariam letras.",
                "Isto quer dizer que ela pode tocar onde lhe for mais " +
                    "cómodo dentro da metade certa. Não tem de acertar em " +
                    "nada e não pode falhar o botão.",
                "Com as setas é diferente: aí os botões veem-se, são as " +
                    "quatro setas e ESCOLHER. As letras continuam a não ser " +
                    "botões.",
                "Pode desativar-se nas definições, em TOCAR NO ECRÃ PARA " +
                    "ESCREVER, se alguém tocar no ecrã sem querer.",
            ),
        ),
        TutorialPage(
            title = "Varredura automática",
            demo = TutorialDemo.Scan,
            lines = listOf(
                "O destaque vermelho desce fila a fila, sozinho.",
                "Quando está na fila que ela quer, carrega. Agora o destaque " +
                    "move-se letra a letra ao longo dessa fila.",
                "Quando está na letra que quer, volta a carregar. A letra " +
                    "é escrita e tudo recomeça de cima.",
                "Se a letra passar, não é preciso fazer nada: depois de duas " +
                    "voltas, a fila larga-se sozinha e o destaque volta a descer.",
                "Tocar na metade esquerda do ecrã desfaz a última coisa.",
            ),
        ),
        TutorialPage(
            title = "Setas",
            demo = TutorialDemo.Arrows,
            lines = listOf(
                "O destaque fica parado até ela o mover. Não há nada para " +
                    "esperar e não se pode chegar tarde.",
                "As quatro setas movem-no uma casa de cada vez. ESCOLHER " +
                    "escreve a letra que está em destaque.",
                "Dá a volta: a partir da primeira letra de uma fila, " +
                    "esquerda leva à última. A partir da fila de cima, " +
                    "cima leva à de baixo. Nada fica nunca longe.",
                "Depois de escrever, o destaque fica onde está. Duas letras " +
                    "iguais seguidas são dois toques em ESCOLHER, e mais nada.",
                "Ao lado de ESCOLHER está ⌫ LETRA, que apaga a última " +
                    "letra, e APAGAR TUDO, que esvazia a frase inteira. " +
                    "APAGAR TUDO carrega-se duas vezes: a primeira passa a " +
                    "vermelho e diz TEM A CERTEZA?, e só a segunda esvazia a " +
                    "frase. Qualquer outra coisa que ela faça cancela-o.",
                "A tecla ⌫ da grelha também apaga uma letra.",
                "Os espaços entre as setas também contam: um toque que caia " +
                    "entre duas vai para a mais próxima.",
            ),
        ),
        TutorialPage(
            title = "A grelha",
            lines = listOf(
                "Fila de cima: palavras que a aplicação sugere, e depois " +
                    "SIM, NÃO e FRASES.",
                "SIM e NÃO só estão lá com a frase vazia. Quando ela começa " +
                    "a escrever, essas duas casas passam a ser mais duas " +
                    "palavras sugeridas: cinco em vez de três. Voltam " +
                    "sozinhas quando a frase é apagada.",
                "As palavras sugeridas mudam enquanto ela escreve. Escolher " +
                    "uma escreve a palavra inteira e o espaço a seguir, o que " +
                    "muitas vezes é de longe o caminho mais curto.",
                "Também perdoa erros: se uma letra se repete ou se perde " +
                    "uma, as palavras continuam a aparecer. Escolher uma " +
                    "corrige a palavra inteira, sem ter de apagar nada.",
                "As letras não estão por ordem alfabética, mas por ordem de " +
                    "uso em português, para que as mais comuns fiquem perto.",
                "× apaga a frase toda, e por isso fica no último canto da " +
                    "grelha.",
            ),
        ),
        TutorialPage(
            title = "Frases",
            lines = listOf(
                "FRASES abre um ecrã com frases inteiras.",
                "Escolher uma diz-a em voz alta. Não se escreve nada na " +
                    "caixa de texto.",
                "Para voltar a escrever: VOLTAR, ou tocar na metade " +
                    "esquerda do ecrã.",
            ),
        ),
        TutorialPage(
            title = "As definições que importam",
            lines = listOf(
                "VELOCIDADE DA VARREDURA: quanto dura cada passo. Se lhe " +
                    "escapam letras, torne-a mais lenta.",
                "TEMPO EXTRA NA PRIMEIRA LETRA: dá-lhe um momento a mais " +
                    "para reagir logo depois de entrar numa fila.",
                "TEMPO MÍNIMO ENTRE TOQUES: se um único toque escreve duas " +
                    "letras, aumente-o.",
                "IGNORAR TREMORES: uma rajada de toques seguidos conta " +
                    "apenas uma vez.",
                "Mude uma coisa de cada vez e experimente. Mudar tudo ao " +
                    "mesmo tempo não diz nada sobre o que ajudou.",
            ),
        ),
        TutorialPage(
            title = "Modo bloqueado e bateria",
            lines = listOf(
                "No modo bloqueado, a aplicação fica no ecrã e não se sai " +
                    "dela por engano.",
                "Para sair: a roda ⚙ no canto superior direito, e desative-o " +
                    "nas definições. É sempre possível.",
                "A bateria vê-se no canto superior direito. Fica vermelha " +
                    "abaixo dos 20 %.",
                "Uma bateria vazia é uma pessoa sem voz. Vale a pena deixá-la " +
                    "ligada à corrente.",
            ),
        ),
        TutorialPage(
            title = "É tudo",
            lines = listOf(
                "Feche isto e experimente você mesmo durante uns minutos " +
                    "antes de lho explicar. É a melhor maneira de o explicar bem.",
                "Se alguma coisa não lhe corre bem, quase sempre é uma " +
                    "definição e não a aplicação. Comece pela velocidade.",
                "Pode voltar a abrir este guia quando quiser: ⚙ → COMO " +
                    "FUNCIONA.",
            ),
        ),
    ),
    bindPressFor = "Carregue no botão para",
    bindButtonFor = "Botão para",
    bindWrite = "ESCREVER",
    bindUndo = "DESFAZER",
    bindAssigned = "Atribuídos",
    bindPressNow = "Carregue agora, no comando ou no interruptor, no botão que quer usar.",
    bindAnother = "Adicionar outro botão para a mesma ação?",
    bindAddAnother = "ADICIONAR OUTRO",
    bindDone = "FEITO",
    bindCancel = "CANCELAR",
    checkTitle = "Verificação dos interruptores",
    checkHint = "Carregue em cada interruptor. Este ecrã fecha-se sozinho.",
    checkWaiting = "A aguardar…",
    checkCode = "código",
    checkRoleWrite = "escreve",
    checkRoleUndo = "desfaz",
    checkRoleUnassigned = "sem atribuição",
)
