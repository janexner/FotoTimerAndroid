package com.exner.tools.fototimer.ui

import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.preference.PreferenceManager
import com.exner.tools.fototimer.R
import com.exner.tools.fototimer.data.FotoTimerSampleProcess
import com.exner.tools.fototimer.data.model.FotoTimerCounterState
import com.exner.tools.fototimer.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessLauncherDestination
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FotoTimerProcessDetails(
    fotoTimerSingleProcessViewModel: FotoTimerSingleProcessViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    processId: Long,
) {
    val ftProcess = fotoTimerSingleProcessViewModel.getAsFotoTimerProcess()

    // lock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        // if this process auto chains, let's find the name of the next process, too
        val nextName =
            ftProcess.gotoId?.let { fotoTimerSingleProcessViewModel.getNameOfNextProcess() }
        ExistingProcessDetails(ftProcess, nextName)
        // bottom - start button
        Surface(modifier = Modifier.weight(0.25f)) {
            Button(
                onClick = {
                    navigator.navigate(
                        FotoTimerProcessLauncherDestination(
                            processId = processId,
                            nextState = FotoTimerCounterState.LEADIN,
                            pause = 0 // not needed here, so set to zero
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = true
            ) {
                ButtonText(text = "Start")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExistingProcessDetails(
    process: FotoTimerProcess,
    nextName: String?,
) {
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    val expertMode = sharedSettings.getBoolean("preference_expert_mode", false)
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        // top - process information
        ProcessName(process.name)
        Divider(modifier = Modifier.padding(8.dp))
        ProcessTimerData(
            process.processTime,
            process.intervalTime,
        )
        if (process.keepsScreenOn && expertMode) {
            ListItem(
                headlineText = { SmallBodyText(text = "UI") },
                supportingText = { BodyText(text = "Screen will stay on") },
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
        Spacer(modifier = Modifier)
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
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    val expertMode = sharedSettings.getBoolean("preference_expert_mode", false)
    if (expertMode) {
        if (hasSoundStart || hasSoundEnd || hasPreBeeps || hasSoundInterval || hasSoundMetronome) {
            var soundStatement = "Sounds: "
            var space = ""
            if (hasSoundStart) {
                soundStatement += "start"
                space = ", "
            }
            if (hasSoundInterval) {
                soundStatement += space + "interval"
                if (hasPreBeeps) {
                    soundStatement += " (with 'pre-beeps')"
                }
                space = ", "
            }
            if (hasSoundEnd) {
                soundStatement += space + "end"
                space = ", "
            }
            if (hasSoundMetronome) {
                soundStatement += space + "every second ('metronome')"
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
    } else { // not expert mode
        if (hasSoundStart || hasSoundEnd || hasPreBeeps || hasSoundInterval || hasSoundMetronome) {
            ListItem(
                headlineText = { SmallBodyText(text = "Sounds") },
                supportingText = { BodyText(text = "Sound is on") },
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
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    val expertMode = sharedSettings.getBoolean("preference_expert_mode", false)
    if (expertMode) {
        if (hasLeadIn && (null != leadInSeconds)) {
            ListItem(
                headlineText = { SmallBodyText(text = "Before") },
                supportingText = { BodyText(text = "There will be a $leadInSeconds second count down") },
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
                "Afterwards, '$nextName' will be started."
            if (true == hasPauseBeforeChain && (null != pauseTime) && (0 < pauseTime)) {
                doneText += " There will be a pause of $pauseTime seconds before that."
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
    } else {
        if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
            ListItem(
                headlineText = { SmallBodyText(text = "After") },
                supportingText = { BodyText(text = "'$nextName' will be started next") },
                leadingContent = {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_navigate_next_24),
                        contentDescription = "Process End",
                    )
                }
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PHONE
)
@Preview(
    showSystemUi = true,
    device = Devices.NEXUS_5
)
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
            nextName = "Next Sample",
        )
    }
}
