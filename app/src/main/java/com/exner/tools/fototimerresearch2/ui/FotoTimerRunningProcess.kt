package com.exner.tools.fototimerresearch2.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.model.FotoTimerCounterState
import com.exner.tools.fototimerresearch2.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import kotlin.time.Duration.Companion.seconds

@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel,
    modifier: Modifier = Modifier,
) {
    // this screen should stay visible, maybe
    if (runningProcessViewModel.keepsScreenOn) {
        KeepScreenOn()
    }
    // what's the orientation, right now?
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // show horizontally
            Row(
                modifier = Modifier.fillMaxSize().padding(8.dp)
            ) {
                Column() {
                    BigTimerText(
                        duration = runningProcessViewModel.elapsedIntervalTime,
                    )
                    Text(
                        text = "Interval Time (total ${runningProcessViewModel.intervalTime})",
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
                Spacer(modifier = Modifier.weight(0.001f))
            }
        }
        else -> {
            // show
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                HeaderText(text = runningProcessViewModel.processName)
                Divider(modifier = Modifier.padding(8.dp))
                // show time
                BigTimerText(
                    duration = runningProcessViewModel.elapsedIntervalTime,
                )
                Text(
                    text = "Interval Time (total ${runningProcessViewModel.intervalTime})",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.weight(0.001f))
                MediumTimerAndIntervalText(
                    duration = runningProcessViewModel.timeLeftUntilEndOfProcess.plus(1.seconds),
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
    }
}

@Preview(showBackground = true)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape")
@Composable
fun FTRPPreview() {
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
