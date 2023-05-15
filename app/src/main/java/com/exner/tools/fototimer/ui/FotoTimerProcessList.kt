package com.exner.tools.fototimer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.data.model.FotoTimerProcessListViewModel
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessDetailsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessList(
    fotoTimerProcessListViewModel: FotoTimerProcessListViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val tmpList = fotoTimerProcessListViewModel.allProcesses
    val ftpList by tmpList.observeAsState()

    LazyVerticalGrid (
        columns = GridCells.Adaptive(minSize = 250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ftpList?.let {
            items(count = it.size) { fotoTimerProcess ->
                val ftProcess = ftpList!![fotoTimerProcess]
                Surface(
                    modifier = Modifier
                        .clickable { navigator.navigate(
                            FotoTimerProcessDetailsDestination(
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

