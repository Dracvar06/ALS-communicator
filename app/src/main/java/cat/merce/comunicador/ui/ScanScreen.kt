package cat.merce.comunicador.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

private val Background = Color(0xFF000000)
private val CellIdle = Color(0xFF1F1F1F)
private val CellLit = Color(0xFFD32F2F)
private val Ink = Color(0xFFFFFFFF)
private val DimInk = Color(0xFF9E9E9E)
private val TextAreaBackground = Color(0xFF121212)
private val SettingsCorner = Color(0xFF2A2A2A)
private val SettingsCornerInk = Color(0xFF6E6E6E)

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
            controller.inDiagnostics -> DiagnosticsPanel(controller)
            controller.inSettings -> SettingsPanel(controller)
            controller.inPhrases -> {
                PhrasesGrid(controller)
                // Same halves as writing: right selects, left goes back.
                if (controller.touchInput) {
                    TouchZones(
                        onWrite = controller::press,
                        onUndo = controller::undo,
                        debounceMs = controller.debounceMs,
                    )
                }
            }
            else -> {
                WritingGrid(controller)
                // Sits above the grid but below the gear, so the gear corner
                // still opens settings. Only present when a carer has left
                // touch input on.
                if (controller.touchInput) {
                    TouchZones(
                        onWrite = controller::press,
                        onUndo = controller::undo,
                        debounceMs = controller.debounceMs,
                    )
                }
                SettingsCornerButton(
                    onOpen = controller::openSettings,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
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

    // She cannot leave this screen: every key is swallowed to be reported, so
    // neither switch does anything. It therefore has to let go by itself if
    // the carer walks away. Only touch resets the clock, because she would be
    // pressing switches the whole time and that is precisely the stranded case.
    LaunchedEffect(controller.diagnosticsTouch) {
        delay(ScanController.DIAGNOSTICS_IDLE_MS)
        controller.closeSettings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures { controller.noteDiagnosticsTouch() } }
            .padding(40.dp)
    ) {
        Text(
            text = "Comprovació dels polsadors",
            color = Ink,
            fontFamily = Hyperlegible,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Prem cada polsador. Aquesta pantalla es tanca sola.",
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        if (controller.recentKeys.isEmpty()) {
            Text(
                text = "Esperant…",
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
                    text = "codi ${key.keyCode}",
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

        TouchButton(text = "TANCA", onTap = controller::closeSettings)
    }
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
private fun TouchZones(onWrite: () -> Unit, onUndo: () -> Unit, debounceMs: Long) {
    // The same debounce as the physical switches, so a tremor that taps twice
    // counts once here too. Rebuilt when the setting changes. Without this, the
    // touch path skipped the filter entirely and every double tap registered.
    val filter = remember(debounceMs) { SwitchFilter(debounceMs = debounceMs) }

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
            modifier = Modifier.fillMaxWidth().weight(1.9f)
        )

        // Every row gets the same number of slots so the columns line up even
        // when a row is short.
        val columns = controller.rows.maxOf { it.size }

        for ((rowIndex, row) in controller.rows.withIndex()) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                for ((colIndex, key) in row.withIndex()) {
                    KeyCell(
                        label = controller.label(key),
                        lit = controller.isLit(rowIndex, colIndex),
                        modifier = Modifier.weight(1f).fillMaxHeight()
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
        Text(
            text = "Velocitat de l'escaneig",
            color = DimInk,
            fontFamily = Hyperlegible,
            fontSize = 28.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "${formatSeconds(chosenMs.roundToLong())} segons per pas",
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
            Text("més ràpid", color = DimInk, fontFamily = Hyperlegible, fontSize = 22.sp)
            Spacer(Modifier.weight(1f))
            Text("més lent", color = DimInk, fontFamily = Hyperlegible, fontSize = 22.sp)
        }

        Spacer(Modifier.height(44.dp))

        // Extra time on the first letter of each row, so entering a row and
        // reacting to its first letter are not crammed into one interval.
        var chosenFirst by remember { mutableFloatStateOf(controller.firstCellExtraMs.toFloat()) }

        Text(
            text = "Temps extra a la primera lletra: " +
                "+${formatSeconds(chosenFirst.roundToLong())} s",
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

        Spacer(Modifier.height(44.dp))

        // How close together two presses of one switch may be. Raise it if a
        // single press is producing several letters.
        var chosenDebounce by remember { mutableFloatStateOf(controller.debounceMs.toFloat()) }

        Text(
            text = "Temps mínim entre polsacions: ${chosenDebounce.roundToLong()} ms",
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
                    text = "Tocar la pantalla per escriure",
                    color = Ink,
                    fontFamily = Hyperlegible,
                    fontSize = 30.sp
                )
                Text(
                    text = "Dreta: escriu. Esquerra: desfà.",
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

        Spacer(Modifier.height(40.dp))

        Row {
            TouchButton(text = "TANCA", onTap = controller::closeSettings)
            Spacer(Modifier.width(20.dp))
            TouchButton(text = "COMPROVA ELS POLSADORS", onTap = controller::openDiagnostics)
        }

        Spacer(Modifier.height(20.dp))
        Text(
            // She is never stuck here even if nobody is holding the tablet.
            text = "Qualsevol dels dos polsadors també tanca aquesta pantalla.",
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
private fun ComposedText(text: String, modifier: Modifier = Modifier) {
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
        for ((rowIndex, row) in controller.rows.withIndex()) {
            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                for ((colIndex, key) in row.withIndex()) {
                    PhraseCell(
                        label = controller.label(key),
                        lit = controller.isLit(rowIndex, colIndex),
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun PhraseCell(label: String, lit: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .padding(5.dp)
            .background(
                color = if (lit) CellLit else CellIdle,
                shape = RoundedCornerShape(10.dp)
            )
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
private fun KeyCell(label: String, lit: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = if (lit) CellLit else CellIdle,
                shape = RoundedCornerShape(10.dp)
            ),
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

/** Beyond this a letter looks shouted rather than clear, even given the room. */
private const val MAX_KEY_TEXT_SP = 64f

/** 1300 becomes "1,3", using the comma Catalan writes decimals with. */
private fun formatSeconds(millis: Long): String {
    val tenths = (millis + 50) / 100
    return "${tenths / 10},${tenths % 10}"
}
