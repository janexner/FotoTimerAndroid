package com.exner.tools.fototimer.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.exner.tools.fototimer.data.model.FotoTimerCounterState
import com.exner.tools.fototimer.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessDetailsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    processId: Long,
) {
    // TODO
    // this whole file is TODO

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
                                Button(
                                    onClick = {
                                        runningProcessViewModel.cancelRunner()
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
                                        .fillMaxWidth(0.3f)
                                        .align(Alignment.Center),
                                    enabled = runningProcessViewModel.counterState.state == FotoTimerCounterState.COUNTING
                                ) {
                                    ButtonText(text = "Stop")
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
                    Button(
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
                        ButtonText(text = "Stop")
                    }
                }
            }
        }
    }
}
