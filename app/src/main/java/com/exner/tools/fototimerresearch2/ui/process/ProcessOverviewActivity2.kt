package com.exner.tools.fototimerresearch2.ui.process

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.FotoTimerApplication
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

class ProcessOverviewActivity2 : ComponentActivity() {

    private var processID: Long = -1

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // find which process whe should show
        val arguments =
            requireNotNull(intent?.extras) { "The ProcessOverviewActivity must be called with a process ID so it knows which process to show!" }
        with(arguments) {
            processID = getLong("PROCESS_ID")
        }
        // get a handle on the DB
        val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
            FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
        }
        // retrieve said process
        val process =
            requireNotNull(fotoTimerProcessViewModel.getProcessById(processID)) { "The process ID passed is not valid" }


        setContent {
            MaterialTheme {
                Scaffold(contentWindowInsets = WindowInsets(16.dp)) {
                    ProcessDetailDescription(process)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        ProcessDetailDescription(FotoTimerProcess(
            "Test Process",
            30,
            10,
            false,
            0,
            false,
            0,
            false,
            0,
            false,
            false,
            0,
            false,
            false,
            0,
            0,
            0
        ))
    }
}
