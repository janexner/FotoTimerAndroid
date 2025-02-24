package com.exner.tools.fototimer.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
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
import androidx.core.text.isDigitsOnly
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.exner.tools.fototimer.ui.theme.Theme
import java.util.Locale
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
fun DefaultSpacer() {
    Spacer(modifier = Modifier.size(16.dp))
}

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
    TextField(
        value = text,
        label = label,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        onValueChange = { raw ->
            text = raw
            if (raw.isNotEmpty() && raw.isDigitsOnly()) {
                val parsed = text.toInt()
                onValueChange(parsed)
            }
        },
        placeholder = placeholder,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun TextAndTriStateToggle(
    text: String,
    currentTheme: Theme,
    updateTheme: (Theme) -> Unit
) {
    val states = listOf(
        Theme.Auto,
        Theme.Dark,
        Theme.Light,
    )

    ListItem(
        headlineContent = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        trailingContent = {
            Surface(
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    states.forEach { thisTheme ->
                        Text(
                            text = thisTheme.name,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(50))
                                .clickable {
                                    updateTheme(thisTheme)
                                }
                                .background(
                                    if (thisTheme == currentTheme) {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                )
                                .padding(
                                    vertical = 8.dp,
                                    horizontal = 16.dp,
                                ),
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun durationToAnnotatedString(
    duration: Duration,
    withHours: Boolean,
    postText: String? = null
): AnnotatedString {
    // convert seconds to "00:00" style string
    val output = duration.toComponents { hours, minutes, seconds, _ ->
        String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }
    val tmp = output.split(":")
    val styledOutput = buildAnnotatedString {
        var myStyle = SpanStyle()
        if (withHours) {
            if ("00" == tmp[0]) {
                myStyle = SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
            }
            withStyle(style = myStyle) {
                append(tmp[0])
            }
            append(":")
        }
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
        if (postText !== null) {
            append(postText)
        }
    }

    return styledOutput
}

@Composable
fun BigTimerText(duration: Duration, withHours: Boolean, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier) {
        AutoSizeText(
            text = durationToAnnotatedString(duration, withHours),
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
    withHours: Boolean,
    intervalText: String,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        val completeText = durationToAnnotatedString(duration, withHours, " | Round $intervalText")
        BoxWithConstraints {
            AutoSizeText(
                text = completeText,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                fontSize = 48.dp.toTextDp(),
                constraints = constraints
            )
        }
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
        defaultFontFamilyResolver = LocalFontFamilyResolver.current,
        defaultDensity = LocalDensity.current,
        defaultLayoutDirection = LayoutDirection.Ltr
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

@Preview(fontScale = 1.5f)
@Composable
fun BTTTest() {
    FotoTimerTheme {
        Column {
            BigTimerText(duration = 10.seconds, false)
            MediumTimerAndIntervalText(
                duration = 75.seconds,
                withHours = false,
                intervalText = "1 of 3"
            )
        }
    }
}

@Preview(fontScale = 1.0f)
@Composable
fun BTTNormalTest() {
    FotoTimerTheme {
        Column {
            BigTimerText(duration = 10.seconds, false)
            MediumTimerAndIntervalText(
                duration = 75.seconds,
                withHours = false,
                intervalText = "1 of 3"
            )
        }
    }
}