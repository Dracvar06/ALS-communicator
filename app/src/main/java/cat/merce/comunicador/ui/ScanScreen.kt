package cat.merce.comunicador.ui

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import cat.merce.comunicador.BuildConfig
import cat.merce.comunicador.input.Switch
import cat.merce.comunicador.input.SwitchFilter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToLong

private val Background = Color(0xFF000000)
private val CellIdle = Color(0xFF1F1F1F)
private val CellLit = Color(0xFFD32F2F)
private val Ink = Color(0xFFFFFFFF)
private val DimInk = Color(0xFF9E9E9E)
private val TextAreaBackground = Color(0xFF121212)
private val SettingsCorner = Color(0xFF2A2A2A)
private val SettingsCornerInk = Color(0xFF6E6E6E)
private val ArrowKeyColour = Color(0xFF3A3A3A)
private val ArrowChoose = Color(0xFF4A4A4A)
private val ArrowArmed = Color(0xFF8E1F1F)
private val ArrowGlyph = Color(0xFFD6D6D6)
private val LowBattery = Color(0xFFE53935)

/**
 * How wide the arrow pad is, against the grid's 1. A third of the screen, which
 * is what it took before the arrows were comfortably reachable; the letters are
 * sized from their cells, so the grid gives up width without giving up clarity.
 */
private const val ARROW_PAD_WEIGHT = 0.5f

/** Up, left, right, down, choose, erase a letter, erase everything. */
private const val ARROW_BUTTONS = 7

/**
 * The share of the pad taken by each erase button, where choose takes
 * [CHOOSE_BAR_WEIGHT] and the empty gap [ARROW_GAP_WEIGHT].
 *
 * Smaller than choose, because choose is pressed once per letter and these are
 * pressed once per mistake — and because every pixel given to a button beside
 * the arrows is a pixel taken off the gap that keeps the two apart.
 */
private const val ERASE_WEIGHT = 0.5f

/** Choose's share of the strip once it is sharing with the erase buttons. */
private const val CHOOSE_BAR_WEIGHT = 0.8f

/** What is left of the strip: empty, between the erase buttons and the arrows. */
private const val ARROW_GAP_WEIGHT = 0.7f

/** The erase pair's share of a side column's height, against the cross's 1. */
private const val ERASE_ROW_WEIGHT = 0.45f

/**
 * The choose button's share of the pad's height, against the arrow cross's 1.
 * It is pressed once per letter where an arrow is pressed two or three times,
 * so it does not need to be the biggest thing here — but a missed choose costs
 * a letter, where a missed arrow costs a moment.
 *
 * Also chosen so the cross above ends up roughly square. The four arrows have
 * to be congruent, which means they are sized off the *shorter* side of their
 * box, so any height beyond its width would simply be left empty. This hands
 * that height to the choose button instead.
 */
private const val CHOOSE_WEIGHT = 0.7f

/**
 * How tall the bottom strip is, against the grid's 1.
 *
 * This one number is the whole compromise of the bottom placement. The arrows
 * are bounded by a square that cannot be taller than the strip, so it is the
 * only way to make them bigger — and every pixel it gains is a pixel off the
 * height of the grid she reads all day. Raised from a third to two fifths once
 * the arrows turned out to be too small to aim at; going much beyond this
 * starts to squeeze the letters.
 */
private const val ARROW_BAR_WEIGHT = 0.7f

/** How far the base of each arrow sits back from the middle, as a fraction. */
private const val ARROW_HUB = 0.23f

/**
 * Half the width of an arrow's base, as a fraction of the square.
 *
 * **Must stay below [ARROW_HUB].** Each arrow lives in its own quarter of the
 * square, and the two ends of its base sit at (±half, hub) from the middle;
 * once half reaches hub those corners cross the diagonal into the next arrow's
 * quarter, and the four triangles fuse into one four-pointed star. Which is
 * exactly what happened the first time this was drawn.
 */
private const val ARROW_BASE_HALF = 0.17f

/** Blunts the three points. Cosmetic; it does not change what can be tapped. */
private const val ARROW_ROUNDING = 0.02f

/**
 * The most of a side column's height the arrows may take.
 *
 * The arrows have to stay congruent, so they are bounded by a square, and in a
 * column that square wants to be as wide as the column — which on a phone is
 * most of the height. This is the ceiling that stops it eating choose entirely.
 */
private const val CROSS_MAX_HEIGHT_SHARE = 0.66f

/** The erase pair's share of whatever height the arrows leave behind. */
private const val ERASE_SHARE_OF_LEFTOVER = 0.4f

/** An arrow button's share of the pad, per side. The pad is three cells wide. */
private const val ARROW_CELL = 1f / 3f

/** How much of its cell an arrow button fills, leaving the rest as the gap. */
private const val ARROW_BUTTON_FILL = 0.92f

/** The triangle inside a separated button, as a fraction of the button. */
private const val ARROW_GLYPH = 0.62f

/** Beyond this an arrow looks shouted rather than clear, even given the room. */
private const val ARROW_MAX_TEXT_SP = 56f

/** Below this the readout turns red, which is about an hour of use left. */
private const val LOW_BATTERY_PERCENT = 20

