package com.exner.tools.fototimerresearch2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessListViewModel
import com.exner.tools.fototimerresearch2.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimerresearch2.ui.NavGraphs
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // instantiate(?) the FTPVM
    val fotoTimerProcessListViewModel: FotoTimerProcessListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // load sounds
        FotoTimerSoundPoolHolder.loadSounds(this)

        // are we in forced dark mode?
        val sharedSettings = PreferenceManager.getDefaultSharedPreferences(this)
        val forceNightMode = sharedSettings.getBoolean("preference_night_mode", false)

        setContent {
            FotoTimerTheme(
                darkTheme = forceNightMode
            ) {
                DestinationsNavHost(
                    navGraph = NavGraphs.root,
                )
            }
        }
    }

    /*
    @Composable
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
    private fun ListScreen(spViewModel: FotoTimerSingleProcessViewModel) {
        val windowSizeClass = calculateWindowSizeClass(this)

        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

        if (isCompact) {
            // lock screen in portrait mode for small-ish screens
            LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }

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
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Compact) {
                                    FotoTimerProcessList(
                                        fotoTimerProcessViewModel,
                                        onNavigateToProcessDetails = {
                                            navController.navigate(
                                                "${ProcessDetails.route}/${it}"
                                            )
                                        },
                                        modifier = Modifier.width(350.dp),
                                        selectedProcessId = processId.toLongOrNull()
                                    )
                                }
                                FotoTimerProcessDetails(
                                    fotoTimerProcessViewModel,
                                    processId
                                ) {
                                    navController.navigate(
                                        route = "${RunningProcess.route}/${processId}"
                                    )
                                }
                            }
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
                            singleProcessViewModel = spViewModel,
                            processId = processId
                        ) { navController.popBackStack() }
                    }
                    // Open Settings screen
                    composable(route = Settings.route) {
                        Settings()
                    }
                    // chain to next process
                    composable(
                        route = ChainingProcess.route,
                        deepLinks = listOf(navDeepLink {
                            uriPattern =
                                "android-app://androidx.navigation/chaining/{processId}"
                        }),
                        arguments = listOf(navArgument("processId") {
                            type = NavType.StringType
                            nullable = false
                        })
                    ) { backStackEntry ->
                        val processId = backStackEntry.arguments?.getString("processId")
                        Column(modifier = Modifier.fillMaxSize()) {
                            HeaderText(text = "Moving on...")
                        }
                        Log.i("jexner Main Nav", "Backstack ${navController.toString()}")
                        navController.navigate(
                            route = "${RunningProcess.route}/${processId}"
                        )
                    }
                    // Run Process nested graph
                    runningGraph(navController)
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
                        @Suppress("ReplaceGetOrSet")
                        val ftrpViewModel = ViewModelProvider(
                            backStackEntry.viewModelStore,
                            FotoTimerRunningProcessViewModelFactory(process,
                                onStartNextProcess = {
                                    navController.navigate(
                                        route = "${ChainingProcess.route}/${process.gotoId}"
                                    )
                                }
                            )
                        ).get(FotoTimerRunningProcessViewModel::class.java)
                        FotoTimerRunningProcess(
                            runningProcessViewModel = ftrpViewModel
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FotoTimerTopBar(navController: NavHostController, currentDestination: NavDestination?) {
        var currentTitle = "Foto Timer"
        var inProcessList = false
        var inProcessDetails = false
        var inSettings = false

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
                if (!inSettings) {
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
            }
        )
    }
*/
}
