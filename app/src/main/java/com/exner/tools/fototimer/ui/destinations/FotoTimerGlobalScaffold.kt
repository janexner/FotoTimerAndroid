package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AboutDestination
import com.ramcosta.composedestinations.generated.destinations.ExportDataDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessListDestination
import com.ramcosta.composedestinations.generated.destinations.ProcessRunDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.rememberNavHostEngine
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.rememberDestinationsNavigator

@Composable
fun FotoTimerGlobalScaffold(windowSizeClass: WindowSizeClass) {
    val engine = rememberNavHostEngine()
    val navController = engine.rememberNavController()
    val destinationsNavigator = navController.rememberDestinationsNavigator()
    val destination = navController.currentDestinationAsState().value

    Scaffold(
        topBar = {
            FotoTimerTopBar(destination, destinationsNavigator)
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
                dependenciesContainerBuilder = {
                    dependency(windowSizeClass)
                },
                modifier = Modifier
                    .fillMaxSize()
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
    destination: DestinationSpec?,
    navigator: DestinationsNavigator
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
                    IconButton(onClick = { navigator.navigateUp() }) {
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
                    ProcessExportIconButton(navigator)
                    AboutActionIconButton(navigator)
                }

                AboutDestination, ProcessRunDestination -> {
                    // no icons
                }

                else -> {
                    ProcessExportIconButton(navigator)
                    SettingsActionIconButton(navigator)
                    AboutActionIconButton(navigator)
                }
            }
        }
    )
}

@Composable
private fun ProcessExportIconButton(navigator: DestinationsNavigator) {
    IconButton(onClick = {
        navigator.navigate(ExportDataDestination)
    }) {
        Icon(imageVector = Icons.Default.Share, contentDescription = "Export Data")
    }
}

@Composable
private fun AboutActionIconButton(navigator: DestinationsNavigator) {
    IconButton(onClick = {
        navigator.navigate(AboutDestination())
    }) {
        Icon(imageVector = Icons.Filled.Info, contentDescription = "About Foto Timer")
    }
}

@Composable
private fun SettingsActionIconButton(navigator: DestinationsNavigator) {
    IconButton(onClick = {
        navigator.navigate(SettingsDestination())
    }) {
        Icon(
            imageVector = Icons.Filled.Settings,
            contentDescription = "Settings"
        )
    }
}
