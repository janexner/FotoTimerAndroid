package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAndSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineText = { BodyText(text = text) },
        modifier = Modifier.fillMaxWidth(),
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldForTimes(
    value: String,
    label: @Composable() (() -> Unit)?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: @Composable() (() -> Unit)? = null,
) {
    TextField(
        value = value,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = onValueChange,
        placeholder = placeholder,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun durationToAnnotatedString(duration: Duration): AnnotatedString {
    // convert seconds to "00:00" style string
    val output = duration.toComponents { hours, minutes, seconds, _ ->
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
    val tmp = output.split(":")
    val styledOutput = buildAnnotatedString {
        var myStyle = SpanStyle()
        if ("00" == tmp[0]) {
            myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        }
        withStyle(style = myStyle) {
            append(tmp[0])
        }
        append(":")
        if ("00" == tmp[1]) {
            myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
        } else {
            myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f))
        }
        withStyle(style = myStyle) {
            append(tmp[1])
        }
        append(":")
        append(tmp[2])
    }

    return styledOutput
}

@Composable
fun BigTimerText(duration: Duration, modifier: Modifier = Modifier) {

    Text(
        text = durationToAnnotatedString(duration),
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        fontSize = 94.dp.toTextDp()
    )
}

@Composable
fun MediumTimerAndIntervalText(
    duration: Duration,
    intervalText: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = durationToAnnotatedString(duration),
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 48.dp.toTextDp(),
            textAlign = TextAlign.Center,
            modifier = Modifier.alignByBaseline()
        )
        Spacer(modifier = Modifier.weight(0.05f))
        Text(
            text = intervalText,
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 48.dp.toTextDp(),
            textAlign = TextAlign.Center,
            modifier = Modifier.alignByBaseline()
        )
    }
}

@Composable
fun Dp.toTextDp(): TextUnit = textSp(density = LocalDensity.current)

private fun Dp.textSp(density: Density): TextUnit = with(density) {
    this@textSp.toSp()
}

@Composable
fun KeepScreenOn() {
    val currentView = LocalView.current
    DisposableEffect(Unit) {
        currentView.keepScreenOn = true
        onDispose {
            currentView.keepScreenOn = false
        }
    }
}

@Preview(fontScale = 1.5f)
@Composable
fun BTTTest() {
    FotoTimerTheme() {
        Column() {
            BigTimerText(duration = 75.seconds)
            MediumTimerAndIntervalText(duration = 10.seconds, intervalText = "1 of 3")
        }
    }
}

@Preview(fontScale = 1.0f)
@Composable
fun BTTNormalTest() {
    FotoTimerTheme() {
        Column() {
            BigTimerText(duration = 75.seconds)
            MediumTimerAndIntervalText(duration = 10.seconds, intervalText = "1 of 3")
        }
    }
}
