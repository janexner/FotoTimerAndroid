package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth(),
        style = MaterialTheme.typography.headlineMedium
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun SmallBodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun BigTimerText(milliSeconds: Long, modifier: Modifier = Modifier) {
    // convert seconds to "00:00" style string
    val seconds = milliSeconds / 1000L
    var output = (seconds / 60).toInt().toString().padStart(2, '0')
    output += ":"
    output += (seconds % 60).toInt().toString().padStart(2, '0')

    Text(
        text = output,
        style = MaterialTheme.typography.headlineLarge,
        fontSize = 150.dp.toTextDp()
    )
}

@Composable
fun MediumTimerAndIntervalText(milliSeconds: Long, intervalText: String, modifier: Modifier = Modifier) {
    // convert seconds to "00:00" style string
    val seconds = milliSeconds / 1000L
    var output = (seconds / 60).toInt().toString().padStart(2, '0')
    output += ":"
    output += (seconds % 60).toInt().toString().padStart(2, '0')

    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = output,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 68.dp.toTextDp(),
            modifier = Modifier.alignByBaseline()
        )
        Spacer(modifier = Modifier.weight(0.05f))
        Text(
            text = intervalText,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 68.dp.toTextDp(),
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun Dp.toTextDp(): TextUnit = textSp(density = LocalDensity.current)

private fun Dp.textSp(density: Density): TextUnit = with(density) {
    this@textSp.toSp()
}

@Preview(fontScale = 1.5f)
@Composable
fun BTTTest() {
    FotoTimerTheme() {
        Column() {
            BigTimerText(milliSeconds = 75000)
            MediumTimerAndIntervalText(milliSeconds = 10000, intervalText = "1 of 3")
        }
    }
}

@Preview(fontScale = 1.0f)
@Composable
fun BTTNormalTest() {
    FotoTimerTheme() {
        Column() {
            BigTimerText(milliSeconds = 75000)
            MediumTimerAndIntervalText(milliSeconds = 10000, intervalText = "1 of 3")
        }
    }
}
