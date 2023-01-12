package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

@Composable
fun ProcessAudioData(
    hasSoundStart: Boolean,
    hasSoundEnd: Boolean,
    hasSoundInterval: Boolean,
    hasSoundMetronome: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
    ) {
        if (hasSoundStart || hasSoundEnd || hasSoundInterval || hasSoundMetronome) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_music_note_24),
                contentDescription = "Process Sounds",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
            )
            Column() {
                var soundStatement = "Will play sound at "
                if (hasSoundStart && hasSoundEnd) {
                    soundStatement += "start and end of process."
                } else if (hasSoundStart) {
                    soundStatement += "start of process."
                } else if (hasSoundEnd) {
                    soundStatement += "end of process."
                }
                BodyText(
                    text = soundStatement
                )
                if (hasSoundInterval) {
                    BodyText(
                        text = "Will play sound at each interval."
                    )
                }
                if (hasSoundMetronome) {
                    BodyText(text = "Will tick every second ('metronome').")
                }
            }
        }
    }
}

@Composable
fun ProcessTimerData(
    processTime: Long,
    intervalTime: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_timer_24),
            contentDescription = "Process Times",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically)
        )
        Column() {
            BodyText(
                text = "The process runs for $processTime seconds, with an interval every $intervalTime seconds"
            )
        }
    }
}

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_start_24),
                contentDescription = "Process Start",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
            Column() {
                BodyText(text = "Before the process starts, there will be a count down for $leadInSeconds seconds.")
            }
        }
    }
    if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                contentDescription = "Process End",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically)
            )
            Column() {
                BodyText(text = "When the process is done, it will automatically lead into the next process: '$nextName'.")
                if (true == hasPauseBeforeChain && (null != pauseTime) && (0 < pauseTime)) {
                    BodyText(text = "There will be a pause of $pauseTime seconds before '$nextName' starts.")
                }
            }
        }
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
