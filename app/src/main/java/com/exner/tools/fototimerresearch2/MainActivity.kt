package com.exner.tools.fototimerresearch2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.*
import androidx.navigation.compose.*
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.*
import com.exner.tools.fototimerresearch2.ui.*
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

class MainActivity : ComponentActivity() {

    private val fotoTimerProcessViewModel: FotoTimerProcessViewModel by viewModels {
        FotoTimerProcessViewModelFactory((application as FotoTimerApplication).repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val spViewModel: FotoTimerSingleProcessViewModel =
            ViewModelProvider(this).get(FotoTimerSingleProcessViewModel::class.java)

        // are we in forced dark mode?
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(this)
        val forceNightMode = sharedSettings.getBoolean("preference_night_mode", false)

        setContent {
            FotoTimerTheme(
                darkTheme = forceNightMode
            ) {
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
                            // Home / Process List
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
                            // Show Process details
                            composable(
                                route = "${ProcessDetails.route}/{processId}",
                                arguments = listOf(navArgument("processId") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val processId = backStackEntry.arguments?.getString("processId")
                                if (null != processId) {
                                    FotoTimerProcessDetails(
                                        fotoTimerProcessViewModel,
                                        processId,
                                        onStartButtonClick = {
                                            navController.navigate(
                                                route = "${RunningProcess.route}/${processId}"
                                            )
                                        }
                                    )
                                }
                            }
                            // Edit Process
                            composable(
                                route = "${ProcessEdit.route}?processId={processId}",
                                arguments = listOf(navArgument("processId") {
                                    type = NavType.StringType
                                    nullable = true
                                })
                            ) { backStackEntry ->
                                val processId = backStackEntry.arguments?.getString("processId")
                                FotoTimerProcessEdit(
                                    fotoTimerProcessViewModel = fotoTimerProcessViewModel,
                                    singleprocessViewModel = spViewModel,
                                    processId = processId,
                                    onSaveClicked = { navController.popBackStack() }
                                )
                            }
                            // Open Settings screen
                            composable(route = Settings.route) {
                                Settings()
                            }
                            // Run Process nested graph
                            runningGraph(navController)
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
        var inProcessDetails = false

        // some state checking - probably lame
        if (currentDestination != null) {
            if (currentDestination.route == ProcessList.route) {
                inProcessList = true
            }
            if (currentDestination.route!!.startsWith(ProcessDetails.route)) {
                inProcessDetails = true
            }
            if (currentDestination.route == Settings.route) {
                currentTitle = "Settings"
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
                    IconButton(onClick = {
                        navController.navigate(ProcessEdit.route)
                    }) {
                        Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Process")
                    }
                } else if (inProcessDetails) {
                    val processId =
                        navController.currentBackStackEntry?.arguments?.getString("processId")
                    if (null != processId) {
                        val editRoute = ProcessEdit.route + "?processId=${processId}"
                        IconButton(onClick = { navController.navigate(editRoute) }) {
                            Icon(
                                imageVector = ProcessEdit.icon,
                                contentDescription = ProcessEdit.contentDescription
                            )
                        }
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

    private fun NavGraphBuilder.runningGraph(navController: NavController) {
        navigation(
            startDestination = "runnerScreen/{processId}",
            route = "${RunningProcess.route}/{processId}",
            arguments = listOf(navArgument("processId") { defaultValue = "-1" })
        ) {
            composable(
                route = "runnerScreen/{processId}",
                arguments = listOf(navArgument("processId") {})
            ) { backStackEntry ->
                val processId =
                    backStackEntry.arguments?.getString("processId")?.toLongOrNull()
                if (null != processId && processId >= 0) {
                    val process =
                        fotoTimerProcessViewModel.getProcessById(processId)
                    if (null != process) {
                        Log.i(
                            "jexner Main",
                            "Creating/retrieving FTRPVM for processId $processId..."
                        )
                        val backStackEntry = remember {
                            navController.currentDestination?.route?.let {
                                Log.i("jexner nested", "Current route $it")
                                navController.getBackStackEntry(
                                    it
                                )
                            }
                        }
                        val ftrpViewModel = backStackEntry?.let {
                            Log.i("jexner nested", "Creating/retrieving FTRPVM for $processId...")
                            ViewModelProvider(
                                it.viewModelStore,
                                FotoTimerRunningProcessViewModelFactory(process)
                            ).get(FotoTimerRunningProcessViewModel::class.java)
                        }
                        if (null != ftrpViewModel) {
                            Log.i("jexner Main", "Creating Composable for Runner...")
                            FotoTimerRunningProcess(ftrpViewModel)
                        }
                    }
                } else {
                    Log.w("jexner Runner", "Cannot run process with id $processId!")
                }
            }
        }
    }
}
