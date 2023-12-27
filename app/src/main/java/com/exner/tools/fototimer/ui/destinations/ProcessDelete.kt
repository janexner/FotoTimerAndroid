package com.exner.tools.fototimer.ui.destinations

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.ui.HeaderText
import com.exner.tools.fototimer.ui.ProcessDeleteViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun ProcessDelete(
    processId: Long,
    processDeleteViewModel: ProcessDeleteViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    Log.i("ProcessDeleteScreen", "entering composable...")

    val processName by processDeleteViewModel.processName.observeAsState()
    val processIsTarget by processDeleteViewModel.processIsTarget.observeAsState()
    val dependantProcesses by processDeleteViewModel.dependantProcesses.observeAsState()

    processDeleteViewModel.checkProcess(processId)

    Scaffold(
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                HeaderText(text = "Delete Process", modifier = Modifier.padding(8.dp))
                Divider(modifier = Modifier.padding(8.dp))
                Text(
                    text = "You are about to delete processID $processId,",
                    modifier = Modifier.padding(8.dp)
                )
                Text(text = "'$processName'.", modifier = Modifier.padding(8.dp))
                // double-check in case there are chains that contain this
                if (processIsTarget == true && dependantProcesses != null) {
                    Text(text = "Other processes link to this one!", modifier = Modifier.padding(8.dp))
                    if (dependantProcesses!!.isNotEmpty()) {
                        dependantProcesses!!.forEach { 
                            Text(text = "${it.uid}: ${it.name}", modifier = Modifier.padding(16.dp))
                        }
                    }
                    Text(text = "If you delete this process, those others will no longer be able to link to it, meaning they will stop when they try to.", modifier = Modifier.padding(8.dp))
                }
            }
        },
        bottomBar = {
            FotoTimerDeleteBottomBar(processId = processId, navigator = navigator) {
                processDeleteViewModel.deleteProcess(processId)
            }
        }
    )
}

@Composable
fun FotoTimerDeleteBottomBar(
    processId: Long,
    navigator: DestinationsNavigator,
    deleteAction: () -> Unit,
) {
    BottomAppBar(
        actions = {},
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    deleteAction()
                    navigator.navigateUp()
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Delete the process")
            }
        }
    )
}
