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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

lateinit var ftpViewModel: FotoTimerProcessViewModel
lateinit var spViewModel: FotoTimerSingleProcessViewModel

@Composable
fun FotoTimerProcessEdit(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    singleProcessViewModel: FotoTimerSingleProcessViewModel,
    processId: String?,
    modifier: Modifier = Modifier,
    onSaveClicked: (Unit) -> Boolean
) {
    ftpViewModel = fotoTimerProcessViewModel
    spViewModel = singleProcessViewModel

    // read the process, if it exists
    val uid = processId?.toLong() ?: -1
    var tmpProcess: FotoTimerProcess? = fotoTimerProcessViewModel.getProcessById(uid)

    // do we need to build one?
    if (null == tmpProcess) {
        // build one
        val context = LocalContext.current
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
        tmpProcess = FotoTimerSampleProcess.getFotoTimerSampleProcess(
            "New Process",
            sharedSettings.getLong("preference_process_time", 30L),
            sharedSettings.getLong("preference_interval_time", 10L),
            false,
            1,
            false,
            2,
            false,
            3,
            leadInSeconds = sharedSettings.getInt("preference_lead_in_time", 5),
            pauseTime = sharedSettings.getInt("preference_pause_time", 0),
            keepsScreenOn = sharedSettings.getBoolean("preference_screen_on", true)
        )
    }
    // let's use that fancy ViewModel
    spViewModel.setVarsFromProcess(tmpProcess)

    // while we're here, let's get the list of all available processes for goto
    val processIdsAndNames = fotoTimerProcessViewModel.getIdsAndNamesOfAllProcesses()

    // OK, at this point we have a process, either existing, or fresh.
    // Now display the thing for editing
    FotoTimerProcessEditor(spViewModel, processIdsAndNames, onSaveClicked)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessEditor(
    processViewModel: FotoTimerSingleProcessViewModel,
    processIdsAndNames: List<FotoTimerProcessIdAndName>?,
    onSaveClicked: (Unit) -> Boolean
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
            Spacer(modifier = Modifier.weight(0.05f))
            Button(
                onClick = {
                    val process = spViewModel.getAsFotoTimerProcess()
                    if (0L == process.uid) {
                        ftpViewModel.insert(process)
                    } else {
                        ftpViewModel.update(process)
                    }
                    modified = false
                },
                enabled = modified,
                modifier = Modifier.width(100.dp)
            ) {
                Text(
                    text = "Save",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
        HeaderText(text = "Times")
        TextFieldForTimes(
            value = processViewModel.processTime,
            onValueChange = {
                processViewModel.processTime = it
                modified = true
            },
            label = { Text(text = "Process time (total)") },
        )
        TextFieldForTimes(
            value = processViewModel.intervalTime,
            onValueChange = {
                processViewModel.intervalTime = it
                modified = true
            },
            label = { Text(text = "Interval time") },
        )
        HeaderText(text = "During the process")
        TextAndSwitch(
            text = "Keep the screen on",
            checked = processViewModel.keepsScreenOn,
            onCheckedChange = {
                processViewModel.keepsScreenOn = it
                modified = true
            },
        )
        Text(
            text = "Play sounds:",
            modifier = Modifier.fillMaxWidth()
        )
        if (expertMode) {
            TextAndSwitch(
                text = "At start",
                checked = processViewModel.hasSoundStart,
                onCheckedChange = {
                    processViewModel.hasSoundStart = it
                    modified = true
                }
            )
            TextAndSwitch(
                text = "Before each interval ('pre-beeps')",
                checked = processViewModel.hasPreBeeps,
                onCheckedChange = {
                    processViewModel.hasPreBeeps = it
                    modified = true
                },
            )
            TextAndSwitch(
                text = "At each interval",
                checked = processViewModel.hasSoundInterval,
                onCheckedChange = {
                    processViewModel.hasSoundInterval = it
                    modified = true
                },
            )
            TextAndSwitch(
                text = "At the end",
                checked = processViewModel.hasSoundEnd,
                onCheckedChange = {
                    processViewModel.hasSoundEnd = it
                    modified = true
                },
            )
        } else {
            TextAndSwitch(
                text = "At start, intervals, and end",
                checked = processViewModel.hasSoundStart && processViewModel.hasSoundInterval && processViewModel.hasSoundEnd,
                onCheckedChange = {
                    processViewModel.hasSoundStart = it
                    processViewModel.hasSoundInterval = it
                    processViewModel.hasSoundEnd = it
                    modified = true
                }
            )
        }
        AnimatedVisibility(visible = expertMode) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextAndSwitch(
                    text = "Play a sound every second ('metronome')",
                    checked = processViewModel.hasSoundMetronome,
                    onCheckedChange = {
                        processViewModel.hasSoundMetronome = it
                        modified = true
                    },
                )
                HeaderText(text = "Before the process")
                TextAndSwitch(
                    text = "Lead-in before process",
                    checked = processViewModel.hasLeadIn,
                    onCheckedChange = {
                        processViewModel.hasLeadIn = it
                        modified = true
                    },
                )
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
            onCheckedChange = {
                processViewModel.hasAutoChain = it
                modified = true
            },
        )
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
                    onCheckedChange = {
                        processViewModel.hasPauseBeforeChain = it
                        modified = true
                    },
                )
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
        Button(onClick = {
            val process = spViewModel.getAsFotoTimerProcess()
            if (0L == process.uid) {
                ftpViewModel.insert(process)
            } else {
                ftpViewModel.update(process)
            }
            // navigate back
            onSaveClicked(Unit)
        }) {
            Text(
                text = "Save Process",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun FTEPreview() {
    spViewModel.setVarsFromProcess(
        FotoTimerSampleProcess.getFotoTimerSampleProcess(
            "Sample Process",
            30L,
            10L,
            true,
            1,
            true,
            2,
            true,
            3,
            hasSoundMetronome = true,
            hasLeadIn = true,
            leadInSeconds = 5,
            hasAutoChain = true,
            hasPauseBeforeChain = true,
            pauseTime = 3,
            gotoId = 3L,
            keepsScreenOn = true,
            hasPreBeeps = true,
        )
    )
    val p1 = FotoTimerProcessIdAndName(1, "Process 1")
    val p2 = FotoTimerProcessIdAndName(2, "Process 2")
    val processIdsAndNames: MutableList<FotoTimerProcessIdAndName> =
        mutableListOf()
    processIdsAndNames.add(p1)
    processIdsAndNames.add(p2)
    FotoTimerTheme {
        FotoTimerProcessEditor(
            processViewModel = spViewModel,
            processIdsAndNames = processIdsAndNames
        ) { true }
    }
}
