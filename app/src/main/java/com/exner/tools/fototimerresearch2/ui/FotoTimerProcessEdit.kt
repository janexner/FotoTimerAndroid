package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

lateinit var process: FotoTimerProcess

@Composable
fun FotoTimerProcessEdit(fotoTimerProcessViewModel: FotoTimerProcessViewModel, processId: String?) {

    val uid = processId?.toLong() ?: -1
    val ftProcess: FotoTimerProcess
    var tmpProcess: FotoTimerProcess? = null

    // read the process, if it exists
    if (uid >= 0) {
        tmpProcess = fotoTimerProcessViewModel.getProcessById(uid)
    }
    // do we need to build one?
    if (null != tmpProcess) {
        ftProcess = tmpProcess
    } else {
        // build one
        val context = LocalContext.current
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
        ftProcess = FotoTimerProcess(
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
    // while we're here, let's get the name of the next process, if any
    val tmpGotoProcess = ftProcess.gotoId?.let { fotoTimerProcessViewModel.getProcessById(it) }

    // OK, now display the thing for editing
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = ftProcess.name)
        Text(text = "${ftProcess.processTime} / ${ftProcess.intervalTime}")
        Text(text = "Then goes on to ${tmpGotoProcess?.name}")
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
