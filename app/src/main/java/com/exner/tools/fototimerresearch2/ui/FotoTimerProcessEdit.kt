package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

lateinit var spViewModel: FotoTimerSingleProcessViewModel

@Composable
fun FotoTimerProcessEdit(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    singleprocessViewModel: FotoTimerSingleProcessViewModel,
    processId: String?,
    modifier: Modifier = Modifier
) {
    spViewModel = singleprocessViewModel

    // read the process, if it exists
    val uid = processId?.toLong() ?: -1
    var tmpProcess: FotoTimerProcess? = fotoTimerProcessViewModel.getProcessById(uid)

    // do we need to build one?
    if (null == tmpProcess) {
        // build one
        val context = LocalContext.current
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
        tmpProcess = FotoTimerProcess(
            "New Process",
            sharedSettings.getLong("preference_process_time", 30L),
            sharedSettings.getLong("preference_interval_time", 10L),
            false,
            1,
            false,
            2,
            false,
            3,
            hasSoundMetronome = false,
            hasLeadIn = false,
            leadInSeconds = sharedSettings.getInt("preference_lead_in_time", 5),
            hasAutoChain = false,
            hasPauseBeforeChain = false,
            pauseTime = sharedSettings.getInt("preference_pause_time", 0),
            gotoId = -1L
        )
    }
    // let's use that fancy ViewModel
    spViewModel.setVarsFromProcess(tmpProcess)

    // while we're here, let's get the list of all available processes for goto
    val processIdsAndNames = fotoTimerProcessViewModel.getIdsAndNamesOfAllProcesses()

    // OK, at this point we have a process, either existing, or fresh.
    // Now display the thing for editing
    FotoTimerProcessEditor(spViewModel, processIdsAndNames)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessEditor(
    processViewModel: FotoTimerSingleProcessViewModel,
    processIdsAndNames: List<FotoTimerProcessIdAndName>?
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // top - fields
        TextField(
            value = processViewModel.name,
            onValueChange = { processViewModel.name = it },
            label = { Text(text = "Process name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        HeaderText(text = "Times")
        TextField(
            value = processViewModel.processTime,
            onValueChange = { processViewModel.processTime = it },
            label = { Text(text = "Process time (total)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = processViewModel.intervalTime,
            onValueChange = { processViewModel.intervalTime = it },
            label = { Text(text = "Interval time") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        HeaderText(text = "Audio")
        Text(
            text = "Play sounds:",
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
        ) {
            Text(text = "At start", modifier = Modifier.align(Alignment.TopStart))
            Text(
                text = "each interval",
                modifier = Modifier.align(Alignment.TopCenter)
            )
            Text(
                text = "at the end",
                modifier = Modifier.align(Alignment.TopEnd)
            )
            Switch(
                checked = processViewModel.hasSoundStart,
                onCheckedChange = { processViewModel.hasSoundStart = it },
                modifier = Modifier.align(Alignment.BottomStart)
            )
            Switch(
                checked = processViewModel.hasSoundInterval,
                onCheckedChange = { processViewModel.hasSoundInterval = it },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            Switch(
                checked = processViewModel.hasSoundEnd,
                onCheckedChange = { processViewModel.hasSoundEnd = it },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Play a sound every second ('metronome')",
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Switch(
                checked = processViewModel.hasSoundMetronome,
                onCheckedChange = { processViewModel.hasSoundMetronome = it },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        HeaderText(text = "Before the process")
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Lead-in before process",
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Switch(
                checked = processViewModel.hasLeadIn,
                onCheckedChange = { processViewModel.hasLeadIn = it },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        if (processViewModel.hasLeadIn) {
            TextField(
                value = processViewModel.leadInSeconds,
                label = { Text(text = "Lead-in (seconds)") },
                onValueChange = { processViewModel.leadInSeconds = it },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        HeaderText(text = "After the process")
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Automatically start another process",
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Switch(
                checked = processViewModel.hasAutoChain,
                onCheckedChange = { processViewModel.hasAutoChain = it },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
        if (processViewModel.hasAutoChain) {
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {expanded = !expanded}) {
                TextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
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
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Pause before going to the next process",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Switch(
                    checked = processViewModel.hasPauseBeforeChain ?: false,
                    onCheckedChange = { processViewModel.hasPauseBeforeChain = it },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
            if (true == processViewModel.hasPauseBeforeChain) {
                TextField(
                    value = processViewModel.pauseTime,
                    label = { Text(text = "Pause (seconds)") },
                    onValueChange = { processViewModel.pauseTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        // middle and bottom - filler and save button
        Spacer(modifier = Modifier.weight(0.5f))
        Button(onClick = { /*TODO*/ }) {
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
@Composable
fun FTEPreview() {
    spViewModel.setVarsFromProcess(
        FotoTimerProcess(
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
        )
    }
}
