package com.exner.tools.fototimerresearch2

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModelFactory
import com.exner.tools.fototimerresearch2.ui.*

class MainActivity : AppCompatActivity() {

    private val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
        FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination
                Scaffold(
                    modifier = Modifier,
                    topBar = { FotoTimerTopBar(navController, currentDestination) },
                    content = { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = ProcessList.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = ProcessList.route) {
                                FotoTimerProcessList(
                                    fotoTimerProcessViewModel,
                                    onNavigateToProcessDetails = {
                                        navController.navigate(
                                            "${ProcessDetails.route}/${it}"
                                        )
                                    }
                                )
                            }
                            composable(
                                route = "${ProcessDetails.route}/{processId}",
                                arguments = listOf(navArgument("processId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val processId = backStackEntry.arguments?.getString("processId")
                                if (null != processId) {
                                    FotoTimerProcessDetails(fotoTimerProcessViewModel, processId)
                                }
                            }
                            composable(route = Settings.route) {
                                Settings()
                            }
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FotoTimerTopBar(navController: NavHostController, currentDestination: NavDestination?) {
        var currentTitle = "Foto Timer"
        var inProcessList = false
        var inSettings = false

        // some state checking - probably lame
        if (currentDestination != null) {
            if (currentDestination.route == ProcessList.route) {
                inProcessList = true
            }
            if (currentDestination.route == Settings.route) {
                currentTitle = "Settings"
                inSettings = true
            }
        }

        // make the toolbar, based on state
        CenterAlignedTopAppBar(
            title = { Text(text = currentTitle) },
            navigationIcon = {
                if (!inProcessList) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                if (inProcessList) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Process")
                    }
                }
                IconButton(onClick = {
                    navController.navigate(Settings.route) {
                        launchSingleTop = true
                    }
                }) {
                    Icon(
                        imageVector = Settings.icon,
                        contentDescription = Settings.contentDescription
                    )
                }
            }
        )
    }
}
