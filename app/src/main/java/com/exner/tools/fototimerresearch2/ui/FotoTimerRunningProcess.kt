package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel,
    modifier: Modifier = Modifier,
) {
    // this screen should stay visible, maybe
    val context = LocalContext.current
    if (runningProcessViewModel.keepsScreenOn) {
        KeepScreenOn()
    }
    // show
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        HeaderText(text = runningProcessViewModel.processName)
        Divider(modifier = Modifier.padding(8.dp))
        // show time
        val processElapsedTime = runningProcessViewModel.elapsedProcessTime
        BigTimerText(
            milliSeconds = processElapsedTime,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        )
        Text(
            text = "Process Time (total ${runningProcessViewModel.processTime})",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(0.001f))
        val intervalElapsedTime = runningProcessViewModel.elapsedIntervalTime
        MediumTimerAndIntervalText(
            milliSeconds = intervalElapsedTime,
            intervalText = "${runningProcessViewModel.currentIntervalIndex + 1} of ${runningProcessViewModel.numberOfIntervals}",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
        )
        Text(
            text = "Interval Time (total ${runningProcessViewModel.intervalTime})",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End),
            style = MaterialTheme.typography.bodyLarge
        )
        // show additional information (next process(es))
        Spacer(modifier = Modifier.weight(0.01f))
        // show huge cancel button
        Button(
            onClick = { runningProcessViewModel.cancelRunner() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f),
            enabled = runningProcessViewModel.keepRunning
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.headlineLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FTRPreview() {
    FotoTimerTheme {
        FotoTimerRunningProcess(
            runningProcessViewModel =
            FotoTimerRunningProcessViewModel(
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
                    keepsScreenOn = true
                )
            ),
            modifier = Modifier.fillMaxHeight()
        )
    }
}