@Composable
fun ScanScreen(controller: ScanController) {

    // The clock lives here and nowhere else. The scan machine itself never
    // measures time; it only gets told that a step has passed. How long each
    // step lasts comes from the controller, so the first letter of a row can be
    // held longer than the rest.
    //
    // Restarts whenever the speed, the first-letter time, or the timing epoch
    // changes. The epoch is bumped on every press, which resets the phase so
    // the letter she just landed on gets its full time instead of whatever was
    // left of a step already in progress.
    LaunchedEffect(
        controller,
        controller.scanIntervalMs,
        controller.firstCellExtraMs,
        controller.timingEpoch,
    ) {
        // Count from a fixed starting point rather than sleeping for the
        // interval each time. Plain repeated delays drift, and a scan that
        // slowly loses its rhythm is exactly what the brief forbids.
        var nextStepAt = SystemClock.elapsedRealtime()
        while (true) {
            nextStepAt += controller.currentStepDurationMs()
            delay((nextStepAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L))
            controller.tick()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when {
            controller.bindingRole != null || controller.bindingMoreRole != null ->
                BindingPanel(controller)
            controller.inTutorial -> TutorialPanel(controller)
            controller.inDiagnostics -> DiagnosticsPanel(controller)
            controller.inSettings -> SettingsPanel(controller)
            else -> {
                val arrows = controller.inputMode == InputMode.Arrows
                val placement = controller.arrowPlacement

                if (arrows && placement.alongTheBottom) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            GridContent(controller, arrows)
                        }
                        ArrowPad(
                            controller = controller,
                            placement = placement,
                            modifier = Modifier.fillMaxWidth().weight(ARROW_BAR_WEIGHT),
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // The pad takes a slice off one side and the grid gets
                        // the rest, rather than floating over the letters:
                        // something she has to hit must never sit on top of
                        // something she has to read.
                        if (arrows && placement == ArrowPlacement.Left) {
                            ArrowPad(
                                controller = controller,
                                placement = placement,
                                modifier = Modifier.weight(ARROW_PAD_WEIGHT).fillMaxHeight(),
                            )
                        }

                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            GridContent(controller, arrows)
                        }

                        if (arrows && placement == ArrowPlacement.Right) {
                            ArrowPad(
                                controller = controller,
                                placement = placement,
                                modifier = Modifier.weight(ARROW_PAD_WEIGHT).fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Shows the raw key behind every press, so a carer can find out what a switch
 * interface actually sends rather than anyone guessing.
 *
 * Plug the interface in, open this, press each switch. The name is what to put
 * into WRITE_KEYS and UNDO_KEYS in MainActivity. The gap column makes contact
 * bounce visible: a burst of presses milliseconds apart is one press of a
 * bouncing switch, and the debounce setting is what to raise.
 */
@Composable
private fun DiagnosticsPanel(controller: ScanController) {

    // This screen swallows every key to report it, so a switch cannot leave it.
    // It closes itself a short while after the carer's last touch. Button
    // presses do not extend it, so a drifting controller cannot pin it open.
    LaunchedEffect(controller.diagnosticsActivity) {
        delay(ScanController.DIAGNOSTICS_IDLE_MS)
        controller.closeSettings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { controller.noteDiagnosticsActivity() } }
            .padding(40.dp)
    ) {
        Text(
            text = controller.language.checkTitle,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = controller.language.checkHint,
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        if (controller.recentKeys.isEmpty()) {
            Text(
                text = controller.language.checkWaiting,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 28.sp
            )
        }

        for (key in controller.recentKeys) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(
                    text = key.name,
                    color = if (key.accepted) Ink else DimInk,
                    fontFamily = Hyperlegible,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    modifier = Modifier.weight(3f)
                )
                Text(
                    text = "${controller.language.checkCode} ${key.keyCode}",
                    color = DimInk,
                    fontFamily = Hyperlegible,
                    fontSize = 26.sp,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = key.role,
                    color = if (key.accepted) CellLit else DimInk,
                    fontFamily = Hyperlegible,
                    fontSize = 26.sp,
                    modifier = Modifier.weight(3f)
                )
                Text(
                    text = key.sinceLastMs?.let { "+$it ms" } ?: "",
                    color = DimInk,
                    fontFamily = Hyperlegible,
                    fontSize = 26.sp,
                    modifier = Modifier.weight(2f)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        TouchButton(text = controller.language.settingsClose, onTap = controller::closeSettings)
    }
}

/**
 * Everything that is not the arrow pad: the grid she reads, the touch halves
 * when scanning, and the battery and gear in the corner.
 *
 * Pulled out because the pad can sit on three different sides of it, and this
 * part is identical in all three. It has to be inside a Box, since the corner
 * readouts are aligned against it.
 */
@Composable
private fun BoxScope.GridContent(controller: ScanController, arrows: Boolean) {
    if (controller.inPhrases) {
        PhrasesGrid(controller)
    } else {
        WritingGrid(controller)
    }

    // The two touch halves belong to scanning alone. In arrow mode the pad is
    // the input, and in direct mode the letters are — in both cases a half
    // screen listening for taps would swallow them before they arrived.
    if (controller.touchInput && controller.inputMode == InputMode.Scan) {
        TouchZones(
            onWrite = controller::press,
            onUndo = controller::undo,
            debounceMs = controller.debounceMs,
            antiTremor = controller.antiTremor,
        )
    }

    Row(
        modifier = Modifier.align(Alignment.TopEnd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BatteryReadout(controller)
        // Settings is reached from the writing grid only, as before; the
        // phrases screen keeps its corner clear for the phrase in it.
        if (!controller.inPhrases) {
            SettingsCornerButton(onOpen = controller::openSettings)
        }
    }
}

/**
 * The walkthrough, for whoever is setting the app up.
 *
 * This exists because of a failure that had nothing to do with the code. The
 * app was taken to her by somebody who had only had it described to them, and
 * who therefore explained it wrong; it was judged not to work, and what had
 * actually not worked was the explanation. A tool that only one person
 * understands stops working the first day that person is not in the room.
 *
 * So it is written for the helper, not for her, and it says out loud the things
 * that are obvious only once somebody has seen them happen: that a row lets go
 * of itself, that the arrows wrap, that a missed letter is not a mistake to be
 * fixed but a moment to be waited out.
 *
 * Laid out across rather than down: the tablet is held sideways, so words on
 * one side and a moving picture on the other beats a column of text with the
 * buttons pushed off the bottom.
 */
@Composable
private fun TutorialPanel(controller: ScanController) {
    val pages = controller.tutorialPages
    val page = pages.getOrNull(controller.tutorialPage) ?: return
    val last = controller.tutorialPage == pages.lastIndex

    Column(modifier = Modifier.fillMaxSize().padding(40.dp)) {
        Text(
            text = "${controller.tutorialPage + 1} / ${pages.size}",
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = page.title,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )

        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    // Scrolls rather than shrinks, so a long page still works
                    // on a short screen and the buttons below stay put.
                    .verticalScroll(rememberScrollState())
            ) {
                for (line in page.lines) {
                    Row(modifier = Modifier.padding(bottom = 14.dp)) {
                        Text(
                            text = "\u2022",
                            color = CellLit,
                            fontFamily = Hyperlegible,
                            fontSize = 24.sp,
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = line,
                            color = Ink,
                            fontFamily = Hyperlegible,
                            fontSize = 24.sp,
                            lineHeight = 32.sp
                        )
                    }
                }
            }

            page.demo?.let { demo ->
                Spacer(Modifier.width(28.dp))
                Column(
                    modifier = Modifier.width(300.dp).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    // Beside the words rather than above them: the text runs
                    // to a different length on every page, and a picture that
                    // slides up and down the screen as you page through is
                    // harder to keep hold of than one that stays put.
                    verticalArrangement = Arrangement.Center,
                ) {
                    val active = when (demo) {
                        TutorialDemo.Scan -> controller.inputMode == InputMode.Scan
                        TutorialDemo.Arrows -> controller.inputMode == InputMode.Arrows
                        // Not a mode, so there is no mode to be current.
                        TutorialDemo.Touch -> false
                    }
                    if (active) {
                        // Which of the two is switched on right now is the
                        // single most useful thing on this page, and the one a
                        // helper is most likely to get wrong.
                        Text(
                            text = controller.language.tutorialActiveMode,
                            color = CellLit,
                            fontFamily = Hyperlegible,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                    TutorialPicture(demo, controller.language)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (controller.tutorialPage > 0) {
                TouchButton(
                    text = controller.language.tutorialBack,
                    onTap = controller::previousTutorialPage,
                )
                Spacer(Modifier.width(20.dp))
            }
            TouchButton(
                text = if (last) {
                    controller.language.settingsClose
                } else {
                    controller.language.tutorialNext
                },
                onTap = controller::nextTutorialPage,
            )
            Spacer(Modifier.weight(1f))
            if (!last) {
                TouchButton(
                    text = controller.language.settingsClose,
                    onTap = controller::closeTutorial,
                )
            }
        }
    }
}

/** Picks the right moving picture for a page. */
@Composable
private fun TutorialPicture(demo: TutorialDemo, language: Language) {
    when (demo) {
        TutorialDemo.Touch -> TutorialTouchDemo(language)
        TutorialDemo.Scan, TutorialDemo.Arrows -> TutorialDemoGrid(demo, language)
    }
}

/**
 * The two invisible halves of the screen, lighting up in turn over a grid.
 *
 * The single most common misunderstanding this app produces. A grid of letters
 * looks exactly like a grid of buttons, so a helper taps the letter they want,
 * nothing happens, and they reasonably conclude the thing is broken. It is not
 * a fault they can be talked out of in words — "the letters are not buttons"
 * describes an absence, and an absence is hard to picture.
 *
 * So it is drawn: the grid underneath, and one enormous half-screen button
 * washing over it. The point being made by the picture is the one that matters
 * most to her, which is that there is nothing to aim at and she cannot miss.
 */
@Composable
private fun TutorialTouchDemo(language: Language) {
    val frames = remember {
        listOf(
            TouchFrame(left = false, right = false, holdMs = 500),
            TouchFrame(left = false, right = true, holdMs = 1500),
            TouchFrame(left = false, right = false, holdMs = 500),
            TouchFrame(left = true, right = false, holdMs = 1500),
        )
    }
    var index by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(frames[index].holdMs)
            index = (index + 1) % frames.size
        }
    }
    val frame = frames[index]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .background(TextAreaBackground, RoundedCornerShape(10.dp))
            .padding(6.dp)
    ) {
        // The grid, underneath and untouchable, exactly as it is in the app.
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(DEMO_ROWS + 1) {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(DEMO_COLS + 1) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(CellIdle, RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        // The two halves, over the top of all of it.
        Row(modifier = Modifier.fillMaxSize()) {
            TouchHalf(
                label = language.settingsUndo.uppercase(),
                lit = frame.left,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
            TouchHalf(
                label = language.settingsWrite.uppercase(),
                lit = frame.right,
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun TouchHalf(label: String, lit: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(
            // Translucent rather than solid, so the letters stay visible
            // underneath while it is lit. That the button is *over* the grid
            // rather than made of it is the whole point of the picture.
            color = if (lit) CellLit.copy(alpha = 0.8f) else Color.Transparent,
            shape = RoundedCornerShape(6.dp),
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (lit) Ink else DimInk,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )
    }
}

private class TouchFrame(val left: Boolean, val right: Boolean, val holdMs: Long)

/**
 * A small grid that acts out one way of choosing a letter, over and over.
 *
 * Words are a poor way to describe a rhythm. "The highlight moves down the rows
 * and you press" is a sentence somebody can read, agree with, and still not be
 * able to picture — and picturing it is the whole of what a helper has to pass
 * on. So the page shows it happening instead, on a grid small enough to sit
 * beside the text.
 */
@Composable
private fun TutorialDemoGrid(demo: TutorialDemo, language: Language) {
    // row, col (null lights the whole row), the cue to show, how long to hold.
    val frames = remember(demo, language) {
        if (demo == TutorialDemo.Arrows) {
            listOf(
                DemoFrame(0, 0, null, 800),
                DemoFrame(1, 0, "\u25bc", 700),
                DemoFrame(1, 1, "\u25b6", 700),
                DemoFrame(1, 2, "\u25b6", 700),
                DemoFrame(1, 2, language.arrowChoose, 900),
            )
        } else {
            listOf(
                DemoFrame(0, null, null, 800),
                DemoFrame(1, null, null, 800),
                DemoFrame(1, null, language.tutorialPressed, 600),
                DemoFrame(1, 0, null, 700),
                DemoFrame(1, 1, null, 700),
                DemoFrame(1, 2, null, 700),
                DemoFrame(1, 2, language.tutorialPressed, 800),
                DemoFrame(0, null, null, 700),
            )
        }
    }

    var index by remember(frames) { mutableStateOf(0) }
    LaunchedEffect(frames) {
        while (true) {
            delay(frames[index].holdMs)
            index = (index + 1) % frames.size
        }
    }
    val frame = frames[index]

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (row in 0 until DEMO_ROWS) {
            Row {
                for (col in 0 until DEMO_COLS) {
                    val lit = frame.row == row && (frame.col == null || frame.col == col)
                    Box(
                        modifier = Modifier
                            .padding(3.dp)
                            .size(width = 62.dp, height = 42.dp)
                            .background(
                                color = if (lit) CellLit else CellIdle,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // A fixed height whether or not there is a cue, so the grid above does
        // not jump up and down as the loop goes round.
        Box(modifier = Modifier.height(44.dp), contentAlignment = Alignment.Center) {
            frame.cue?.let { cue ->
                Text(
                    text = cue,
                    color = Ink,
                    fontFamily = Hyperlegible,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .background(ArrowChoose, RoundedCornerShape(10.dp))
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}

private class DemoFrame(
    val row: Int,
    /** Null lights the whole row, as row scanning does. */
    val col: Int?,
    val cue: String?,
    val holdMs: Long,
)

/** Big enough to show a row being entered, small enough to sit beside text. */
private const val DEMO_ROWS = 3
private const val DEMO_COLS = 4

/**
 * The four arrows and the choose button, down one side of the screen.
 *
 * The other way of using the app. Scanning asks her to wait for the highlight
 * to arrive and to press at exactly the right moment; this asks for more
 * presses but no timing at all, and nothing is ever missed by a moment. Which
 * of the two suits a person is not something anyone can work out in advance,
 * which is why both are here.
 *
 * The choose button is the largest, because it is pressed once per letter
 * while each arrow is pressed less often, and because a missed choose is the
 * one that costs a letter.
 *
 * Deliberately no press-and-hold repeat. Both wraps mean nothing on the grid is
 * more than three presses away in either direction, so a repeat would buy very
 * little, and a hand that rests too long on a button would pay for it.
 */
@Composable
private fun ArrowPad(
    controller: ScanController,
    placement: ArrowPlacement,
    modifier: Modifier = Modifier,
) {

    // One filter per button rather than one for the pad. A tremor repeating the
    // same arrow has to be swallowed, but moving and then choosing is two real
    // presses in quick succession and has to keep working.
    val filters = remember(controller.debounceMs, controller.antiTremor) {
        List(ARROW_BUTTONS) {
            SwitchFilter(
                debounceMs = controller.debounceMs,
                restartOnReject = controller.antiTremor,
            )
        }
    }

    /** Runs an arrow, but only if it is a real press and not a tremor. */
    fun guarded(index: Int, move: () -> Unit): () -> Unit = {
        // The same window as the switches, so the tremor setting means one
        // thing everywhere rather than two.
        if (filters[index].accept(Switch.Write, SystemClock.elapsedRealtime())) move()
    }

    val cross: @Composable (Modifier) -> Unit = { crossModifier ->
        ArrowCross(
            shape = controller.arrowShape,
            modifier = crossModifier,
            onUp = guarded(0, controller::moveUp),
            onLeft = guarded(1, controller::moveLeft),
            onRight = guarded(2, controller::moveRight),
            onDown = guarded(3, controller::moveDown),
        )
    }

    val choose: @Composable (Modifier) -> Unit = { chooseModifier ->
        ArrowKey(
            label = controller.language.arrowChoose,
            filter = filters[4],
            modifier = chooseModifier,
            onPress = controller::press,
            // A lighter fill, so the one button that commits a letter does not
            // read as a fifth arrow.
            colour = ArrowChoose,
        )
    }

    val eraseLetter: @Composable (Modifier) -> Unit = { keyModifier ->
        ArrowKey(
            label = controller.language.arrowDeleteLetter,
            filter = filters[5],
            modifier = keyModifier,
            onPress = controller::eraseLetter,
        )
    }

    // Turns red and changes what it says once it is armed. The colour is the
    // part that carries: whoever is watching sees at a glance that the app is
    // waiting for something, without having to read at speed.
    val eraseAll: @Composable (Modifier) -> Unit = { keyModifier ->
        ArrowKey(
            label = if (controller.clearArmed) {
                controller.language.arrowClearConfirm
            } else {
                controller.language.clearLabel
            },
            filter = filters[6],
            modifier = keyModifier,
            onPress = controller::eraseAll,
            colour = if (controller.clearArmed) ArrowArmed else ArrowKeyColour,
        )
    }

    val erasing = controller.eraseKeys

    if (placement.alongTheBottom) {
        // More room around the edges than the side column needs. The cross
        // fills its square exactly, so with the strip pressed against the
        // bottom of the screen the down arrow had its point shaved off.
        Row(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            // The arrows take every pixel the strip's height allows. They
            // have to stay congruent, so they are bounded by a square, and a
            // square in a horizontal strip can never be taller than the strip:
            // making them bigger means making the strip taller, and the grid
            // pays for it. See ARROW_BAR_WEIGHT.
            val arrowsBox = Modifier.fillMaxHeight().aspectRatio(1f)

            // Choose does not take all the room left over. It was a slab half
            // the width of the screen, which is far more than a button needs in
            // order to be easy to hit, and every pixel of it was a pixel a
            // forearm could land on.
            val chooseBox = Modifier.weight(1f).fillMaxHeight()

            // The gap between them is empty, and deliberately the largest
            // single thing here. It is what keeps a hand reaching for one end
            // from arriving at the other, which is the whole reason for putting
            // the pad down here at all.
            // The two erase keys come out of choose's share and out of the
            // gap, in that order. Clear-all goes at the far end, with the edge
            // of the screen on one side of it and choose on the other, so the
            // one button that can undo a whole sentence is the furthest thing
            // in the strip from the arrows her hand lives on. Backspace goes
            // between choose and the arrows instead: it is the one she reaches
            // for most, and the worst a stray press can cost is one letter.
            val eraseBox = Modifier.weight(ERASE_WEIGHT).fillMaxHeight()
            val sharedChooseBox = Modifier.weight(CHOOSE_BAR_WEIGHT).fillMaxHeight()

            if (controller.chooseFirst) {
                if (erasing) {
                    eraseAll(eraseBox)
                    choose(sharedChooseBox)
                    eraseLetter(eraseBox)
                    Spacer(Modifier.weight(ARROW_GAP_WEIGHT))
                } else {
                    choose(chooseBox)
                    Spacer(Modifier.weight(1.5f))
                }
                cross(arrowsBox)
            } else {
                cross(arrowsBox)
                if (erasing) {
                    Spacer(Modifier.weight(ARROW_GAP_WEIGHT))
                    eraseLetter(eraseBox)
                    choose(sharedChooseBox)
                    eraseAll(eraseBox)
                } else {
                    Spacer(Modifier.weight(1.5f))
                    choose(chooseBox)
                }
            }
        }
    } else {
        BoxWithConstraints(modifier = modifier.padding(8.dp)) {
            // The arrows are measured first and everything else divides up what
            // is left, rather than the other way round.
            //
            // They have to stay congruent, which bounds them by a square, and a
            // square in a column wants to be as wide as the column. Sizing them
            // by weight meant they were bounded by their share of the *height*
            // instead, so on a phone they came out a third narrower than the
            // column they sat in and the rest of that width was simply empty —
            // which is how they ended up too small to aim at comfortably.
            val crossSide = minOf(maxWidth, maxHeight * CROSS_MAX_HEIGHT_SHARE)
            val leftOver = maxHeight - crossSide
            val eraseHeight = if (erasing) leftOver * ERASE_SHARE_OF_LEFTOVER else 0.dp

            Column(modifier = Modifier.fillMaxSize()) {
                // Side by side, on the far side of choose from the arrows, for
                // the same reason clear-all sits at the end of the strip: the
                // two buttons her hand is on all day should have the least
                // reachable thing be the one that erases the most.
                val eraseRow: @Composable () -> Unit = {
                    Row(modifier = Modifier.fillMaxWidth().height(eraseHeight)) {
                        eraseAll(Modifier.weight(1f).fillMaxHeight())
                        eraseLetter(Modifier.weight(1f).fillMaxHeight())
                    }
                }

                // Choose takes whatever the arrows and the erase pair leave.
                // It is a word in a box rather than something aimed at, so it
                // is the one that can afford to give room away.
                val chooseBox = Modifier.fillMaxWidth().weight(1f)
                val crossBox = Modifier.size(crossSide).align(Alignment.CenterHorizontally)

                // Choose above or below the arrows, for the same reason it sits
                // at one end or the other of the strip: whichever of the two
                // the forearm sweeps over on the way in is the one that should
                // not be in the way.
                if (controller.chooseFirst) {
                    if (erasing) eraseRow()
                    choose(chooseBox)
                    cross(crossBox)
                } else {
                    cross(crossBox)
                    choose(chooseBox)
                    if (erasing) eraseRow()
                }
            }
        }
    }
}

/**
 * The four arrows, as one square of four triangles around a middle.
 *
 * Four congruent triangles, each pointing the way it moves the highlight. Every
 * measurement comes off the same `side`, so no arrow is bigger or better placed
 * than another — with an up that is easier to hit than a left, the grid stops
 * being neutral and she pays for it on every letter.
 *
 * **The whole square is live, and the gaps belong to the nearest arrow.** What
 * is drawn is four triangles; what is *listened to* is four quarters of a
 * square, split along its diagonals. So a tap that lands short of a triangle,
 * or in the space between two, still does the obvious thing rather than
 * nothing. For a hand that cannot be placed precisely, a pad with no dead space
 * anywhere in it is worth more than tidy edges.
 */
@Composable
private fun ArrowCross(
    shape: ArrowShape,
    modifier: Modifier = Modifier,
    onUp: () -> Unit,
    onLeft: () -> Unit,
    onRight: () -> Unit,
    onDown: () -> Unit,
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        // A centred square, and the listening happens inside it and nowhere
        // else. This matters more than it looks: the quarters are cut by the
        // diagonals, so in a box wider than it is tall the left and right
        // quarters would swallow almost everything and up and down would be
        // reduced to two thin wedges. Along the bottom of the screen, that
        // would have made up and down nearly unhittable.
        val side = minOf(maxWidth, maxHeight)

        Box(
            modifier = Modifier
                .size(side)
                .pointerInput(onUp, onLeft, onRight, onDown) {
                    detectTapGestures { tap ->
                        val dx = tap.x - size.width / 2f
                        val dy = tap.y - size.height / 2f
                        // Which side of the two diagonals the tap fell on, and
                        // nothing more. Every point in the square belongs to
                        // exactly one arrow. This is the same whichever way the
                        // arrows are drawn, so changing their look can never
                        // quietly open a hole in the middle of the pad.
                        if (abs(dx) > abs(dy)) {
                            if (dx > 0) onRight() else onLeft()
                        } else {
                            if (dy > 0) onDown() else onUp()
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val square = min(size.width, size.height)
                val cx = size.width / 2f
                val cy = size.height / 2f

                // Joined draws the triangles themselves as the buttons, so
                // they are the pale-on-black shapes. Separate draws a button
                // and then an arrow on top of it, which has to be lighter than
                // what it sits on or it is not there at all.
                val ink = when (shape) {
                    ArrowShape.Joined -> ArrowKeyColour
                    ArrowShape.Separate -> ArrowGlyph
                }

                /** One arrow: its tip, and the two ends of the base behind it. */
                fun arrow(
                    tipX: Float, tipY: Float,
                    leftX: Float, leftY: Float,
                    rightX: Float, rightY: Float,
                    rounding: Float,
                ) {
                    val path = Path().apply {
                        moveTo(tipX, tipY)
                        lineTo(leftX, leftY)
                        lineTo(rightX, rightY)
                        close()
                    }
                    drawPath(path, ink)
                    // Fill plus a rounded outline, so the points are blunt
                    // rather than needle sharp. Purely how it looks; the tap
                    // area is the whole quarter regardless.
                    drawPath(
                        path = path,
                        color = ink,
                        style = Stroke(width = rounding, join = StrokeJoin.Round),
                    )
                }

                when (shape) {
                    ArrowShape.Joined -> {
                        val arm = square / 2f
                        val hub = square * ARROW_HUB
                        val half = square * ARROW_BASE_HALF
                        val rounding = square * ARROW_ROUNDING

                        // Up and down, then left and right. Each is the same
                        // triangle, turned: arm out to the tip, hub back from
                        // the middle, half across the base.
                        arrow(cx, cy - arm, cx - half, cy - hub, cx + half, cy - hub, rounding)
                        arrow(cx, cy + arm, cx - half, cy + hub, cx + half, cy + hub, rounding)
                        arrow(cx - arm, cy, cx - hub, cy - half, cx - hub, cy + half, rounding)
                        arrow(cx + arm, cy, cx + hub, cy - half, cx + hub, cy + half, rounding)
                    }

                    ArrowShape.Separate -> {
                        // A plus laid over a three by three: up on the top
                        // middle, down on the bottom middle, left and right on
                        // the sides. The four corners are left empty on
                        // purpose. There is no honest way to label a corner —
                        // the top left is exactly as much "up" as it is "left"
                        // — so nothing is drawn there and it keeps belonging to
                        // whichever arrow it is nearer.
                        val cell = square * ARROW_CELL
                        val button = cell * ARROW_BUTTON_FILL
                        val radius = CornerRadius(button * 0.18f)
                        val glyph = button * ARROW_GLYPH
                        val rounding = glyph * 0.06f

                        /** One arrow's button, centred on [bx], [by]. */
                        fun key(bx: Float, by: Float, direction: Int) {
                            drawRoundRect(
                                color = ArrowKeyColour,
                                topLeft = Offset(bx - button / 2f, by - button / 2f),
                                size = Size(button, button),
                                cornerRadius = radius,
                            )
                            val reach = glyph / 2f
                            // Squatter than it is wide, which is what an arrow
                            // head looks like; a triangle as tall as its base
                            // reads as a wedge rather than a direction.
                            val base = glyph * 0.46f
                            when (direction) {
                                0 -> arrow(bx, by - reach, bx - reach, by + base, bx + reach, by + base, rounding)
                                1 -> arrow(bx, by + reach, bx - reach, by - base, bx + reach, by - base, rounding)
                                2 -> arrow(bx - reach, by, bx + base, by - reach, bx + base, by + reach, rounding)
                                else -> arrow(bx + reach, by, bx - base, by - reach, bx - base, by + reach, rounding)
                            }
                        }

                        key(cx, cy - cell, 0)
                        key(cx, cy + cell, 1)
                        key(cx - cell, cy, 2)
                        key(cx + cell, cy, 3)
                    }
                }
            }
        }
    }
}

/**
 * One button of the arrow pad.
 *
 * Not a Compose Button and never focusable, for the same reason the rest of
 * this screen is not: a focusable control can be pressed by a space bar, and a
 * switch sending a space would then work the pad instead of the grid.
 */
@Composable
private fun ArrowKey(
    label: String,
    filter: SwitchFilter,
    modifier: Modifier = Modifier,
    onPress: () -> Unit,
    colour: Color = ArrowKeyColour,
) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .background(color = colour, shape = RoundedCornerShape(14.dp))
            .pointerInput(filter) {
                detectTapGestures {
                    // The same window as the switches, so the tremor setting
                    // means one thing everywhere rather than two.
                    if (filter.accept(Switch.Write, SystemClock.elapsedRealtime())) onPress()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        BoxWithConstraints {
            // Sized from the button, like the letters are, so the pad works on
            // a ten inch tablet and on a phone held sideways.
            val size = minOf(
                maxHeight.value * 0.5f,
                // The 0.62 is the average width of a character in this font as
                // a fraction of its size; the 0.9 is the margin either side, so
                // ESBORRA TOT stops touching the corners of its own button.
                (maxWidth.value * 0.9f) / (label.length.coerceAtLeast(1) * 0.68f),
                ARROW_MAX_TEXT_SP,
            )
            Text(
                text = label,
                color = Ink,
                fontFamily = Hyperlegible,
                fontWeight = FontWeight.Bold,
                fontSize = size.sp,
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
            )
        }
    }
}

/**
 * The battery charge, small, in the corner of the grid.
 *
 * On her own tablet the system bars are hidden and locked mode stops anyone
 * leaving the app, so without this there is genuinely no way to find out how
 * much charge is left short of unlocking the whole thing. A device that goes
 * flat unannounced is a person who cannot say so.
 *
 * Stays grey and quiet until the charge is actually low, and then turns red, so
 * it is ignorable for most of the day and hard to miss when it matters.
 */
@Composable
private fun BatteryReadout(controller: ScanController) {
    val percent = controller.batteryPercent ?: return
    val low = percent <= LOW_BATTERY_PERCENT && !controller.batteryCharging

    Text(
        // The bolt says charging without needing a word for it in every
        // language the app grows into.
        text = if (controller.batteryCharging) "\u26a1 $percent%" else "$percent%",
        color = if (low) LowBattery else SettingsCornerInk,
        fontFamily = Hyperlegible,
        fontWeight = if (low) FontWeight.Bold else FontWeight.Normal,
        fontSize = 20.sp,
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
    )
}

/**
 * Two invisible tap targets over the grid, so the screen itself can stand in
 * for the two switches while there is no switch box: right half writes, left
 * half undoes. The halves match the switches, not the reading order, because
 * the writing switch is the one used far more often and the right is the
 * easier reach for most people.
 *
 * Transparent on purpose. The grid is what she reads; a visible split would
 * only add clutter and imply the letters themselves are buttons, which they
 * are not.
 */
@Composable
private fun TouchZones(
    onWrite: () -> Unit,
    onUndo: () -> Unit,
    debounceMs: Long,
    antiTremor: Boolean,
) {
    // The same debounce as the physical switches, so a tremor that taps twice
    // counts once here too. Rebuilt when either setting changes. Without this,
    // the touch path skipped the filter entirely and every double tap counted.
    val filter = remember(debounceMs, antiTremor) {
        SwitchFilter(debounceMs = debounceMs, restartOnReject = antiTremor)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (filter.accept(Switch.Undo, SystemClock.elapsedRealtime())) onUndo()
                    }
                }
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectTapGestures {
                        if (filter.accept(Switch.Write, SystemClock.elapsedRealtime())) onWrite()
                    }
                }
        )
    }
}

/**
 * A drop-down of the languages, rather than a row of buttons: the list is meant
 * to grow, and a row of buttons stops fitting once there are more than a few.
 * The open list scrolls, so it works with many languages on a short screen.
 */
@Composable
private fun LanguageChooser(current: Language, onChoose: (Language) -> Unit) {
    var open by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .background(CellIdle, RoundedCornerShape(12.dp))
                .pointerInput(Unit) { detectTapGestures { open = true } }
                .padding(horizontal = 28.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = current.displayName,
                color = Ink,
                fontFamily = Hyperlegible,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
            Spacer(Modifier.width(14.dp))
            Text(text = "▾", color = DimInk, fontSize = 24.sp)
        }

        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
            modifier = Modifier
                .background(CellIdle)
                // Tall lists stay usable on a phone held sideways.
                .heightIn(max = 320.dp)
        ) {
            for (language in LANGUAGES) {
                val selected = language.code == current.code
                DropdownMenuItem(
                    onClick = {
                        open = false
                        onChoose(language)
                    },
                    text = {
                        Text(
                            text = language.displayName,
                            color = if (selected) CellLit else Ink,
                            fontFamily = Hyperlegible,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 26.sp
                        )
                    },
                )
            }
        }
    }
}

/** A titled on/off row, as used by every switch in settings. */
@Composable
private fun SettingSwitch(
    title: String,
    detail: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Ink, fontFamily = Hyperlegible, fontSize = 30.sp)
            Text(text = detail, color = DimInk, fontFamily = Hyperlegible, fontSize = 20.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Ink,
                checkedTrackColor = CellLit,
                uncheckedTrackColor = CellIdle,
            ),
        )
    }
}

/**
 * One of the two input modes, as a pill. Two buttons rather than a switch,
 * because these are two named ways of working rather than one thing being on
 * or off, and a helper should be able to see which one they are choosing.
 */
@Composable
private fun ModeOption(text: String, selected: Boolean, onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) CellLit else CellIdle,
                shape = RoundedCornerShape(12.dp)
            )
            .pointerInput(Unit) { detectTapGestures { onTap() } }
            .padding(horizontal = 32.dp, vertical = 18.dp)
    ) {
        Text(
            text = text,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 26.sp
        )
    }
}

/** A plain touch button. Never focusable, so no switch can ever press it. */
@Composable
private fun TouchButton(text: String, onTap: () -> Unit) {
    Box(
        modifier = Modifier
            .background(CellIdle, RoundedCornerShape(12.dp))
            .pointerInput(Unit) { detectTapGestures { onTap() } }
            .padding(horizontal = 48.dp, vertical = 20.dp)
    ) {
        Text(
            text = text,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Composable
private fun WritingGrid(controller: ScanController) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

        // The text box gets close to two rows' worth of height, so a sentence
        // is easy to read at a glance. It is the thing being said, so it earns
        // the room.
        ComposedText(
            text = controller.text,
            bold = controller.boldWriting,
            modifier = Modifier.fillMaxWidth().weight(1.9f)
        )

        // Every row gets the same number of slots so the columns line up even
        // when a row is short.
        val columns = controller.rows.maxOf { it.size }

        val direct = controller.inputMode == InputMode.Direct

        for ((rowIndex, row) in controller.rows.withIndex()) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                for ((colIndex, key) in row.withIndex()) {
                    KeyCell(
                        label = controller.label(key),
                        lit = controller.isLit(rowIndex, colIndex),
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onTap = if (direct) {
                            { controller.touchKey(rowIndex, colIndex) }
                        } else {
                            null
                        },
                        debounceMs = controller.debounceMs,
                        antiTremor = controller.antiTremor,
                    )
                }
                if (row.size < columns) {
                    Spacer(modifier = Modifier.weight((columns - row.size).toFloat()))
                }
            }
        }
    }
}

/**
 * The speed control, for a carer's finger rather than for the switch.
 *
 * Scanning this would be absurd: it is touched a handful of times by someone
 * with working hands, and every control the grid has to carry costs her time on
 * every letter for the rest of the day.
 */
@Composable
private fun SettingsPanel(controller: ScanController) {

    // The slider drives a local value and only commits when the finger lifts.
    //
    // Feeding it straight into the controller was a real bug: Slider emits a
    // change while it is being laid out, before anyone has touched it, so
    // merely opening this screen silently rewrote her saved speed by 1.7
    // seconds. Committing on release means an event nobody caused cannot
    // change anything. Re-read each time the panel opens, since it is only
    // composed while settings are showing.
    var chosenMs by remember { mutableFloatStateOf(controller.scanIntervalMs.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            // Scrolls, so this still works on a tablet shorter than the one it
            // was laid out on.
            .verticalScroll(rememberScrollState())
            .padding(48.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // First, and full width, because a helper who has opened settings
        // without knowing what the app does needs this before anything else
        // here will mean much to them.
        TouchButton(
            text = controller.language.settingsTutorial,
            onTap = controller::openTutorial,
        )

        Spacer(Modifier.height(32.dp))

        // Scanning or arrows. The most consequential setting on this screen,
        // so it comes first: everything below it is a detail of whichever of
        // the two is chosen.
        Text(
            text = controller.language.settingsModeTitle,
            color = Ink,
            fontFamily = Hyperlegible,
            fontSize = 30.sp
        )
        Text(
            text = controller.language.settingsModeDetail,
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(14.dp))
        Row {
            for (mode in InputMode.entries) {
                ModeOption(
                    text = when (mode) {
                        InputMode.Scan -> controller.language.settingsModeScan
                        InputMode.Arrows -> controller.language.settingsModeArrows
                        InputMode.Direct -> controller.language.settingsModeDirect
                    },
                    selected = controller.inputMode == mode,
                    onTap = { controller.useInputMode(mode) },
                )
                Spacer(Modifier.width(16.dp))
            }
        }

        Spacer(Modifier.height(32.dp))

        // These two are about reading and writing rather than about how she
        // drives the grid, so they sit above the per-mode settings and stay
        // put whichever mode is chosen.
        SettingSwitch(
            title = controller.language.settingsForgiveTitle,
            detail = controller.language.settingsForgiveDetail,
            checked = controller.forgiveMistakes,
            onChange = { controller.useForgiveMistakes(it) },
        )

        Spacer(Modifier.height(24.dp))

        SettingSwitch(
            title = controller.language.settingsBoldWritingTitle,
            detail = controller.language.settingsBoldWritingDetail,
            checked = controller.boldWriting,
            onChange = { controller.useBoldWriting(it) },
        )

        Spacer(Modifier.height(32.dp))

        if (controller.inputMode == InputMode.Arrows) {
            // Which hand still reaches, and where the forearm falls on the way
            // to the screen, are not things anyone chooses. So the pad moves
            // rather than her.
            Text(
                text = controller.language.settingsArrowPlaceTitle,
                color = Ink,
                fontFamily = Hyperlegible,
                fontSize = 30.sp
            )
            Text(
                text = controller.language.settingsArrowPlaceDetail,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(14.dp))

            // Two rows of two rather than one row of four: the choice is
            // really two questions, where the pad goes and which way round it
            // is, and four pills in a line reads as one flat list of options
            // that happen to share words.
            Row {
                for (placement in ArrowPlacement.entries) {
                    ModeOption(
                        text = when (placement) {
                            ArrowPlacement.Right -> controller.language.settingsArrowRight
                            ArrowPlacement.Left -> controller.language.settingsArrowLeft
                            ArrowPlacement.Bottom -> controller.language.settingsArrowBottom
                        },
                        selected = controller.arrowPlacement == placement,
                        onTap = { controller.useArrowPlacement(placement) },
                    )
                    Spacer(Modifier.width(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Which end choose sits at is its own question, and the same
            // question in both arrangements: top or bottom of a column, left or
            // right of a strip. So it is one switch rather than a separate
            // option for every combination.
            SettingSwitch(
                title = if (controller.arrowPlacement.alongTheBottom) {
                    controller.language.settingsChooseLeft
                } else {
                    controller.language.settingsChooseTop
                },
                detail = controller.language.settingsChooseDetail,
                checked = controller.chooseFirst,
                onChange = { controller.useChooseFirst(it) },
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = controller.language.settingsArrowShapeTitle,
                color = Ink,
                fontFamily = Hyperlegible,
                fontSize = 30.sp
            )
            Text(
                text = controller.language.settingsArrowShapeDetail,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 20.sp
            )
            Spacer(Modifier.height(14.dp))
            Row {
                for (shape in ArrowShape.entries) {
                    ModeOption(
                        text = when (shape) {
                            ArrowShape.Joined -> controller.language.settingsArrowShapeJoined
                            ArrowShape.Separate -> controller.language.settingsArrowShapeSeparate
                        },
                        selected = controller.arrowShape == shape,
                        onTap = { controller.useArrowShape(shape) },
                    )
                    Spacer(Modifier.width(16.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            SettingSwitch(
                title = controller.language.settingsEraseKeysTitle,
                detail = controller.language.settingsEraseKeysDetail,
                checked = controller.eraseKeys,
                onChange = { controller.useEraseKeys(it) },
            )
            Spacer(Modifier.height(32.dp))
        }

        // Both of these are about a highlight that moves on its own, so in
        // arrow mode they would be two sliders that visibly do nothing.
        if (controller.inputMode == InputMode.Scan) {
            Text(
                text = controller.language.settingsSpeed,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 28.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "${formatSeconds(chosenMs.roundToLong())} ${controller.language.settingsSecondsPerStep}",
                color = Ink,
                fontFamily = Hyperlegible,
                fontWeight = FontWeight.Bold,
                fontSize = 64.sp
            )

            Spacer(Modifier.height(40.dp))

            Slider(
                value = chosenMs,
                onValueChange = { chosenMs = it },
                onValueChangeFinished = { controller.changeInterval(chosenMs.roundToLong()) },
                valueRange = ScanController.MIN_INTERVAL_MS.toFloat()..
                    ScanController.MAX_INTERVAL_MS.toFloat(),
                // One stop per tenth of a second, so it cannot land somewhere odd.
                steps = ((ScanController.MAX_INTERVAL_MS - ScanController.MIN_INTERVAL_MS) /
                    ScanController.INTERVAL_STEP_MS).toInt() - 1,
                colors = SliderDefaults.colors(
                    thumbColor = CellLit,
                    activeTrackColor = CellLit,
                    inactiveTrackColor = CellIdle,
                ),
                modifier = Modifier.fillMaxWidth().height(64.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(controller.language.settingsFaster, color = DimInk, fontFamily = Hyperlegible, fontSize = 22.sp)
                Spacer(Modifier.weight(1f))
                Text(controller.language.settingsSlower, color = DimInk, fontFamily = Hyperlegible, fontSize = 22.sp)
            }

            Spacer(Modifier.height(44.dp))

            // Extra time on the first letter of each row, so entering a row and
            // reacting to its first letter are not crammed into one interval.
            var chosenFirst by remember { mutableFloatStateOf(controller.firstCellExtraMs.toFloat()) }

            Text(
                text = controller.language.settingsFirstLetterExtra + ": +${formatSeconds(chosenFirst.roundToLong())} s",
                color = Ink,
                fontFamily = Hyperlegible,
                fontSize = 30.sp
            )
            Slider(
                value = chosenFirst,
                onValueChange = { chosenFirst = it },
                onValueChangeFinished = { controller.changeFirstCellExtra(chosenFirst.roundToLong()) },
                valueRange = 0f..ScanController.MAX_FIRST_CELL_EXTRA_MS.toFloat(),
                steps = (ScanController.MAX_FIRST_CELL_EXTRA_MS /
                    ScanController.INTERVAL_STEP_MS).toInt() - 1,
                colors = SliderDefaults.colors(
                    thumbColor = CellLit,
                    activeTrackColor = CellLit,
                    inactiveTrackColor = CellIdle,
                ),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

        }

        Spacer(Modifier.height(44.dp))

        // How close together two presses of one switch may be. Raise it if a
        // single press is producing several letters.
        var chosenDebounce by remember { mutableFloatStateOf(controller.debounceMs.toFloat()) }

        Text(
            text = controller.language.settingsMinBetweenPresses + ": ${chosenDebounce.roundToLong()} ms",
            color = Ink,
            fontFamily = Hyperlegible,
            fontSize = 30.sp
        )
        Slider(
            value = chosenDebounce,
            onValueChange = { chosenDebounce = it },
            onValueChangeFinished = { controller.changeDebounce(chosenDebounce.roundToLong()) },
            valueRange = 0f..SwitchFilter.MAX_DEBOUNCE_MS.toFloat(),
            steps = (SwitchFilter.MAX_DEBOUNCE_MS / 25L).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = CellLit,
                activeTrackColor = CellLit,
                inactiveTrackColor = CellIdle,
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        )

        Spacer(Modifier.height(40.dp))

        // Turn touch input off once real switches arrive, so a stray touch on
        // the screen cannot type.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = controller.language.settingsTouchTitle,
                    color = Ink,
                    fontFamily = Hyperlegible,
                    fontSize = 30.sp
                )
                Text(
                    text = controller.language.settingsTouchDetail,
                    color = DimInk,
                    fontFamily = Hyperlegible,
                    fontSize = 20.sp
                )
            }
            Switch(
                checked = controller.touchInput,
                onCheckedChange = { controller.useTouchInput(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Ink,
                    checkedTrackColor = CellLit,
                    uncheckedTrackColor = CellIdle,
                ),
            )
        }

        Spacer(Modifier.height(28.dp))

        // Tremor mode: a burst of taps counts once.
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = controller.language.settingsTremorTitle,
                    color = Ink,
                    fontFamily = Hyperlegible,
                    fontSize = 30.sp
                )
                Text(
                    text = controller.language.settingsTremorDetail,
                    color = DimInk,
                    fontFamily = Hyperlegible,
                    fontSize = 20.sp
                )
            }
            Switch(
                checked = controller.antiTremor,
                onCheckedChange = { controller.useAntiTremor(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Ink,
                    checkedTrackColor = CellLit,
                    uncheckedTrackColor = CellIdle,
                ),
            )
        }

        Spacer(Modifier.height(32.dp))

        // Bind one or more controller / switch buttons to each action.
        Text(
            text = controller.language.settingsButtons,
            color = Ink,
            fontFamily = Hyperlegible,
            fontSize = 30.sp
        )
        Text(
            text = controller.language.settingsWrite + ": " + boundOrDefault(controller.writeButtonLabels),
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 20.sp
        )
        Text(
            text = controller.language.settingsUndo + ": " + boundOrDefault(controller.undoButtonLabels),
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(10.dp))
        Row {
            TouchButton(
                text = controller.language.settingsAssignWrite,
                onTap = { controller.startBinding(SwitchRole.Write) },
            )
            Spacer(Modifier.width(20.dp))
            TouchButton(
                text = controller.language.settingsAssignUndo,
                onTap = { controller.startBinding(SwitchRole.Undo) },
            )
        }

        Spacer(Modifier.height(28.dp))

        // Locked mode, and reopening after a restart. Both belong to her own
        // device; on a helper's phone they stay off and it remains a phone.
        SettingSwitch(
            title = controller.language.settingsLockedTitle,
            detail = controller.language.settingsLockedDetail,
            checked = controller.locked,
            onChange = { controller.useLocked(it) },
        )

        Spacer(Modifier.height(28.dp))

        SettingSwitch(
            title = controller.language.settingsBootTitle,
            detail = controller.language.settingsBootDetail,
            checked = controller.openOnBoot,
            onChange = { controller.useOpenOnBoot(it) },
        )

        Spacer(Modifier.height(32.dp))

        // Language. Changing it swaps the letters and their order, the words on
        // the keys, the prediction and the voice, all together.
        Text(
            text = controller.language.settingsLanguage,
            color = Ink,
            fontFamily = Hyperlegible,
            fontSize = 30.sp
        )
        Spacer(Modifier.height(10.dp))
        LanguageChooser(
            current = controller.language,
            onChoose = { controller.changeLanguage(it) },
        )

        Spacer(Modifier.height(40.dp))

        Row {
            TouchButton(text = controller.language.settingsClose, onTap = controller::closeSettings)
            Spacer(Modifier.width(20.dp))
            TouchButton(text = controller.language.settingsCheckButtons, onTap = controller::openDiagnostics)
        }

        Spacer(Modifier.height(20.dp))
        Text(
            // She is never stuck here even if nobody is holding the tablet.
            text = controller.language.settingsEitherCloses,
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 20.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            // Printed because there is no cable to ask the tablet with.
            text = "Comunicador ${BuildConfig.VERSION_NAME} " +
                "(build ${BuildConfig.VERSION_CODE})",
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 18.sp
        )
    }
}

private fun boundOrDefault(labels: List<String>): String =
    if (labels.isEmpty()) "—" else labels.joinToString(", ")

/**
 * Binds one or more buttons to an action. First it waits for a press (captured
 * in MainActivity), then it offers to add another, so several buttons can drive
 * the same action — all of X, Y, A, B, or both triggers, say.
 */
@Composable
private fun BindingPanel(controller: ScanController) {
    val waitingRole = controller.bindingRole
    val moreRole = controller.bindingMoreRole
    val role = waitingRole ?: moreRole
    val what = if (role == SwitchRole.Write) controller.language.bindWrite else controller.language.bindUndo

    Column(
        modifier = Modifier.fillMaxSize().padding(48.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (waitingRole != null) controller.language.bindPressFor else controller.language.bindButtonFor,
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 28.sp
        )
        Text(
            text = what,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 64.sp
        )

        if (controller.boundThisSession.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = controller.language.bindAssigned + ": " + controller.boundThisSession.joinToString(", "),
                color = CellLit,
                fontFamily = Hyperlegible,
                fontSize = 22.sp
            )
        }

        Spacer(Modifier.height(24.dp))

        if (waitingRole != null) {
            // Waiting for the press.
            Text(
                text = controller.language.bindPressNow,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(40.dp))
            TouchButton(text = controller.language.bindCancel, onTap = controller::cancelBinding)
        } else {
            // Just captured one; offer to add more or finish.
            Text(
                text = controller.language.bindAnother,
                color = DimInk,
                fontFamily = Hyperlegible,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(40.dp))
            Row {
                TouchButton(text = controller.language.bindAddAnother, onTap = controller::bindMore)
                Spacer(Modifier.width(20.dp))
                TouchButton(text = controller.language.bindDone, onTap = controller::finishBinding)
            }
        }
    }
}

/**
 * A small, dim touch target for a carer.
 *
 * It is not part of the grid and cannot be reached by either switch, so she can
 * never land in settings by mistake. It stays deliberately unobtrusive: it is
 * pressed a few times a month, and every pixel of attention it takes is taken
 * from the letters.
 *
 * Touch only, on purpose. The obvious `clickable` modifier makes an element
 * keyboard focusable, and a focused clickable in Compose treats space as a
 * click. That handed her switch a way into settings, which is the exact
 * accident this button being outside the grid was meant to rule out. Raw
 * pointer input takes focus out of the picture entirely.
 */
@Composable
private fun SettingsCornerButton(onOpen: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .size(44.dp)
            .background(SettingsCorner, RoundedCornerShape(8.dp))
            .pointerInput(Unit) { detectTapGestures(onTap = { onOpen() }) },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "⚙", color = SettingsCornerInk, fontSize = 22.sp)
    }
}

@Composable
private fun ComposedText(
    text: String,
    bold: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()

    // Always keep the newest words in view. A long sentence wraps onto more
    // lines than the box can show, so the end is what matters; the beginning
    // scrolls up out of sight. Keyed on maxValue as well as the text, so once
    // the new line has been laid out we scroll to the true bottom rather than
    // wherever the old bottom was.
    LaunchedEffect(text, scroll.maxValue) { scroll.scrollTo(scroll.maxValue) }

    Box(
        modifier = modifier
            .padding(4.dp)
            .background(TextAreaBackground, RoundedCornerShape(10.dp))
            .padding(horizontal = 24.dp, vertical = 12.dp),
        // A short sentence sits centred in the box; a long one fills it and
        // scrolls, so it never floats awkwardly at the top of a tall panel.
        contentAlignment = Alignment.CenterStart
    ) {
        // The reader can drag this to scroll back through everything she has
        // written. On her own device the switches do the writing, so a touch
        // here only ever scrolls; it never types.
        Column(modifier = Modifier.fillMaxWidth().verticalScroll(scroll)) {
            Text(
                // A trailing bar so the end of the sentence is visible, and so
                // a typed space is not an invisible event.
                text = "$text|",
                color = Ink,
                fontFamily = Hyperlegible,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
                fontSize = 48.sp,
                lineHeight = 58.sp
            )
        }
    }
}

/**
 * The phrases screen: no text box, just the scannable grid of whole phrases,
 * with a full-width TORNA across the top to leave. Selecting a phrase speaks it.
 */
@Composable
private fun PhrasesGrid(controller: ScanController) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        val direct = controller.inputMode == InputMode.Direct

        for ((rowIndex, row) in controller.rows.withIndex()) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                for ((colIndex, key) in row.withIndex()) {
                    PhraseCell(
                        label = controller.label(key),
                        lit = controller.isLit(rowIndex, colIndex),
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onTap = if (direct) {
                            { controller.touchKey(rowIndex, colIndex) }
                        } else {
                            null
                        },
                        debounceMs = controller.debounceMs,
                        antiTremor = controller.antiTremor,
                    )
                }
            }
        }
    }
}

@Composable
private fun PhraseCell(
    label: String,
    lit: Boolean,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null,
    debounceMs: Long = 0L,
    antiTremor: Boolean = false,
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(5.dp)
            .background(
                color = if (lit) CellLit else CellIdle,
                shape = RoundedCornerShape(10.dp)
            )
            .then(tapGuard(onTap, debounceMs, antiTremor))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Whole sentences, so they wrap and are sized from the cell rather than
        // squeezed onto one line. Capped so a short phrase is not enormous.
        val size = (maxHeight.value * 0.24f).coerceIn(15f, 30f)
        Text(
            text = label,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = size.sp,
            lineHeight = (size * 1.2f).sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun KeyCell(
    label: String,
    lit: Boolean,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null,
    debounceMs: Long = 0L,
    antiTremor: Boolean = false,
) {
    BoxWithConstraints(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = if (lit) CellLit else CellIdle,
                shape = RoundedCornerShape(10.dp)
            )
            .then(tapGuard(onTap, debounceMs, antiTremor)),
        contentAlignment = Alignment.Center
    ) {
        // Sized from the cell it is sitting in rather than from a number picked
        // against one screen. The same six rows have to fit a ten inch tablet
        // and a phone held sideways, where each row is less than half as tall;
        // fixed sizes simply cropped the letters on the smaller one.
        //
        // Measured in dp rather than sp on purpose. A large system font-scale
        // setting would otherwise push the letters back out of their cells, and
        // a letter she cannot see is worse than one that ignores that setting.
        val fitHeight = maxHeight.value * if (label.length > 1) 0.34f else 0.52f
        val fitWidth = maxWidth.value / (label.length.coerceAtLeast(1) * 0.66f)
        val size = minOf(fitHeight, fitWidth, MAX_KEY_TEXT_SP)

        Text(
            text = label,
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = size.sp,
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Makes one cell of the grid tappable, in direct mode only.
 *
 * The filter is built here, inside the cell, so **each cell has its own**. That
 * is the point of it: a tremor that taps the same letter twice has to count
 * once, while touching two different letters in quick succession is two real
 * choices and must both land. One filter shared across the grid would have
 * turned fast, deliberate typing into half a sentence.
 *
 * Returns an empty modifier when there is nothing to tap, so the cells stay
 * inert in the modes where the letters are for reading rather than pressing.
 */
@Composable
private fun tapGuard(
    onTap: (() -> Unit)?,
    debounceMs: Long,
    antiTremor: Boolean,
): Modifier {
    if (onTap == null) return Modifier
    val filter = remember(debounceMs, antiTremor) {
        SwitchFilter(debounceMs = debounceMs, restartOnReject = antiTremor)
    }
    return Modifier.pointerInput(filter, onTap) {
        detectTapGestures {
            if (filter.accept(Switch.Write, SystemClock.elapsedRealtime())) onTap()
        }
    }
}

/** Beyond this a letter looks shouted rather than clear, even given the room. */
private const val MAX_KEY_TEXT_SP = 64f

/** 1300 becomes "1,3", using the comma Catalan writes decimals with. */
private fun formatSeconds(millis: Long): String {
    val tenths = (millis + 50) / 100
    return "${tenths / 10},${tenths % 10}"
}
