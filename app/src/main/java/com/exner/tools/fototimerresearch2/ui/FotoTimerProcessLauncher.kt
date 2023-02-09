package com.exner.tools.fototimerresearch2.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessLauncherViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessListViewModel
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerRunningProcessDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun FotoTimerProcessLauncher(
    fotoTimerProcessLauncherViewModel: FotoTimerProcessLauncherViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    processId: Long
) {
    if (fotoTimerProcessLauncherViewModel.noProcessSoGoBack) {
        Log.i("jexner FTPL", "Process ID $processId was not good, so jumping back...")
        navigator.popBackStack()
    }
    // for now we do not wait or anything. We simply call runner
    navigator.navigate(FotoTimerRunningProcessDestination(processId))
}