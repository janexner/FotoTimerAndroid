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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerRunningProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerRunningProcess(
    runningProcessViewModel: FotoTimerRunningProcessViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    // show
    Column(modifier = Modifier.fillMaxSize()) {
        HeaderText(text = runningProcessViewModel.processName)
        Divider(modifier = Modifier.padding(8.dp))
        // show time
        val processElapsedTime = runningProcessViewModel.elapsedProcessTime
        BigTimerText(
            seconds = processElapsedTime,
            modifier = Modifier.fillMaxWidth()
        )
        // show additional information (next process(es))
        Spacer(modifier = Modifier.weight(0.5f))
        // show huge cancel button
        Button(
            onClick = { runningProcessViewModel.cancelRunner() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
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
                )
            ),
            modifier = Modifier.fillMaxHeight()
        )
    }
}
