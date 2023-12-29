package com.exner.tools.fototimer

import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.exner.tools.fototimer.audio.SoundPoolHolder
import com.exner.tools.fototimer.ui.destinations.FotoTimerGlobalScaffold
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(this)
        val forceNightMode = sharedSettings.getBoolean("preference_night_mode", false)

        setContent {
            FotoTimerTheme(
                darkTheme = forceNightMode
            ) {
                FotoTimerGlobalScaffold()
            }
        }
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
