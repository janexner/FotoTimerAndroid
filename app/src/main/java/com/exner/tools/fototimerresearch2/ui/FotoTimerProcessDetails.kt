package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

@Composable
fun FotoTimerProcessDetails(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    processId: String,
    modifier: Modifier = Modifier
) {

    val uid = processId.toLong()
    val ftProcess = fotoTimerProcessViewModel.getProcessById(uid)

    if (null != ftProcess) {
        ExistingProcessDetails(ftProcess, Modifier.fillMaxHeight())
    } else {
        Text(text = "This process does not exist!", style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun ExistingProcessDetails(process: FotoTimerProcess, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxHeight()) {
        // top - process information
        ProcessName(process.name)
        Divider()
        ProcessMainData(
            process.processTime,
            process.intervalTime,
            process.hasSoundStart,
            process.hasSoundEnd,
            process.hasSoundInterval,
        )
        // middle - spacer
        Spacer(modifier = Modifier.fillMaxHeight())
        // bottom - start button
        Button(onClick = { /*TODO*/ }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Start Process", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun ProcessName(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.headlineMedium
    )
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
    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(imageVector = Icons.Filled.Notifications, contentDescription = "Start Sound on")
        Text(text = "Runs $processTime seconds, interval every $intervalTime seconds")
    }
}

@Preview(showBackground = true)
@Composable
fun FTPPreview() {
    ExistingProcessDetails(
        process = FotoTimerProcess(
            "Sample Process",
            30L,
            10L,
            false,
            1,
            false,
            2,
            false,
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