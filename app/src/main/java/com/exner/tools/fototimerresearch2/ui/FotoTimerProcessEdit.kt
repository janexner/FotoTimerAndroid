package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

lateinit var spViewModel: FotoTimerSingleProcessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessEdit(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    spViewModel: FotoTimerSingleProcessViewModel,
    processId: String?,
    modifier: Modifier = Modifier
) {

    // read the process, if it exists
    val uid = processId?.toLong() ?: -1
    var tmpProcess: FotoTimerProcess? = fotoTimerProcessViewModel.getProcessById(uid)

    // do we need to build one?
    if (null == tmpProcess) {
        // build one
        val context = LocalContext.current
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
        tmpProcess = FotoTimerProcess(
            "New Process",
            sharedSettings.getLong("preference_process_time", 30L),
            sharedSettings.getLong("preference_interval_time", 10L),
            false,
            1,
            false,
            2,
            false,
            3,
            false,
            false,
            sharedSettings.getInt("preference_lead_in_time", 5),
            false,
            false,
            sharedSettings.getInt("preference_pause_time", 0),
            -1L
        )
    }
    // let's use that fancy ViewModel
    spViewModel.setVarsFromProcess(tmpProcess)

    // while we're here, let's get the name of the next process, if any
    val tmpGotoProcess = tmpProcess.gotoId?.let { fotoTimerProcessViewModel.getProcessById(it) }
    // and the list of all available processes for goto
    // TODO

    // OK, at this point we have a process, either existing, or fresh.
    // Now display the thing for editing
    Column(modifier = Modifier.fillMaxSize()) {
        // top - fields
        TextField(
            value = spViewModel.name,
            onValueChange = { spViewModel.name = it },
            label = {
                Text(
                    text = "Process name"
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // middle and bottom - filler and save button
        Spacer(modifier = Modifier.fillMaxSize())
        Button(onClick = { /*TODO*/ }) {
            Text(
                text = "Save Process",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
