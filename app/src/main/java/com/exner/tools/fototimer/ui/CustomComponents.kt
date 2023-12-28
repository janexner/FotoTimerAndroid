package com.exner.tools.fototimer.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
fun BodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier,
    )
}

@Composable
fun SmallBodyText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
    )
}

@Composable
fun ButtonText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge,
        textAlign = TextAlign.Center,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextAndSwitch(
    text: String,
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?
) {
    ListItem(
        headlineContent = {
            Text(
                text = text,
                maxLines = 3,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier
            )
        },
    )
}

@Composable
fun TextFieldForTimes(
    value: Int,
    label: @Composable (() -> Unit)?,
    onValueChange: (Int) -> Unit,
    placeholder: @Composable (() -> Unit)? = null,
) {
    var text by remember(value) { mutableStateOf(value.toString()) }
    OutlinedTextField(
        value = text,
        label = label,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { raw ->
            text = raw
            val parsed = text.toIntOrNull() ?: 0
            onValueChange(parsed)
        },
        placeholder = placeholder,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

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
            myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        }
        withStyle(style = myStyle) {
            append(tmp[0])
        }
        append(":")
        myStyle = if ("00" == tmp[1]) {
            SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        } else {
            SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f))
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
    BoxWithConstraints(modifier = modifier) {
        AutoSizeText(
            text = durationToAnnotatedString(duration),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            fontSize = 294.dp.toTextDp(),
            constraints = constraints
        )
    }
}

@Composable
fun MediumTimerAndIntervalText(
    duration: Duration,
    intervalText: String
) {
    Row(modifier = Modifier) {
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

@OptIn(ExperimentalTextApi::class)
@Composable
private fun AutoSizeText(
    text: AnnotatedString,
    constraints: Constraints,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val tm = TextMeasurer(
        fallbackFontFamilyResolver = LocalFontFamilyResolver.current,
        fallbackDensity = LocalDensity.current,
        fallbackLayoutDirection = LayoutDirection.Ltr
    )
    var shrunkFontSize = fontSize
    // measure
    var measure = tm.measure(
        text = text,
        style = TextStyle.Default.copy(
            fontSize = shrunkFontSize,
        ),
        maxLines = 1,
        constraints = constraints
    )
    while (measure.hasVisualOverflow) {
        shrunkFontSize = (shrunkFontSize.value - 2).sp
        measure = tm.measure(
            text = text,
            style = TextStyle.Default.copy(
                fontSize = shrunkFontSize,
            ),
            maxLines = 1,
            constraints = constraints
        )
    }
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = shrunkFontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        onTextLayout = onTextLayout,
        style = style
    )
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

// from https://stackoverflow.com/questions/67768746/chaining-modifier-based-on-certain-conditions-in-android-compose
fun Modifier.conditional(condition: Boolean, modifier: Modifier.() -> Modifier): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}

@Preview(fontScale = 1.5f)
@Composable
fun BTTTest() {
    FotoTimerTheme {
        Column {
            BigTimerText(duration = 10.seconds)
            MediumTimerAndIntervalText(duration = 75.seconds, intervalText = "1 of 3")
        }
    }
}

@Preview(fontScale = 1.0f)
@Composable
fun BTTNormalTest() {
    FotoTimerTheme {
        Column {
            BigTimerText(duration = 10.seconds)
            MediumTimerAndIntervalText(duration = 75.seconds, intervalText = "1 of 3")
        }
    }
}