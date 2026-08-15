package cat.merce.comunicador.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val Background = Color(0xFF000000)
private val CellIdle = Color(0xFF1F1F1F)
private val CellLit = Color(0xFFD32F2F)
private val Ink = Color(0xFFFFFFFF)
private val TextAreaBackground = Color(0xFF121212)

@Composable
fun ScanScreen(controller: ScanController) {

    // The clock lives here and nowhere else. The scan machine itself never
    // measures time; it only gets told that a step has passed.
    LaunchedEffect(controller) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(12.dp)
    ) {
        ComposedText(
            text = controller.text,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        for ((rowIndex, row) in controller.rows.withIndex()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                for ((colIndex, key) in row.withIndex()) {
                    KeyCell(
                        key = key,
                        lit = controller.isLit(rowIndex, colIndex),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun ComposedText(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .padding(4.dp)
            .background(TextAreaBackground, RoundedCornerShape(10.dp))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            // A trailing bar so the end of the sentence is visible, and so a
            // typed space is not an invisible event.
            text = text + "|",
            color = Ink,
            fontSize = 40.sp
        )
    }
}

@Composable
private fun KeyCell(key: Key, lit: Boolean, modifier: Modifier = Modifier) {
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
            text = key.label,
            color = Ink,
            // Word labels need to be smaller than single characters to fit.
            fontSize = if (key.label.length > 1) 18.sp else 36.sp,
            textAlign = TextAlign.Center
        )
    }
}
