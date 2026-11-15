package cat.merce.comunicador.ui

/**
 * The questions a fresh install asks before the walkthrough.
 *
 * The app has three ways of choosing a letter and several ways of pressing,
 * and which ones suit a person is the first thing a helper has to settle. The
 * walkthrough used to explain all of them, one after the other, to someone who
 * would only ever use one; now the helper says which, and the walkthrough only
 * shows the pages that apply.
 */
enum class OnboardingStep {

    /** What this is, and that every answer can be changed later. */
    Welcome,

    /** Touch the letters, arrows, or automatic scanning. */
    Method,

    /** Scanning only: the screen, physical switches, or both. */
    ScanPress,

    /** Arrows only: which part of the screen the pad goes on. */
    ArrowPlace,
}

/** What she presses to make the scan take a letter. */
enum class ScanPress {

    /** The two halves of the screen, and no switch. */
    Screen,

    /** Switches only. Touching the screen does nothing. */
    Switches,

    /** Either: switches, and the screen as well. */
    Both,
}

/** The words on the onboarding screens, for one language. */
class OnboardingText(
    val welcomeTitle: String,
    val welcomeLines: List<String>,
    val start: String,
    val skip: String,

    val methodTitle: String,
    val methodHint: String,
    val methodDirectDetail: String,
    val methodArrowsDetail: String,
    val methodScanDetail: String,

    val pressTitle: String,
    val pressHint: String,
    val pressScreen: String,
    val pressScreenDetail: String,
    val pressSwitches: String,
    val pressSwitchesDetail: String,
    val pressBoth: String,
    val pressBothDetail: String,
)
