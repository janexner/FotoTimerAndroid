package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.exner.tools.fototimer.ui.destinations.destinations.ProcessEditDestination
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

    Scaffold(
        modifier = Modifier.imePadding(),
        content = { innerPadding ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
            ) {
                processes?.let {
                    items(count = it.size) { fotoTimerProcess ->
                        val ftProcess = processes!![fotoTimerProcess]
                        Surface(
                            modifier = Modifier
                                .clickable {
                                    navigator.navigate(
                                        ProcessDetailsDestination(
                                            processId = ftProcess.uid,
                                        )
                                    )
                                },
                        ) {
                            var supText = "${ftProcess.processTime}/${ftProcess.intervalTime}"
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
                item {
                    Spacer(
                        Modifier.windowInsetsBottomHeight(
                            WindowInsets.systemBars
                        )
                    )
                }
            }
        },
        bottomBar = {
            FotoTimerListBottomBar(navigator)
        }
    )
}

@Composable
private fun FotoTimerListBottomBar(
    navigator: DestinationsNavigator
) {
    BottomAppBar(
        actions = {
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Add") },
                icon = {
                    Icon(Icons.Filled.Add, "Add a process")
                },
                onClick = {
                    navigator.navigate(
                        ProcessEditDestination(-1)
                    )
                },
                containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
            )
        }
    )
}
