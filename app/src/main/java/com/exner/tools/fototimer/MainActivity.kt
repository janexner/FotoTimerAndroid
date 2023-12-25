package com.exner.tools.fototimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.exner.tools.fototimer.ui.destinations.FotoTimerGlobalScaffold
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FotoTimerTheme {
                FotoTimerGlobalScaffold()
            }
        }
    }
}
