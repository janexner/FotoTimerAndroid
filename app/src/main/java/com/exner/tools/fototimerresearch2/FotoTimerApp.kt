package com.exner.tools.fototimerresearch2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.exner.tools.fototimerresearch2.ui.FotoTimerScaffold
import com.exner.tools.fototimerresearch2.ui.FotoTimerTopBar
import com.exner.tools.fototimerresearch2.ui.NavGraphs
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.rememberNavHostEngine

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun FotoTimerApp() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()

    // val vm

    FotoTimerScaffold(
        startRoute = NavGraphs.root.startRoute,
        navController = navController,
        topBar = { dest, backStackEntry ->
            FotoTimerTopBar(dest, backStackEntry)
        },
        content = { innerPadding ->
            DestinationsNavHost(
                navGraph = NavGraphs.root,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}
