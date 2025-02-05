package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.R
import com.exner.tools.fototimer.ui.BodyText
import com.exner.tools.fototimer.ui.HeaderText
import com.exner.tools.fototimer.ui.ProcessDetailsViewModel
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.exner.tools.fototimer.ui.SmallBodyText
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ProcessDeleteDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessEditDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>
@Composable
fun ProcessDetails(
    processId: Long,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val processDetailsViewModel =
        hiltViewModel<ProcessDetailsViewModel, ProcessDetailsViewModel.ProcessDetailsViewModelFactory> { factory ->
            factory.create(processId = processId)
        }

    val name by processDetailsViewModel.name.observeAsState()
    val processTime by processDetailsViewModel.processTime.observeAsState()
    val intervalTime by processDetailsViewModel.intervalTime.observeAsState()
    val hasSoundStart by processDetailsViewModel.hasSoundStart.observeAsState()
    val hasSoundEnd by processDetailsViewModel.hasSoundEnd.observeAsState()
    val hasSoundInterval by processDetailsViewModel.hasSoundInterval.observeAsState()
    val hasSoundMetronome by processDetailsViewModel.hasSoundMetronome.observeAsState()
    val hasPreBeeps by processDetailsViewModel.hasPreBeeps.observeAsState()
    val hasLeadIn by processDetailsViewModel.hasLeadIn.observeAsState()
    val leadInSeconds by processDetailsViewModel.leadInSeconds.observeAsState()
    val hasLeadInSound by processDetailsViewModel.hasLeadInSound.observeAsState()
    val hasAutoChain by processDetailsViewModel.hasAutoChain.observeAsState()
    val hasPauseBeforeChain by processDetailsViewModel.hasPauseBeforeChain.observeAsState()
    val pauseTime by processDetailsViewModel.pauseTime.observeAsState()
    val gotoId by processDetailsViewModel.gotoId.observeAsState()
    // this one is the odd one out
    val nextProcessesName by processDetailsViewModel.nextProcessesName.observeAsState()

    val expertMode by settingsViewModel.expertMode.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.imePadding(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // top - process information
                ProcessName(name, modifier = Modifier.padding(8.dp))
                HorizontalDivider(modifier = Modifier.padding(8.dp))
                ProcessTimerData(
                    processTime,
                    intervalTime,
                )
                ProcessAudioData(
                    hasSoundStart,
                    hasSoundEnd,
                    hasSoundInterval,
                    hasSoundMetronome,
                    hasPreBeeps,
                    hasLeadInSound,
                    expertMode
                )
                ProcessLeadInAndChainData(
                    hasLeadIn,
                    leadInSeconds,
                    hasAutoChain,
                    hasPauseBeforeChain,
                    pauseTime,
                    gotoId,
                    nextProcessesName,
                    expertMode
                )
                // middle - spacer
                Spacer(modifier = Modifier)
            }
        },
        bottomBar = {
            FotoTimerDetailsBottomBar(processId = processId, navigator = navigator)
        }
    )
}

