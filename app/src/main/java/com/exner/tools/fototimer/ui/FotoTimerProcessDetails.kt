package com.exner.tools.fototimer.ui

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.exner.tools.fototimer.data.model.FotoTimerProcessListViewModel
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.running.FotoTimerProcessStepAction
import com.exner.tools.fototimer.data.running.getAsFTProcessStepList
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

const val TAG = "FTPDetails"

@SuppressLint("CoroutineCreationDuringComposition")
@Destination
@Composable
fun FotoTimerProcessDetails(
    processId: Long = -1,
    fotoTimerProcessListViewModel: FotoTimerProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    // read the process, if it exists
    val uid = processId
    val ftProcess: FotoTimerProcess? = fotoTimerProcessListViewModel.getProcessById(uid)
    // while we're here, let's get the list of all available processes for goto
    val processIdsAndNames = fotoTimerProcessListViewModel.getIdsAndNamesOfAllProcesses()
    val fotoTimerRepository = fotoTimerProcessListViewModel.getRepository()

    // create actionslist list
    val coroutineScope = rememberCoroutineScope()
    var actionsListList: List<List<FotoTimerProcessStepAction>>
    coroutineScope.launch {
        actionsListList = getAsFTProcessStepList(fotoTimerRepository, uid)
        Log.i(TAG, "actionsListList created: $actionsListList")
    }

    // lock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        // if this process auto chains, let's find the name of the next process, too
        var nextName: String? = null
        if (ftProcess !== null) {
            if (ftProcess.gotoId !== null) {
                processIdsAndNames.forEach { tupel ->
                    if (tupel.uid == ftProcess.gotoId) {
                        nextName = tupel.name
                    }
                }
            }
            ExistingProcessDetails(ftProcess, nextName)
        }

        // bottom - start button
        Surface(modifier = Modifier.weight(0.25f)) {
            Button(
                onClick = {
                          // TODO
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = false // only active once the steps list is ready
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
                headlineContent = { SmallBodyText(text = "UI") },
                supportingContent = { BodyText(text = "Screen will stay on") },
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
        if (hasSoundStart || hasSoundEnd || hasPreBeeps || hasSoundInterval || hasSoundMetronome) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessTimerData(
    processTime: Int,
    intervalTime: Int
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
        if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
            var doneText =
                "Afterwards, '$nextName' will be started."
            if (true == hasPauseBeforeChain && (null != pauseTime) && (0 < pauseTime)) {
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
        }
    } else {
        if (hasAutoChain && (null != gotoId) && (-1L < gotoId) && (null != nextName)) {
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
                processTime = 75,
                intervalTime = 25,
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
