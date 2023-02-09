package com.exner.tools.fototimerresearch2

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.exner.tools.fototimerresearch2.ui.*
import com.exner.tools.fototimerresearch2.ui.destinations.*
import com.exner.tools.fototimerresearch2.ui.destinations.FotoTimerProcessDetailsDestination.argsFrom
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerApp() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value

    Scaffold(
        topBar = {
            FotoTimerTopBar(destination, navController)
        },
        content = { innerPadding ->
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(FotoTimerProcessDetailsDestination) {
                    FotoTimerProcessDetails(
                        navigator = destinationsNavigator,
                        processId = navArgs.processId,
                    )
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FotoTimerTopBar(
    destination: Destination?,
    navController: NavHostController
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Foto Timer") },
        navigationIcon = {
            when (destination) {
                FotoTimerProcessListDestination -> {
                    // no back button here
                }
                FotoTimerRunningProcessDestination -> {
                    // go back, but all the way to the list!
                    IconButton(onClick = { navController.navigate(FotoTimerProcessListDestination) {
                        popUpTo(FotoTimerProcessListDestination.route) {
                            inclusive = true
                        }
                    } }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
                else -> {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        actions = {
            when (destination) {
                FotoTimerProcessListDestination -> {
                    IconButton(onClick = {
                        navController.navigate(
                            FotoTimerProcessEditDestination(-1)
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Process"
                        )
                    }
                    IconButton(onClick = {
                        navController.navigate(FotoTimerSettingsDestination())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
                FotoTimerProcessDetailsDestination -> {
                    argsFrom(navController.currentBackStackEntry!!).processId.let {
                        IconButton(onClick = {
                            navController.navigate(
                                FotoTimerProcessEditDestination(
                                    it
                                )
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Process"
                            )
                        }
                    }
                    IconButton(onClick = {
                        navController.navigate(FotoTimerSettingsDestination())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
                FotoTimerSettingsDestination -> {
                    // no icons
                }
                FotoTimerRunningProcessDestination -> {
                    // no icons
                }
                else -> {
                    IconButton(onClick = {
                        navController.navigate(FotoTimerSettingsDestination())
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            }
        }
    )
}
