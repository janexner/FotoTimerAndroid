package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerProcessDetails(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    processId: String,
    modifier: Modifier = Modifier
) {

    val uid = processId.toLong()
    val ftProcess = fotoTimerProcessViewModel.getProcessById(uid)

    if (null != ftProcess) {
        // if this process auto chains, let's find the name of the next process, too
        val nextProcess = ftProcess.gotoId?.let { fotoTimerProcessViewModel.getProcessById(it) }
        var nextName: String? = null
        if (null != nextProcess) {
            nextName = nextProcess.name
        }
        ExistingProcessDetails(ftProcess, nextName)
    } else {
        HeaderText(text = "This process does not exist!")
    }
}

@Composable
fun ExistingProcessDetails(
    process: FotoTimerProcess,
    nextName: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(8.dp)) {
        // top - process information
        ProcessName(process.name)
        Divider(modifier = Modifier.padding(8.dp))
        ProcessAudioData(
            process.hasSoundStart,
            process.hasSoundEnd,
            process.hasSoundInterval,
            process.hasSoundMetronome,
        )
        ProcessTimerData(
            process.processTime,
            process.intervalTime,
        )
        ProcessLeadInAndChainData(
            process.hasLeadIn,
            process.leadInSeconds,
            process.hasAutoChain,
            process.hasPauseBeforeChain,
            process.pauseTime,
            process.gotoId,
            nextName,
        )
        // middle - spacer
        Spacer(modifier = Modifier.weight(0.5f))
        // bottom - start button
        Surface(modifier = Modifier.weight(0.2f)) {
            ProcessStartButton()
        }
    }
}

@Composable
fun ProcessName(name: String, modifier: Modifier = Modifier) {
    HeaderText(text = name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessAudioData(
    hasSoundStart: Boolean,
    hasSoundEnd: Boolean,
    hasSoundInterval: Boolean,
    hasSoundMetronome: Boolean,
    modifier: Modifier = Modifier
) {
    if (hasSoundStart || hasSoundEnd || hasSoundInterval || hasSoundMetronome) {
        var soundStatement = ""
        var space = ""
        if (hasSoundStart || hasSoundEnd || hasSoundInterval) {
            var soundStatement = "Will play sound at "
            if (hasSoundStart && hasSoundEnd) {
                soundStatement += "start and end of process."
            } else if (hasSoundStart) {
                soundStatement += "start of process."
            } else if (hasSoundEnd) {
                soundStatement += "end of process."
            }
            space = " "
        }
        if (hasSoundInterval) {
            soundStatement += space + "Will play sound at each interval."
            space = " "
        }
        if (hasSoundMetronome) {
            soundStatement += space + "Will tick every second ('metronome')."
        }
        ListItem(
            headlineText = { Text(text = "Sound") },
            supportingText = { Text(text = soundStatement) },
            leadingContent = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_music_note_24),
                    contentDescription = "Process Sounds",
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessTimerData(
    processTime: Long,
    intervalTime: Long,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineText = { Text(text = "Time") },
        supportingText = { Text(text = "The process runs for $processTime seconds, with an interval every $intervalTime seconds") },
        leadingContent = {
            Icon(
                painterResource(id = R.drawable.ic_baseline_timer_24),
                contentDescription = "Process Times",
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessLeadInAndChainData(
    hasLeadIn: Boolean,
    leadInSeconds: Int?,
    hasAutoChain: Boolean,
    hasPauseBeforeChain: Boolean?,
    pauseTime: Int?,
    gotoId: Long?,
    nextName: String?,
    modifier: Modifier = Modifier
) {
    if (hasLeadIn && (null != leadInSeconds)) {
        ListItem(
            headlineText = { Text(text = "Before") },
            supportingText = { Text(text = "Before the process starts, there will be a $leadInSeconds second count down.") },
            leadingContent = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_start_24),
                    contentDescription = "Process Start",
                )
            }
        )
    }
    if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
        var doneText = "When the process is done, it will automatically lead into the next process: '$nextName'."
        if (true == hasPauseBeforeChain && (null != pauseTime) && (0 < pauseTime)) {
            doneText += " There will be a pause of $pauseTime seconds before '$nextName' starts."
        }
        ListItem(
            headlineText = { Text(text = "After") },
            supportingText = { Text(text = doneText) },
            leadingContent = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                    contentDescription = "Process End",
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FTPPreview() {
    FotoTimerTheme {
        ExistingProcessDetails(
            process = FotoTimerProcess(
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
            ),
            nextName = "Next Sample",
            modifier = Modifier.fillMaxHeight()
        )
    }
}
