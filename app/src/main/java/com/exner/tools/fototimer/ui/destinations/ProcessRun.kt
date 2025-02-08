package com.exner.tools.fototimer.ui.destinations

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.steps.ProcessDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessPauseDisplayStepAction
import com.exner.tools.fototimer.ui.BigTimerText
import com.exner.tools.fototimer.ui.KeepScreenOn
import com.exner.tools.fototimer.ui.MediumTimerAndIntervalText
import com.exner.tools.fototimer.ui.ProcessRunViewModel
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.time.Duration.Companion.seconds

@Destination<RootGraph>
@Composable
fun ProcessRun(
    processId: Long,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val processRunViewModel =
        hiltViewModel<ProcessRunViewModel, ProcessRunViewModel.ProcessRunViewModelFactory> { factory ->
            factory.create(processId = processId) { navigator.navigateUp() }
        }

    val displayAction by processRunViewModel.displayAction.observeAsState()
    val numberOfSteps by processRunViewModel.numberOfSteps.observeAsState()
    val currentStepNumber by processRunViewModel.currentStepNumber.observeAsState()
    val hasLoop by processRunViewModel.hasLoop.observeAsState()
    val hasHours by processRunViewModel.hasHours.observeAsState()

    val intervalTimeIsCentral by settingsViewModel.interValTimeIsCentral.collectAsStateWithLifecycle()

    val isPaused by processRunViewModel.isPaused.observeAsState()
    val maxJumpTarget by processRunViewModel.maxJumpTarget.observeAsState()

    // Keeping the screen on while counting, bcs otherwise the phone may switch off.
    // this is now default for everything.
    KeepScreenOn()

    Scaffold(
        modifier = Modifier.imePadding(),
        content = { innerPadding ->
            val configuration = LocalConfiguration.current
            Column(
                modifier = Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                // first, a nice process indicator (if possible)
                if (hasLoop != true) {
                    val currentProgress =
                        if (numberOfSteps != null && numberOfSteps != 0) currentStepNumber!!.toFloat() / numberOfSteps!! else 0.0f
                    LinearProgressIndicator(
                        progress = { currentProgress },
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // show the display, depending on where we are right now
                when (displayAction) {
                    is ProcessLeadInDisplayStepAction -> {
                        // TODO
                        val plAction = (displayAction as ProcessLeadInDisplayStepAction)
                        Text(text = plAction.processName + " | " + plAction.processParameters + " | lead-in")
                        BigTimerText(
                            duration = plAction.currentLeadInTime.seconds,
                            hasHours == true
                        )
                    }

                    is ProcessDisplayStepAction -> {
                        // TODO
                        val pdAction = (displayAction as ProcessDisplayStepAction)
                        Text(text = pdAction.processName + " | " + pdAction.processParameters + " | running")
                        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                BigTimerText(
                                    duration = if (intervalTimeIsCentral) pdAction.currentIntervalTime.seconds else pdAction.currentProcessTime.seconds,
                                    withHours = hasHours == true,
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .alignByBaseline()
                                )
                                MediumTimerAndIntervalText(
                                    duration = if (intervalTimeIsCentral) pdAction.currentProcessTime.seconds else pdAction.currentIntervalTime.seconds,
                                    withHours = hasHours == true,
                                    intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}",
                                    modifier = Modifier.alignByBaseline()
                                )
                            }
                        } else {
                            BigTimerText(
                                duration = if (intervalTimeIsCentral) pdAction.currentIntervalTime.seconds else pdAction.currentProcessTime.seconds,
                                withHours = hasHours == true
                            )
                            MediumTimerAndIntervalText(
                                duration = if (intervalTimeIsCentral) pdAction.currentProcessTime.seconds else pdAction.currentIntervalTime.seconds,
                                withHours = hasHours == true,
                                intervalText = "${pdAction.currentRound} of ${pdAction.totalRounds}"
                            )
                        }
                    }

                    is ProcessPauseDisplayStepAction -> {
                        // TODO
                        val ppAction = (displayAction as ProcessPauseDisplayStepAction)
                        Text(text = ppAction.processName + " | " + ppAction.processParameters + " | pausing")
                        BigTimerText(
                            duration = ppAction.currentPauseTime.seconds,
                            withHours = hasHours == true
                        )
                    }

                    else -> {
                        // nothing to do for us
                    }
                }

                Spacer(modifier = Modifier.weight(.5f))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val buttonColors = ButtonDefaults.buttonColors()
                    Button(
                        modifier = Modifier
                            .weight(45f)
                            .height(56.dp),
                        onClick = {
                            isPaused?.let { processRunViewModel.setPaused(it.not()) }
                        },
                        colors = if (isPaused == true) {
                            buttonColors.copy(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                        } else {
                            buttonColors
                        }
                    ) {
                        if (isPaused == true) {
                            Text(text = "Continue")
                        } else {
                            Text(text = "Pause")
                        }
                    }
                    if (currentStepNumber != null && maxJumpTarget != null) {
                        if (currentStepNumber!! < maxJumpTarget!!) {
                            Spacer(modifier = Modifier.weight(10f))
                            Button(
                                modifier = Modifier
                                    .weight(45f)
                                    .height(56.dp),
                                onClick = {
                                    processRunViewModel.goToNextProcess()
                                },
                            ) {
                                Text(text = "Next")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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
            ExtendedFloatingActionButton(
                text = { Text(text = "Stop") },
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Stop"
                    )
                },
                onClick = {
                    cancelAction()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            )
        }
    )
}
