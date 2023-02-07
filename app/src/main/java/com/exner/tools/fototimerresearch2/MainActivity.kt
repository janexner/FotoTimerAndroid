package com.exner.tools.fototimerresearch2

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimerresearch2.ui.LockScreenOrientation
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load sounds
        FotoTimerSoundPoolHolder.loadSounds(this)

        // are we in forced dark mode?
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(this)
        val forceNightMode = sharedSettings.getBoolean("preference_night_mode", false)

        setContent {
            FotoTimerTheme(
                darkTheme = forceNightMode
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
