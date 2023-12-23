package com.exner.tools.fototimer

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.exner.tools.fototimer.data.model.FotoTimerSettingsViewModel
import com.exner.tools.fototimer.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimer.ui.LockScreenOrientation
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: FotoTimerSettingsViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("jexner MainActivity", "SVM: $settingsViewModel")

        // load sounds
        FotoTimerSoundPoolHolder.loadSounds(this)

        setContent {
            val dynamicColour = settingsViewModel.getUseDynamicColour()
            val darkTheme = isSystemInDarkTheme()

            FotoTimerTheme(
                dynamicColor = dynamicColour,
                darkTheme = darkTheme,
            ) {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
                if (isCompact) {
                    // lock screen in portrait mode for small-ish screens
                    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }

                FotoTimerApp()
            }
        }
    }
}
