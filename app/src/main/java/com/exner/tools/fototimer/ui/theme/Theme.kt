package com.exner.tools.fototimer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = md_jexner_alabaster_80,
    onPrimary = md_jexner_alabaster_20,
    primaryContainer = md_jexner_alabaster_30,
    onPrimaryContainer = md_jexner_alabaster_90,
    secondary = md_jexner_blue_80,
    onSecondary = md_jexner_blue_20,
    secondaryContainer = md_jexner_blue_30,
    onSecondaryContainer = md_jexner_blue_90,
    tertiary = md_jexner_orange_80,
    onTertiary = md_jexner_orange_20,
    tertiaryContainer = md_jexner_orange_30,
    onTertiaryContainer = md_jexner_orange_90,
    background = md_jexner_dim_gray_10,
    onBackground = md_jexner_dim_gray_90,
    surfaceVariant = md_jexner_khaki_30,
    onSurfaceVariant = md_jexner_khaki_80,
    outline = md_jexner_khaki_60,
    outlineVariant = md_jexner_khaki_30,
)

private val LightColorScheme = lightColorScheme(
    primary = md_jexner_alabaster_40,
    onPrimary = md_jexner_alabaster_100,
    primaryContainer = md_jexner_alabaster_90,
    onPrimaryContainer = md_jexner_alabaster_10,
    secondary = md_jexner_blue_40,
    onSecondary = md_jexner_blue_100,
    secondaryContainer = md_jexner_blue_90,
    onSecondaryContainer = md_jexner_blue_10,
    tertiary = md_jexner_orange_40,
    onTertiary = md_jexner_orange_100,
    tertiaryContainer = md_jexner_orange_90,
    onTertiaryContainer = md_jexner_orange_10,
    background = md_jexner_dim_gray_99,
    onBackground = md_jexner_dim_gray_10,
    surfaceVariant = md_jexner_khaki_90,
    onSurfaceVariant = md_jexner_khaki_30,
    outline = md_jexner_khaki_50,
    outlineVariant = md_jexner_khaki_80,
)

@Composable
fun FotoTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val currentDarkTheme by remember { mutableStateOf(darkTheme) }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (currentDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        currentDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}