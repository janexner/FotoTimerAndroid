package com.exner.tools.fototimer.ui.destinations

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.steps.ProcessDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessPauseDisplayStepAction
import com.exner.tools.fototimer.ui.BigTimerText
import com.exner.tools.fototimer.ui.KeepScreenOn
import com.exner.tools.fototimer.ui.MediumTimerAndIntervalText
import com.exner.tools.fototimer.ui.ProcessRunViewModel
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.Duration.Companion.seconds

@Destination
@Composable
fun ProcessRun(
    processId: Long,
    processRunViewModel: ProcessRunViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Log.d("ProcessRunScreen", "entering composable...")

    val displayAction by processRunViewModel.displayAction.observeAsState()
    val numberOfSteps by processRunViewModel.numberOfSteps.observeAsState()
    val currentStepNumber by processRunViewModel.currentStepNumber.observeAsState()
    val keepScreenOn by processRunViewModel.keepScreenOn.observeAsState()
    val hasLoop by processRunViewModel.hasLoop.observeAsState()

    val numberOfPreBeeps by settingsViewModel.numberOfPreBeeps.observeAsState()

    processRunViewModel.initialiseRun(processId, numberOfPreBeeps ?: 0)

    if (keepScreenOn == true) {
        Log.d("ProcessRunScreen", "Will keep screen on")
        KeepScreenOn()
    }

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // first, a nice process indicator (if possible)
                if (hasLoop != true) {
                    val currentProgress =
                        if (numberOfSteps != null && numberOfSteps != 0) currentStepNumber!!.toFloat() / numberOfSteps!! else 0.0f
                    LinearProgressIndicator(
                        progress = currentProgress,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // show the display, depending on where we are right now
                when (displayAction) {
                    is ProcessLeadInDisplayStepAction -> {
                        // TODO
                        val plAction = (displayAction as ProcessLeadInDisplayStepAction)
                        Text(text = plAction.processName + " - lead-in")
                        BigTimerText(duration = plAction.currentLeadInTime.seconds)
                    }

                    is ProcessDisplayStepAction -> {
                        // TODO
                        val pdAction = (displayAction as ProcessDisplayStepAction)
                        Text(text = pdAction.processName + " - running")
                        BigTimerText(duration = pdAction.currentProcessTime.seconds)
                        MediumTimerAndIntervalText(duration = pdAction.currentIntervalTime.seconds, intervalText = pdAction.currentRound.toString())
                    }

                    is ProcessPauseDisplayStepAction -> {
                        // TODO
                        val ppAction = (displayAction as ProcessPauseDisplayStepAction)
                        Text(text = ppAction.processName + " - pausing")
                        BigTimerText(duration = ppAction.currentPauseTime.seconds)
                    }

                    else -> {
                        // nothing to do for us
                    }
                }
            }
        },
        bottomBar = {
            FotoTimerRunBottomBar(navigator = navigator) { processRunViewModel.cancel() }
        }
    )
}

@Composable
fun FotoTimerRunBottomBar(navigator: DestinationsNavigator, cancelAction: () -> Unit) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    cancelAction()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Stop"
                )
            }
        }
    )
}
