package com.exner.tools.fototimer.ui.destinations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.ui.HeaderText
import com.exner.tools.fototimer.ui.ProcessEditViewModel
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.exner.tools.fototimer.ui.TextAndSwitch
import com.exner.tools.fototimer.ui.TextFieldForTimes
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun ProcessEdit(
    processId: Long,
    processEditViewModel: ProcessEditViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {

    val name by processEditViewModel.name.observeAsState()
    val processTime by processEditViewModel.processTime.observeAsState()
    val intervalTime by processEditViewModel.intervalTime.observeAsState()
    val hasSoundStart by processEditViewModel.hasSoundStart.observeAsState()
    val hasSoundEnd by processEditViewModel.hasSoundEnd.observeAsState()
    val hasSoundInterval by processEditViewModel.hasSoundInterval.observeAsState()
    val hasSoundMetronome by processEditViewModel.hasSoundMetronome.observeAsState()
    val hasPreBeeps by processEditViewModel.hasPreBeeps.observeAsState()
    val hasLeadIn by processEditViewModel.hasLeadIn.observeAsState()
    val leadInSeconds by processEditViewModel.leadInSeconds.observeAsState()
    val hasLeadInSound by processEditViewModel.hasLeadInSound.observeAsState()
    val hasAutoChain by processEditViewModel.hasAutoChain.observeAsState()
    val hasPauseBeforeChain by processEditViewModel.hasPauseBeforeChain.observeAsState()
    val pauseTime by processEditViewModel.pauseTime.observeAsState()
    // some odd ones out
    val nextProcessesName by processEditViewModel.nextProcessesName.observeAsState()
    val processIdsAndNames by processEditViewModel.processIdsAndNames.observeAsState()

    processEditViewModel.getProcess(processId)
    processEditViewModel.getProcessIdsAndNames()

    var modified by remember { mutableStateOf(false) }

    val expertMode by settingsViewModel.expertMode.collectAsStateWithLifecycle()

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .wrapContentHeight()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // top - fields
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = name ?: "Name",
                        onValueChange = {
                            processEditViewModel.updateName(it)
                            modified = true
                        },
                        label = { Text(text = "Process name") },
                        singleLine = true,
                        modifier = Modifier.weight(0.75f)
                    )
                }
                HeaderText(text = "Times")
                TextFieldForTimes(
                    value = processTime ?: 30,
                    label = { Text(text = "Process time (total)") },
                    onValueChange = {
                        processEditViewModel.updateProcessTime(it)
                        modified = true
                    },
                )
                TextFieldForTimes(
                    value = intervalTime ?: 10,
                    label = { Text(text = "Interval time") },
                    onValueChange = {
                        processEditViewModel.updateIntervalTime(it)
                        modified = true
                    },
                )
                HeaderText(text = "During the process")
                if (expertMode) {
                    Text(
                        text = "Play sounds:",
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextAndSwitch(
                        text = "At start",
                        checked = hasSoundStart == true
                    ) {
                        processEditViewModel.updateHasSoundStart(it)
                        modified = true
                    }
                    TextAndSwitch(
                        text = "Before each interval ('pre-beeps')",
                        checked = hasPreBeeps == true,
                    ) {
                        processEditViewModel.updateHasPreBeeps(it)
                        modified = true
                    }
                    TextAndSwitch(
                        text = "At each interval",
                        checked = hasSoundInterval == true,
                    ) {
                        processEditViewModel.updateHasSoundInterval(it)
                        modified = true
                    }
                    TextAndSwitch(
                        text = "Every second ('metronome')",
                        checked = hasSoundMetronome == true,
                    ) {
                        processEditViewModel.updateHasSoundMetronome(it)
                        modified = true
                    }
                    TextAndSwitch(
                        text = "At the end",
                        checked = hasSoundEnd == true,
                    ) {
                        processEditViewModel.updateHasSoundEnd(it)
                        modified = true
                    }
                } else {
                    TextAndSwitch(
                        text = "Play sounds at start, intervals, and end",
                        checked = hasSoundStart == true && hasSoundInterval == true && hasSoundEnd == true
                    ) {
                        processEditViewModel.updateHasSoundStart(it)
                        processEditViewModel.updateHasSoundInterval(it)
                        processEditViewModel.updateHasSoundEnd(it)
                        modified = true
                    }
                }
                AnimatedVisibility(visible = expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HeaderText(text = "Before the process")
                        TextAndSwitch(
                            text = "Lead-in before process",
                            checked = hasLeadIn == true,
                        ) {
                            processEditViewModel.updateHasLeadIn(it)
                            modified = true
                        }
                        AnimatedVisibility(visible = hasLeadIn == true) {
                            TextFieldForTimes(
                                value = leadInSeconds ?: 5,
                                label = { Text(text = "Lead-in (seconds)") },
                                onValueChange = {
                                    processEditViewModel.updateLeadInSeconds(it)
                                    modified = true
                                }
                            )
                            TextAndSwitch(
                                text = "Play a sound during lead-in",
                                checked = hasLeadInSound == true
                            ) {
                                processEditViewModel.updateHasLeadInSound(it)
                                modified = true
                            }
                        }
                    }
                }
                HeaderText(text = "After the process")
                TextAndSwitch(
                    text = "Automatically start another process",
                    checked = hasAutoChain == true,
                ) {
                    processEditViewModel.updateHasAutoChain(it)
                    modified = true
                }
                AnimatedVisibility(visible = hasAutoChain == true) {
                    var expanded by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                // The `menuAnchor` modifier must be passed to the text field for correctness.
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                readOnly = true,
                                value = nextProcessesName ?: "",
                                placeholder = { Text("Select next Process") },
                                onValueChange = {},
                                label = { Text("Next Process") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }) {
                                processIdsAndNames?.forEach { idAndName ->
                                    DropdownMenuItem(
                                        text = { Text(text = idAndName.name) },
                                        onClick = {
                                            processEditViewModel.updateGotoId(idAndName.uid)
                                            processEditViewModel.updateNextProcessesName(idAndName.name)
                                            modified = true
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                    )
                                }
                            }
                        }
                        TextAndSwitch(
                            text = "Pause before going to the next process",
                            checked = hasPauseBeforeChain ?: false,
                        ) {
                            processEditViewModel.updateHasPauseBeforeChain(it)
                            modified = true
                        }
                        AnimatedVisibility(visible = true == hasPauseBeforeChain) {
                            TextFieldForTimes(
                                value = pauseTime ?: 5,
                                label = { Text(text = "Pause (seconds)") },
                                onValueChange = {
                                    processEditViewModel.updatePauseTime(it)
                                    modified = true
                                },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            FotoTimerEditBottomBar(navigator = navigator, commitProcess = {
                processEditViewModel.commitProcess()
            })
        }
    )
}

@Composable
fun FotoTimerEditBottomBar(navigator: DestinationsNavigator, commitProcess: () -> Unit) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Save") },
                icon = {
                    Icon(imageVector = Icons.Filled.Done, contentDescription = "Save the process")
                },
                onClick = {
                    commitProcess()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}
