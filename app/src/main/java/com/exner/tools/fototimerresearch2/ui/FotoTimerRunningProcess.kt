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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.model.FotoTimerCounterState
import com.exner.tools.fototimerresearch2.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessDetailsDestination
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessLauncherDestination
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessListDestination
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.result.ResultBackNavigator

@Destination
@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    processId: Long,
) {
    // are we done?
    when (runningProcessViewModel.counterState.state) {
        FotoTimerCounterState.COMPLETED -> {
            Log.i("jexner FTR", "counterState completed")
//            resultBackNavigator.navigateBack(result = true)
        }
        FotoTimerCounterState.CANCELLED -> {
            Log.i("jexner FTR", "counterState cancelled")
//            resultBackNavigator.navigateBack(result = false)
        }
        FotoTimerCounterState.CHAINING -> {
            Log.i("jexner FTR", "will chain!")
            navigator.navigate(FotoTimerProcessLauncherDestination(runningProcessViewModel.processGotoId!!)) {
                popUpTo(FotoTimerProcessLauncherDestination.route) {
                    inclusive = true
                }
            }
        }
    }

    // this screen should stay visible, maybe
    if (runningProcessViewModel.keepsScreenOn) {
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
                            runningProcessViewModel.cancelRunner()
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
                            duration = runningProcessViewModel.elapsedIntervalTime,
                            modifier = Modifier.height(fifth * 4)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(fifth)
                        ) {
                            Text(
                                text = "Time left ${durationToAnnotatedString(duration = runningProcessViewModel.timeLeftUntilEndOfProcess)}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterStart)
                            )
                            if (!sharedSettings.getBoolean(
                                    "preference_stop_is_everywhere",
                                    false
                                )
                            ) {
                                // show huge cancel button
                                FilledTonalButton(
                                    onClick = {
                                        runningProcessViewModel.cancelRunner()
                                        navigator.navigate(FotoTimerProcessDetailsDestination(processId)) {
                                            popUpTo(FotoTimerProcessDetailsDestination.route) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(0.3f)
                                        .align(Alignment.Center),
                                    enabled = runningProcessViewModel.counterState.state == FotoTimerCounterState.COUNTING
                                ) {
                                    Text(
                                        text = "Stop",
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                            }
                            Text(
                                text = "Round ${runningProcessViewModel.counterState.roundNumber} of ${runningProcessViewModel.numberOfIntervals}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
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
                            runningProcessViewModel.cancelRunner()
                            navigator.navigate(FotoTimerProcessDetailsDestination(processId)) {
                                popUpTo(FotoTimerProcessDetailsDestination.route) {
                                    inclusive = true
                                }
                            }
                        }
                    }
            ) {
                HeaderText(text = runningProcessViewModel.processName)
                Divider(modifier = Modifier.padding(8.dp))
                // show time
                BigTimerText(
                    duration = runningProcessViewModel.elapsedIntervalTime,
                )
                Text(
                    text = "Interval Time (total ${runningProcessViewModel.intervalTime})",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(0.001f))
                MediumTimerAndIntervalText(
                    duration = runningProcessViewModel.timeLeftUntilEndOfProcess,
                    intervalText = "${runningProcessViewModel.counterState.roundNumber} of ${runningProcessViewModel.numberOfIntervals}",
                )
                Text(
                    text = "Process Time (total ${runningProcessViewModel.processTime})",
                    style = MaterialTheme.typography.bodyLarge
                )
                // show additional information (next process(es))
                Spacer(modifier = Modifier.weight(0.01f))
                if (!sharedSettings.getBoolean("preference_stop_is_everywhere", false)) {
                    // show huge cancel button
                    FilledTonalButton(
                        onClick = {
                            runningProcessViewModel.cancelRunner()
                            navigator.navigate(FotoTimerProcessDetailsDestination(processId)) {
                                popUpTo(FotoTimerProcessDetailsDestination.route) {
                                    inclusive = true
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f),
                        enabled = runningProcessViewModel.counterState.state == FotoTimerCounterState.COUNTING
                    ) {
                        Text(
                            text = "Stop",
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }
            }
        }
    }
}
