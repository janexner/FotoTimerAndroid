package com.exner.tools.fototimer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.exner.tools.fototimer.audio.SoundPoolHolder
import com.exner.tools.fototimer.audio.VibratorHolder
import com.exner.tools.fototimer.data.preferences.FotoTimerPreferencesManager
import com.exner.tools.fototimer.ui.MainViewModel
import com.exner.tools.fototimer.ui.destinations.FotoTimerGlobalScaffold
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesManager: FotoTimerPreferencesManager

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            enableEdgeToEdge()
        }

        setContent {
            val nightModeState = viewModel.nightModeState.collectAsState()

            @RequiresApi(Build.VERSION_CODES.R)
            if (nightModeState.value) {
                window.navigationBarColor = Color(0xFF000000).toArgb()
                window.insetsController?.setSystemBarsAppearance(0,
                    APPEARANCE_LIGHT_NAVIGATION_BARS)
            } else {
                window.navigationBarColor = Color(0xFFFFFFFF).toArgb()
                window.insetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_NAVIGATION_BARS,
                    APPEARANCE_LIGHT_NAVIGATION_BARS)
            }

            FotoTimerTheme(
                darkTheme = nightModeState.value
            ) {
                FotoTimerGlobalScaffold()
            }
        }

        // experiment: vibrate
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        VibratorHolder.initialise(vibrator)
    }

    override fun onResume() {
        super.onResume()

        // load all sounds
        SoundPoolHolder.loadSounds(this)
    }

    override fun onPause() {
        super.onPause()

        // release the kraken
        SoundPoolHolder.release()
    }
}