@Composable
fun FotoTimerDetailsBottomBar(
    processId: Long,
    navigator: DestinationsNavigator
) {
    BottomAppBar(
        actions = {

            IconButton(onClick = {
                navigator.navigate(
                    ProcessEditDestination(processId = processId)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit"
                )
            }

            IconButton(onClick = {
                navigator.navigate(
                    ProcessDeleteDestination(processId = processId)
                )
            }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete"
                )
            }

        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Start") },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Start"
                    )
                },
                onClick = {
                    navigator.navigate(
                        ProcessRunDestination(processId = processId)
                    )
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}

@Composable
fun ProcessName(name: String?, modifier: Modifier) {
    HeaderText(
        text = name ?: "Name",
        modifier = modifier
    )
}

@Composable
fun ProcessAudioData(
    hasSoundStart: Boolean?,
    hasSoundEnd: Boolean?,
    hasSoundInterval: Boolean?,
    hasSoundMetronome: Boolean?,
    hasPreBeeps: Boolean?,
    hasLeadInSound: Boolean?,
    expertMode: Boolean?
) {
    if (expertMode == true) {
        if (hasSoundStart == true || hasSoundEnd == true || hasPreBeeps == true || hasSoundInterval == true || hasSoundMetronome == true || hasLeadInSound == true) {
            var soundStatement = "Sounds: "
            var space = ""
            if (hasLeadInSound == true) {
                soundStatement += space + "lead-in"
                space = ", "
            }
            if (hasSoundStart == true) {
                soundStatement += space + "start"
                space = ", "
            }
            if (hasSoundInterval == true) {
                soundStatement += space + "interval"
                if (hasPreBeeps == true) {
                    soundStatement += " (with 'pre-beeps')"
                }
                space = ", "
            }
            if (hasSoundEnd == true) {
                soundStatement += space + "end"
                space = ", "
            }
            if (hasSoundMetronome == true) {
                soundStatement += space + "every second ('metronome')"
            }
            ListItem(
                headlineContent = { SmallBodyText(text = "Sounds") },
                supportingContent = { BodyText(text = soundStatement) },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_music_note_24),
                        contentDescription = "Process Sounds",
                    )
                }
            )
        }
    } else { // not expert mode
        if (hasSoundStart == true || hasSoundEnd == true || hasPreBeeps == true || hasSoundInterval == true || hasSoundMetronome == true || hasLeadInSound == true) {
            ListItem(
                headlineContent = { SmallBodyText(text = "Sounds") },
                supportingContent = { BodyText(text = "Sound is on") },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_music_note_24),
                        contentDescription = "Process Sounds",
                    )
                }
            )
        }
    }
}

@Composable
fun ProcessTimerData(
    processTime: String?,
    intervalTime: String?
) {
    ListItem(
        headlineContent = { SmallBodyText(text = "Times") },
        supportingContent = { BodyText(text = "The process runs for $processTime seconds, with an interval every $intervalTime seconds") },
        leadingContent = {
            Icon(
                painterResource(id = R.drawable.ic_baseline_timer_24),
                contentDescription = "Process Times",
            )
        }
    )
}

@Composable
fun ProcessLeadInAndChainData(
    hasLeadIn: Boolean?,
    leadInSeconds: String?,
    hasAutoChain: Boolean?,
    hasPauseBeforeChain: Boolean?,
    pauseTime: String?,
    gotoId: Long?,
    nextName: String?,
    expertMode: Boolean?
) {
    if (expertMode == true) {
        if (hasLeadIn == true && (null != leadInSeconds)) {
            ListItem(
                headlineContent = { SmallBodyText(text = "Before") },
                supportingContent = { BodyText(text = "There will be a $leadInSeconds second count down") },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_start_24),
                        contentDescription = "Process Start",
                    )
                }
            )
        }
        if (hasAutoChain == true && (null != gotoId) && (-1L < gotoId)) {
            if (null != nextName) {
                var doneText =
                    "Afterwards, '$nextName' will be started."
                if (true == hasPauseBeforeChain && (null != pauseTime) && ("0" != pauseTime)) {
                    doneText += " There will be a pause of $pauseTime seconds before that."
                }
                ListItem(
                    headlineContent = { SmallBodyText(text = "After") },
                    supportingContent = { BodyText(text = doneText) },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                            contentDescription = "Process End",
                        )
                    }
                )
            } else {
                ListItem(
                    headlineContent = { SmallBodyText(text = "After") },
                    supportingContent = { BodyText(text = "This process chains into a process that does not exist!") },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.baseline_error_24),
                            contentDescription = "Problem"
                        )
                    }
                )
            }
        }
    } else {
        if (hasAutoChain == true && (null != gotoId) && (-1L < gotoId)) {
            if (null != nextName) {
                ListItem(
                    headlineContent = { SmallBodyText(text = "After") },
                    supportingContent = { BodyText(text = "'$nextName' will be started next") },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                            contentDescription = "Process End",
                        )
                    }
                )
            } else {
                ListItem(
                    headlineContent = { SmallBodyText(text = "After") },
                    supportingContent = { BodyText(text = "This process chains into a process that does not exist!") },
                    leadingContent = {
                        Icon(
                            painterResource(id = R.drawable.baseline_error_24),
                            contentDescription = "Problem"
                        )
                    }
                )
            }
        }
    }
}
