package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.ui.BodyText
import com.exner.tools.fototimer.ui.HeaderText
import com.exner.tools.fototimer.ui.ProcessListViewModel
import com.exner.tools.fototimer.ui.destinations.destinations.ProcessDetailsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@Composable
fun ProcessList(
    processListViewModel: ProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val processes by processListViewModel.allProcesses.observeAsState()

    LazyVerticalGrid (
        columns = GridCells.Adaptive(minSize = 250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        processes?.let {
            items(count = it.size) { fotoTimerProcess ->
                val ftProcess = processes!![fotoTimerProcess]
                Surface(
                    modifier = Modifier
                        .clickable { navigator.navigate(
                            ProcessDetailsDestination(
                                processId = ftProcess.uid,
                            )
                        ) },
                ) {
                    var supText = "${ftProcess.processTime}/${ftProcess.intervalTime}"
                    if (ftProcess.keepsScreenOn) {
                        supText += " keeps screen on"
                    }
                    if (ftProcess.hasAutoChain && null != ftProcess.gotoId && ftProcess.gotoId >= 0) {
                        supText += ". Next: ${ftProcess.gotoId}"
                    }
                    ListItem(
                        headlineContent = { HeaderText(text = ftProcess.name) },
                        supportingContent = { BodyText(text = supText) }
                    )
                }
            }
        }
    }
}
