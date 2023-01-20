package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
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
        fontSize = 60.sp
    )
}

@Preview
@Composable
fun BTTTest() {
    FotoTimerTheme() {
        BigTimerText(milliSeconds = 75000)
    }
}
