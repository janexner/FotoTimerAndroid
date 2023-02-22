package com.exner.tools.fototimer

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.exner.tools.fototimer.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimer.ui.LockScreenOrientation
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load sounds
        FotoTimerSoundPoolHolder.loadSounds(this)

        setContent {
            FotoTimerTheme() {
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
