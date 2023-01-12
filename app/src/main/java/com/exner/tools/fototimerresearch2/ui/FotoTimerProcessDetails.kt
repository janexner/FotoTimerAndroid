package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
        ExistingProcessDetails(ftProcess)
    } else {
        Text(text = "This process does not exist!", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ExistingProcessDetails(process: FotoTimerProcess, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(8.dp)) {
        // top - process information
        ProcessName(process.name)
        Divider(modifier = Modifier.padding(8.dp))
        ProcessMainData(
            process.processTime,
            process.intervalTime,
            process.hasSoundStart,
            process.hasSoundEnd,
            process.hasSoundInterval,
        )
        // middle - spacer
        Spacer(modifier = Modifier.weight(0.5f))
        // bottom - start button
        FilledTonalButton(
            onClick = { /*TODO*/ }, modifier = Modifier
                .fillMaxWidth()
                .weight(0.2f),
            shape = RoundedCornerShape(16.dp)
        ) {
            HeaderText(text = "Start Process")
        }
    }
}

@Composable
fun ProcessName(name: String, modifier: Modifier = Modifier) {
    HeaderText(text = name)
}

@Composable
fun ProcessMainData(
    processTime: Long,
    intervalTime: Long,
    hasSoundStart: Boolean,
    hasSoundEnd: Boolean,
    hasSoundInterval: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
    ) {
        if (hasSoundStart || hasSoundEnd || hasSoundInterval) {
            Icon(
                painterResource(id = R.drawable.ic_baseline_music_note_24),
                contentDescription = "Process Sounds",
                modifier = Modifier.padding(8.dp)
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
            }
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 0.dp, 0.dp, 8.dp)
    ) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_timer_24),
            contentDescription = "Process Times",
            modifier = Modifier.padding(8.dp)
        )
        BodyText(
            text = "Runs $processTime seconds, interval every $intervalTime seconds"
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
                false,
                false,
                5,
                false,
                false,
                0,
                -1L
            ), modifier = Modifier.fillMaxHeight()
        )
    }
}
