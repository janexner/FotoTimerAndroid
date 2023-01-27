package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.model.FotoTimerCounterState
import com.exner.tools.fototimerresearch2.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel,
    modifier: Modifier = Modifier,
) {
    // this screen should stay visible, maybe
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
        val intervalElapsedTime = runningProcessViewModel.elapsedIntervalTime
        val processRemainingTime = runningProcessViewModel.timeLeftUntilEndOfProcess
        BigTimerText(
            duration = intervalElapsedTime,
        )
        Text(
            text = "Interval Time (total ${runningProcessViewModel.intervalTime})",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.weight(0.001f))
        MediumTimerAndIntervalText(
            duration = processRemainingTime,
            intervalText = "${runningProcessViewModel.counterState.roundNumber} of ${runningProcessViewModel.numberOfIntervals}",
        )
        Text(
            text = "Process Time (total ${runningProcessViewModel.processTime})",
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
            enabled = runningProcessViewModel.counterState.state == FotoTimerCounterState.COUNTING
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
                process = FotoTimerSampleProcess.getFotoTimerSampleProcess(
                    "Sample Process",
                )
            ),
            modifier = Modifier.fillMaxHeight()
        )
    }
}
