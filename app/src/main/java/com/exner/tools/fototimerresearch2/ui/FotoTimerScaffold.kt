package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.exner.tools.fototimerresearch2.ui.destinations.Destination
import com.ramcosta.composedestinations.spec.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerScaffold(
    startRoute: Route,
    navController: NavHostController,
    topBar: @Composable (Destination, NavBackStackEntry?) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val destination: Destination = (navController.appCurrentDestinationAsState().value
        ?: startRoute.startAppDestination) as Destination
    val navBackStackEntry = navController.currentBackStackEntry

    Scaffold(
        topBar = {
            FotoTimerTopBar(
                navController = navController,
                destination = destination,
                navBackStackEntry = navBackStackEntry
            )
        },
        content = content
    )
}
