package com.exner.tools.fototimerresearch2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerSingleProcessViewModel
import com.exner.tools.fototimerresearch2.ui.*
import com.exner.tools.fototimerresearch2.ui.destinations.Destination
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessDetailsDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.rememberNavHostEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun FotoTimerApp() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value

    FotoTimerScaffold(
        startRoute = NavGraphs.root.startRoute,
        navController = navController,
        topBar = { dest, backStackEntry ->
            FotoTimerTopBar(navController = navController, destination = destination, navBackStackEntry = backStackEntry)
        },
        content = { innerPadding ->
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(FotoTimerProcessDetailsDestination) {
                    val singleProcessViewModel = hiltViewModel<FotoTimerSingleProcessViewModel>()
                    FotoTimerProcessDetails(
                        navigator = destinationsNavigator,
                        processId = navArgs.processId,
                    )
                }
            }
        }
    )
}
