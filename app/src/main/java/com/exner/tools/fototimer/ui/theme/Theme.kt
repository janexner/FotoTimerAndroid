package com.exner.tools.fototimer.ui.theme

import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)


private val RealDarkColors = darkColorScheme(
    primary = jexner_theme_dark_black,
    onPrimary = jexner_theme_dark_red,
    primaryContainer = jexner_theme_dark_dark,
    onPrimaryContainer = jexner_theme_dark_red,
    secondary = jexner_theme_dark_black,
    onSecondary = jexner_theme_dark_red,
    secondaryContainer = jexner_theme_dark_black,
    onSecondaryContainer = jexner_theme_dark_red,
    tertiary = jexner_theme_dark_black,
    onTertiary = jexner_theme_dark_red,
    tertiaryContainer = jexner_theme_dark_black,
    onTertiaryContainer = jexner_theme_dark_red,
    error = jexner_theme_dark_black,
    errorContainer = jexner_theme_dark_black,
    onError = jexner_theme_dark_red,
    onErrorContainer = jexner_theme_dark_red,
    background = jexner_theme_dark_black,
    onBackground = jexner_theme_dark_red,
    surface = jexner_theme_dark_black,
    onSurface = jexner_theme_dark_red,
    surfaceVariant = jexner_theme_dark_black,
    onSurfaceVariant = jexner_theme_dark_red,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = jexner_theme_dark_black,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun FotoTimerTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    // are we in forced dark mode? Do we want dynamic colors?
    val currentDarkTheme by remember { mutableStateOf(sharedSettings.getBoolean("preference_night_mode", false)) }
    val dynamicColor by remember { mutableStateOf(sharedSettings.getBoolean("preference_dynamic_color", true)) }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (currentDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        currentDarkTheme -> RealDarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}