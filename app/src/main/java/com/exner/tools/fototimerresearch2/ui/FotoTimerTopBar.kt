package com.exner.tools.fototimerresearch2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.exner.tools.fototimerresearch2.ui.destinations.*
import com.ramcosta.composedestinations.navigation.navigate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerTopBar(
    navController: NavHostController,
    destination: Destination?,
    navBackStackEntry: NavBackStackEntry?
) {
    var currentTitle = "Foto Timer"
    var inProcessList = false
    var inProcessDetails = false
    var inSettings = false

    // some state checking - probably lame
    if (destination != null) {
        if (destination.route == FotoTimerProcessListDestination.route) {
            inProcessList = true
        }
        if (destination.route == FotoTimerProcessDetailsDestination.route) {
            inProcessDetails = true
        }
        if (destination.route == FotoTimerSettingsDestination.route) {
            currentTitle = "Settings"
            inSettings = true
        }
    }

    // make the toolbar, based on state
    CenterAlignedTopAppBar(
        title = { Text(text = currentTitle) },
        navigationIcon = {
            if (!inProcessList) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (inProcessList) {
                IconButton(onClick = {
                    navController.navigate(
                        FotoTimerProcessEditDestination()
                    )
                }) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Process")
                }
            } else if (inProcessDetails) {
                val processId =
                    navController.currentBackStackEntry?.navArgs<FotoTimerProcessDetailsDestination>()
                        ?.argsFrom(navController.currentBackStackEntry!!)?.processId.let {
                            IconButton(onClick = {
                                navController.navigate(
                                    FotoTimerProcessEditDestination(
                                        it!!
                                    )
                                )
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Process"
                                )
                            }
                        }
            }
            if (!inSettings) {
                IconButton(onClick = {
                    val ftsd = FotoTimerSettingsDestination()
                    navController.navigate(ftsd)
                }) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }
    )
}

