package cat.merce.comunicador.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background = Color(0xFF000000)
private val CellIdle = Color(0xFF1F1F1F)
private val CellLit = Color(0xFFD32F2F)
private val Ink = Color(0xFFFFFFFF)
private val TextAreaBackground = Color(0xFF121212)
private val SettingsCorner = Color(0xFF2A2A2A)
private val SettingsCornerInk = Color(0xFF6E6E6E)

@Composable
fun ScanScreen(controller: ScanController) {

    // The clock lives here and nowhere else. The scan machine itself never
    // measures time; it only gets told that a step has passed.
    //
    // Keyed on the interval as well, so changing the speed in settings restarts
    // the loop at the new rate rather than waiting for the old one to come
    // round.
    LaunchedEffect(controller, controller.scanIntervalMs) {
        // Count from a fixed starting point rather than sleeping for the
        // interval each time. Plain repeated delays drift, and a scan that
        // slowly loses its rhythm is exactly what the brief forbids.
        var nextStepAt = SystemClock.elapsedRealtime()
        while (true) {
            nextStepAt += controller.scanIntervalMs
            delay((nextStepAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L))
            controller.tick()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {

        Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {

            Banner(
                text = if (controller.inSettings) {
                    "VELOCITAT: ${formatSeconds(controller.scanIntervalMs)} s"
                } else {
                    controller.text + "|"
                },
                modifier = Modifier.fillMaxWidth().weight(1f)
            )

            // Every row gets the same number of slots so the columns line up
            // even when a row is short.
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

        if (!controller.inSettings) {
            SettingsCornerButton(
                onOpen = controller::openSettings,
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

/**
 * A small, dim touch target for a carer.
 *
 * It is not part of the grid and cannot be reached by the switch, so she can
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
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onOpen() })
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "⚙", color = SettingsCornerInk, fontSize = 22.sp)
    }
}

@Composable
private fun Banner(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(TextAreaBackground, RoundedCornerShape(10.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = Ink, fontSize = 40.sp)
    }
}

@Composable
private fun KeyCell(label: String, lit: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = if (lit) CellLit else CellIdle,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Ink,
            // Words need to be smaller than single characters to fit.
            fontSize = when {
                label.length > 6 -> 16.sp
                label.length > 1 -> 22.sp
                else -> 36.sp
            },
            textAlign = TextAlign.Center
        )
    }
}

/** 1000 becomes "1,0", using the comma Catalan writes decimals with. */
private fun formatSeconds(millis: Long): String {
    val tenths = (millis + 50) / 100
    return "${tenths / 10},${tenths % 10}"
}
