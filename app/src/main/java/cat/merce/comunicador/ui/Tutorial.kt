package cat.merce.comunicador.ui

/**
 * One screen of the walkthrough.
 *
 * Written for the **helper**, not for her. The app can be set up perfectly and
 * still fail, because the person sitting beside her explained it wrong — which
 * is what happened the first time it was tried with somebody who had only had
 * it described to them second hand. A tool that one person understands is a
 * tool that stops working the day that person is not in the room.
 *
 * @param demo a small moving picture of the thing being described. Some of this
 *   cannot be explained in words to somebody who has never seen it happen.
 */
class TutorialPage(
    val title: String,
    val lines: List<String>,
    val demo: TutorialDemo? = null,
)

/** The moving picture a page is showing. */
enum class TutorialDemo {

    /** The highlight walking the rows, then a row, with presses along the way. */
    Scan,

    /** The highlight being steered a cell at a time, then chosen. */
    Arrows,

    /**
     * The two invisible halves of the screen, lighting up in turn.
     *
     * The one thing people reliably get wrong. A grid of letters looks like a
     * grid of buttons, so they tap a letter, nothing happens, and they conclude
     * the app is broken — when in fact the letters are never buttons and the
     * whole right half of the screen is one enormous one.
     */
    Touch,
}
