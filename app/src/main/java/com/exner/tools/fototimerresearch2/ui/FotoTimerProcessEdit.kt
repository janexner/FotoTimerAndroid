package com.exner.tools.fototimerresearch2.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessListViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator

lateinit var ftpViewModel: FotoTimerProcessListViewModel
lateinit var spViewModel: FotoTimerSingleProcessViewModel

@Destination
@Composable
fun FotoTimerProcessEdit(
    processId: Long? = -1,
    viewModel: FotoTimerSingleProcessViewModel = hiltViewModel(),
    fotoTimerProcessListViewModel: FotoTimerProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    // set them view models
    ftpViewModel = fotoTimerProcessListViewModel
    spViewModel = viewModel

    // read the process, if it exists
    val uid = processId?.toLong() ?: -1
    val tmpProcess: FotoTimerProcess? = ftpViewModel.getProcessById(uid)
    val thisProcessIsNew = null == tmpProcess

    // while we're here, let's get the list of all available processes for goto
    val processIdsAndNames = ftpViewModel.getIdsAndNamesOfAllProcesses()

    // OK, at this point we have a process, either existing, or fresh.
    // Now display the thing for editing
    FotoTimerProcessEditor(spViewModel, processIdsAndNames, navigator, thisProcessIsNew)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessEditor(
    processViewModel: FotoTimerSingleProcessViewModel,
    processIdsAndNames: List<FotoTimerProcessIdAndName>?,
    navigator: DestinationsNavigator,
    isThisNewProcess: Boolean
) {
    var nextProcessName: String by remember { mutableStateOf("") }
    if ((null != processViewModel.gotoId) && (-1L != processViewModel.gotoId)) {
        // loop through and assign the right name
        processIdsAndNames?.forEach {
            if (it.uid == processViewModel.gotoId) {
                nextProcessName = it.name
            }
        }
        // if gotoId not found, set it to -1
        if ("" == nextProcessName) {
            processViewModel.gotoId = -1
        }
    }
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    val expertMode = sharedSettings.getBoolean("preference_expert_mode", false)
    val keepScreenOn = sharedSettings.getBoolean("preference_screen_on", true)
    var modified by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // top - fields
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = processViewModel.name,
                onValueChange = {
                    processViewModel.name = it
                    modified = true
                },
                label = { Text(text = "Process name") },
                singleLine = true,
                modifier = Modifier.weight(0.75f)
            )
            if (!isThisNewProcess) {
                Spacer(modifier = Modifier.weight(0.05f))
                FilledTonalButton(
                    onClick = {
                        val process = processViewModel.getAsFotoTimerProcess()
                        ftpViewModel.update(process) // this button only for edit
                        modified = false
                    },
                    enabled = modified,
                    modifier = Modifier.width(100.dp)
                ) {
                    Text(
                        text = "Save",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        HeaderText(text = "Times")
        TextFieldForTimes(
            value = processViewModel.processTime,
            label = { Text(text = "Process time (total)") },
            onValueChange = {
                processViewModel.processTime = it
                modified = true
            },
        )
        TextFieldForTimes(
            value = processViewModel.intervalTime,
            label = { Text(text = "Interval time") },
            onValueChange = {
                processViewModel.intervalTime = it
                modified = true
            },
        )
        HeaderText(text = "During the process")
        if (expertMode || !keepScreenOn) {
            TextAndSwitch(
                text = "Keep the screen on",
                checked = processViewModel.keepsScreenOn,
            ) {
                processViewModel.keepsScreenOn = it
                modified = true
            }
        }
        if (expertMode) {
            Text(
                text = "Play sounds:",
                modifier = Modifier.fillMaxWidth()
            )
            TextAndSwitch(
                text = "At start",
                checked = processViewModel.hasSoundStart
            ) {
                processViewModel.hasSoundStart = it
                modified = true
            }
            TextAndSwitch(
                text = "Before each interval ('pre-beeps')",
                checked = processViewModel.hasPreBeeps,
            ) {
                processViewModel.hasPreBeeps = it
                modified = true
            }
            TextAndSwitch(
                text = "At each interval",
                checked = processViewModel.hasSoundInterval,
            ) {
                processViewModel.hasSoundInterval = it
                modified = true
            }
            TextAndSwitch(
                text = "At the end",
                checked = processViewModel.hasSoundEnd,
            ) {
                processViewModel.hasSoundEnd = it
                modified = true
            }
        } else {
            TextAndSwitch(
                text = "Play sounds at start, intervals, and end",
                checked = processViewModel.hasSoundStart && processViewModel.hasSoundInterval && processViewModel.hasSoundEnd
            ) {
                processViewModel.hasSoundStart = it
                processViewModel.hasSoundInterval = it
                processViewModel.hasSoundEnd = it
                modified = true
            }
        }
        AnimatedVisibility(visible = expertMode) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextAndSwitch(
                    text = "Play a sound every second ('metronome')",
                    checked = processViewModel.hasSoundMetronome,
                ) {
                    processViewModel.hasSoundMetronome = it
                    modified = true
                }
                HeaderText(text = "Before the process")
                TextAndSwitch(
                    text = "Lead-in before process",
                    checked = processViewModel.hasLeadIn,
                ) {
                    processViewModel.hasLeadIn = it
                    modified = true
                }
                AnimatedVisibility(visible = processViewModel.hasLeadIn) {
                    OutlinedTextField(
                        value = processViewModel.leadInSeconds,
                        label = { Text(text = "Lead-in (seconds)") },
                        onValueChange = {
                            processViewModel.leadInSeconds = it
                            modified = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        }
        HeaderText(text = "After the process")
        TextAndSwitch(
            text = "Automatically start another process",
            checked = processViewModel.hasAutoChain,
        ) {
            processViewModel.hasAutoChain = it
            modified = true
        }
        AnimatedVisibility(visible = processViewModel.hasAutoChain) {
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
                        value = nextProcessName,
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
                                    processViewModel.gotoId = idAndName.uid
                                    nextProcessName = idAndName.name
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
                    checked = processViewModel.hasPauseBeforeChain ?: false,
                ) {
                    processViewModel.hasPauseBeforeChain = it
                    modified = true
                }
                AnimatedVisibility(visible = true == processViewModel.hasPauseBeforeChain) {
                    TextFieldForTimes(
                        value = processViewModel.pauseTime,
                        label = { Text(text = "Pause (seconds)") },
                        onValueChange = {
                            processViewModel.pauseTime = it
                            modified = true
                        },
                    )
                }
            }
        }
        // middle and bottom - filler and save button
        Spacer(modifier = Modifier.weight(0.5f))
        FilledTonalButton(onClick = {
            val process = processViewModel.getAsFotoTimerProcess()
            if (isThisNewProcess) {
                ftpViewModel.insert(process)
            } else {
                ftpViewModel.update(process)
            }
            // navigate back
            navigator.popBackStack()
        }) {
            ButtonText(
                text = "Save Process",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun FTEPreview() {
    val p1 = FotoTimerProcessIdAndName(1, "Process 1")
    val p2 = FotoTimerProcessIdAndName(2, "Process 2")
    val processIdsAndNames: MutableList<FotoTimerProcessIdAndName> =
        mutableListOf()
    processIdsAndNames.add(p1)
    processIdsAndNames.add(p2)
    FotoTimerTheme {
        FotoTimerProcessEditor(
            processViewModel = spViewModel,
            processIdsAndNames = processIdsAndNames,
            navigator = EmptyDestinationsNavigator,
            isThisNewProcess = false
        )
    }
}
