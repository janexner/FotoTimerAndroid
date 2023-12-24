package com.exner.tools.fototimer

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.exner.tools.fototimer.ui.FotoTimerProcessDetails
import com.exner.tools.fototimer.ui.NavGraphs
import com.exner.tools.fototimer.ui.appCurrentDestinationAsState
import com.exner.tools.fototimer.ui.destinations.Destination
import com.exner.tools.fototimer.ui.destinations.FotoTimerAboutDestination
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessDetailsDestination
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessEditDestination
import com.exner.tools.fototimer.ui.destinations.FotoTimerProcessListDestination
import com.exner.tools.fototimer.ui.destinations.FotoTimerRunningProcessDestination
import com.exner.tools.fototimer.ui.destinations.FotoTimerSettingsDestination
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
        },
        bottomBar = {
            FotoTimerBottomBar(destination, navController)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FotoTimerTopBar(
    destination: Destination?,
    navController: NavHostController
) {
    TopAppBar(
        title = { Text(text = "Foto Timer") },
        navigationIcon = {
            when (destination) {
                FotoTimerProcessListDestination -> {
                    // no back button here
                }

                FotoTimerRunningProcessDestination -> {
                    // go back, but all the way to the list!
                    IconButton(onClick = {
                        navController.navigate(FotoTimerProcessListDestination) {
                            popUpTo(FotoTimerProcessListDestination.route) {
                                inclusive = true
                            }
                        }
                    }) {
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
                FotoTimerSettingsDestination -> {
                    AboutActionIconButton(navController)
                }

                FotoTimerRunningProcessDestination -> {
                    // no icons
                }

                FotoTimerAboutDestination -> {
                    // no icons
                }

                else -> {
                    SettingsActionIconButton(navController)
                    AboutActionIconButton(navController)
                }
            }
        }
    )
}

@Composable
private fun FotoTimerBottomBar(
    destination: Destination?,
    navController: NavHostController
) {
    BottomAppBar(
        actions = {
            when (destination) {
                FotoTimerProcessListDestination -> {
                    // no icons
                }
                FotoTimerProcessDetailsDestination -> {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Start")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit")
                    }
                }
                FotoTimerProcessEditDestination -> {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Done, contentDescription = "Save")
                    }
                }
                else -> {
                    // no icons
                }
            }
        },
        floatingActionButton = {
            when (destination) {
                FotoTimerAboutDestination -> {
                    // no floating action button
                }
                FotoTimerProcessEditDestination -> {
                    // no fab
                }
                FotoTimerRunningProcessDestination -> {
                    // no fab
                }
                FotoTimerSettingsDestination -> {
                    // no fab
                }
                else -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(
                                FotoTimerProcessEditDestination(-1)
                            )
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(Icons.Filled.Add, "Add a process")
                    }
                }
            }
        }
    )
}

@Composable
private fun AboutActionIconButton(navController: NavHostController) {
    IconButton(onClick = {
        navController.navigate(FotoTimerAboutDestination())
    }) {
        Icon(imageVector = Icons.Filled.Info, contentDescription = "About Foto Timer")
    }
}

@Composable
private fun SettingsActionIconButton(navController: NavHostController) {
    IconButton(onClick = {
        navController.navigate(FotoTimerSettingsDestination())
    }) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}
