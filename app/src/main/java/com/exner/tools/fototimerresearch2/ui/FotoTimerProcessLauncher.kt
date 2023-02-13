package com.exner.tools.fototimerresearch2.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerCounterState
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessLauncherViewModel
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessDetailsDestination
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerRunningProcessDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FotoTimerProcessLauncher(
    fotoTimerProcessLauncherViewModel: FotoTimerProcessLauncherViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    processId: Long,
    nextState: Int,
    pause: Int, // set to zero when not needed
) {
    // are we done?
    when (fotoTimerProcessLauncherViewModel.counterState.state) {
        FotoTimerCounterState.COMPLETED -> {
            Log.i("jexner FTPL", "counterState completed")
            Log.i("jexner FTPL", "Now running $processId")
            navigator.navigate(FotoTimerRunningProcessDestination(processId))
        }
        FotoTimerCounterState.CANCELLED -> {
            Log.i("jexner FTPL", "counterState cancelled")
//            resultBackNavigator.navigateBack(result = false)
        }
        FotoTimerCounterState.CHAINING -> {
            Log.i("jexner FTPL", "will chain!")
            // TODO
        }
    }
    when (nextState) {
        FotoTimerCounterState.LEADIN -> {
            Log.i("jexner FTPL", "nextState lead-in")
        }
        FotoTimerCounterState.CHAINING -> {
            Log.i("jexner FTPL", "nextState chaining")
        }
    }

    if (fotoTimerProcessLauncherViewModel.noProcessSoGoBack) {
        Log.i("jexner FTPL", "Process ID $processId was not good, so jumping back...")
        navigator.popBackStack()
    }

    // this screen should stay visible, maybe
    if (fotoTimerProcessLauncherViewModel.keepsScreenOn) {
        KeepScreenOn()
    }
    // we need the settings
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)
    // what's the orientation, right now?
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // show horizontally
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .conditional(
                        sharedSettings.getBoolean(
                            "preference_stop_is_everywhere",
                            false
                        )
                    ) {
                        clickable {
                            fotoTimerProcessLauncherViewModel.cancelRunner()
                            navigator.navigate(FotoTimerProcessDetailsDestination(processId)) {
                                popUpTo(FotoTimerProcessDetailsDestination.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val fifth = maxHeight / 5
                    Column {
                        BigTimerText(
                            duration = fotoTimerProcessLauncherViewModel.elapsedChainTime,
                            modifier = Modifier.height(fifth * 4)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(fifth)
                        ) {
                            if (!sharedSettings.getBoolean(
                                    "preference_stop_is_everywhere",
                                    false
                                )
                            ) {
                                // show huge cancel button
                                HugeCancelButton(
                                    fotoTimerProcessLauncherViewModel,
                                    navigator,
                                    processId
                                )
                            }
                        }
                        // show additional information (next process(es))
                    }
                }
            }
        }
        else -> {
            // show
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .conditional(
                        sharedSettings.getBoolean(
                            "preference_stop_is_everywhere",
                            false
                        )
                    ) {
                        clickable {
                            fotoTimerProcessLauncherViewModel.cancelRunner()
                            navigator.navigate(FotoTimerProcessDetailsDestination(processId)) {
                                popUpTo(FotoTimerProcessDetailsDestination.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
            ) {
                HeaderText(text = fotoTimerProcessLauncherViewModel.processName)
                Divider(modifier = Modifier.padding(8.dp))
                // show time
                BigTimerText(
                    duration = fotoTimerProcessLauncherViewModel.elapsedChainTime,
                )
                Text(
                    text = "Wait Time (total ${fotoTimerProcessLauncherViewModel.pauseTime})",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(0.001f))
                // show additional information (next process(es))
                Spacer(modifier = Modifier.weight(0.01f))
                if (!sharedSettings.getBoolean("preference_stop_is_everywhere", false)) {
                    // show huge cancel button
                    HugeCancelButton(fotoTimerProcessLauncherViewModel, navigator, processId)
                }
            }
        }
    }
}

@Composable
private fun HugeCancelButton(
    fotoTimerProcessLauncherViewModel: FotoTimerProcessLauncherViewModel,
    navigator: DestinationsNavigator,
    processId: Long
) {
    FilledTonalButton(
        onClick = {
            fotoTimerProcessLauncherViewModel.cancelRunner()
            navigator.navigate(
                FotoTimerProcessDetailsDestination(
                    processId
                )
            ) {
                popUpTo(FotoTimerProcessDetailsDestination.route) {
                    inclusive = true
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.3f),
        enabled = fotoTimerProcessLauncherViewModel.counterState.state == FotoTimerCounterState.CHAINING || fotoTimerProcessLauncherViewModel.counterState.state == FotoTimerCounterState.LEADIN
    ) {
        Text(
            text = "Stop",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
