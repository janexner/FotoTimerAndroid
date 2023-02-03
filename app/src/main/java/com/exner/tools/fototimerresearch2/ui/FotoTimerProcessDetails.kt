package com.exner.tools.fototimerresearch2.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.R
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerProcessDetails(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    processId: String,
    onStartButtonClick: () -> Unit,
) {
    val uid = processId.toLong()
    val ftProcess = fotoTimerProcessViewModel.getProcessById(uid)

    // lock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    if (null != ftProcess) {
        // if this process auto chains, let's find the name of the next process, too
        val nextProcess = ftProcess.gotoId?.let { fotoTimerProcessViewModel.getProcessById(it) }
        var nextName: String? = null
        if (null != nextProcess) {
            nextName = nextProcess.name
        }
        ExistingProcessDetails(ftProcess, nextName, onStartButtonClick)
    } else {
        HeaderText(text = "This process does not exist!")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingProcessDetails(
    process: FotoTimerProcess,
    nextName: String?,
    onStartButtonClick: () -> Unit
) {
    Column(modifier = Modifier.padding(8.dp)) {
        // top - process information
        ProcessName(process.name)
        Divider(modifier = Modifier.padding(8.dp))
        ProcessTimerData(
            process.processTime,
            process.intervalTime,
        )
        if (process.keepsScreenOn) {
            ListItem(
                headlineText = { SmallBodyText(text = "UI") },
                supportingText = { BodyText(text = "Keeps screen on") },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.baseline_light_mode_24),
                        contentDescription = "UI",
                    )
                }
            )
        }
        ProcessAudioData(
            process.hasSoundStart,
            process.hasSoundEnd,
            process.hasSoundInterval,
            process.hasSoundMetronome,
            process.hasPreBeeps
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
            FilledTonalButton(
                onClick = onStartButtonClick,
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Start", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@Composable
fun ProcessName(name: String) {
    HeaderText(text = name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessAudioData(
    hasSoundStart: Boolean,
    hasSoundEnd: Boolean,
    hasSoundInterval: Boolean,
    hasSoundMetronome: Boolean,
    hasPreBeeps: Boolean
) {
    if (hasSoundStart || hasSoundEnd || hasPreBeeps || hasSoundInterval || hasSoundMetronome) {
        var soundStatement = ""
        var space = ""
        if (hasSoundStart || hasSoundEnd) {
            soundStatement = "Will play sound at "
            soundStatement += if (hasSoundStart && hasSoundEnd) {
                "start and end of process."
            } else if (hasSoundStart) {
                "start of process."
            } else { // hasSoundEnd == true here
                "end of process."
            }
            space = " "
        }
        if (hasSoundInterval) {
            soundStatement += space + "Will play sound at each interval."
            space = " "
        }
        if (hasPreBeeps) {
            soundStatement += space + "Will beep before the interval sound ('pre-beep')"
            space = " "
        }
        if (hasSoundMetronome) {
            soundStatement += space + "Will tick every second ('metronome')."
        }
        ListItem(
            headlineText = { SmallBodyText(text = "Sounds") },
            supportingText = { BodyText(text = soundStatement) },
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
    intervalTime: Long
) {
    ListItem(
        headlineText = { SmallBodyText(text = "Times") },
        supportingText = { BodyText(text = "The process runs for $processTime seconds, with an interval every $intervalTime seconds") },
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
    nextName: String?
) {
    if (hasLeadIn && (null != leadInSeconds)) {
        ListItem(
            headlineText = { SmallBodyText(text = "Before") },
            supportingText = { BodyText(text = "Before the process starts, there will be a $leadInSeconds second count down.") },
            leadingContent = {
                Icon(
                    painterResource(id = R.drawable.ic_baseline_start_24),
                    contentDescription = "Process Start",
                )
            }
        )
    }
    if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
        var doneText =
            "When the process is done, it will automatically lead into the next process: '$nextName'."
        if (true == hasPauseBeforeChain && (null != pauseTime) && (0 < pauseTime)) {
            doneText += " There will be a pause of $pauseTime seconds before '$nextName' starts."
        }
        ListItem(
            headlineText = { SmallBodyText(text = "After") },
            supportingText = { BodyText(text = doneText) },
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
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun FTPPreview() {
    FotoTimerTheme {
        ExistingProcessDetails(
            process = FotoTimerSampleProcess.getFotoTimerSampleProcess(
                name = "Sample Process",
                processTime = 75L,
                intervalTime = 25L,
                hasSoundStart = true,
                soundStartId = 1L,
                hasSoundEnd = true,
                soundEndId = 2L,
                hasSoundInterval = true,
                soundIntervalId = 3L,
                hasSoundMetronome = true,
                hasLeadIn = true,
                leadInSeconds = 5,
                hasAutoChain = true,
                hasPauseBeforeChain = true,
                pauseTime = 10,
                gotoId = 1L,
                keepsScreenOn = true,
                hasPreBeeps = true
            ),
            nextName = "Next Sample"
        ) {}
    }
}
