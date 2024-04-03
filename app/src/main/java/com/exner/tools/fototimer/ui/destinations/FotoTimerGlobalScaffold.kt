package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.exner.tools.fototimer.ui.destinations.destinations.AboutDestination
import com.exner.tools.fototimer.ui.destinations.destinations.Destination
import com.exner.tools.fototimer.ui.destinations.destinations.ProcessListDestination
import com.exner.tools.fototimer.ui.destinations.destinations.ProcessRunDestination
import com.exner.tools.fototimer.ui.destinations.destinations.SettingsDestination
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.rememberNavHostEngine

@Composable
fun FotoTimerGlobalScaffold() {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destination = navController.appCurrentDestinationAsState().value

    Scaffold(
        topBar = {
            FotoTimerTopBar(destination, navController)
        },
        content = { innerPadding ->
            val newPadding = PaddingValues.Absolute(
                innerPadding.calculateLeftPadding(LayoutDirection.Ltr),
                innerPadding.calculateTopPadding(),
                innerPadding.calculateRightPadding(LayoutDirection.Ltr),
                0.dp
            )
            DestinationsNavHost(
                navController = navController,
                navGraph = NavGraphs.root,
                modifier = Modifier.fillMaxSize()
                    .consumeWindowInsets(newPadding)
                    .padding(newPadding)
            ) {
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FotoTimerTopBar(
    destination: Destination?,
    navController: NavHostController
) {
    TopAppBar(
        title = { Text(text = "Foto Timer") },
        navigationIcon = {
            when (destination) {
                ProcessListDestination -> {
                    // no back button here
                }

                ProcessRunDestination -> {
                    // none here, either
                }

                else -> {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            }
        },
        actions = {
            when (destination) {
                SettingsDestination -> {
                    AboutActionIconButton(navController)
                }

                AboutDestination -> {
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
private fun AboutActionIconButton(navController: NavHostController) {
    IconButton(onClick = {
        navController.navigate(AboutDestination())
    }) {
        Icon(imageVector = Icons.Filled.Info, contentDescription = "About Foto Timer")
    }
}

@Composable
private fun SettingsActionIconButton(navController: NavHostController) {
    IconButton(onClick = {
        navController.navigate(SettingsDestination())
    }) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}
