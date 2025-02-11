package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.ui.DefaultSpacer
import com.exner.tools.fototimer.ui.ImportDataViewModel
import com.exner.tools.fototimer.ui.ImportState
import com.exner.tools.fototimer.ui.ImportStateConstants
import com.exner.tools.fototimer.ui.TextAndSwitch
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType

@Destination<RootGraph>
@Composable
fun ImportData(
    importDataViewModel: ImportDataViewModel = hiltViewModel(),
    destinationsNavigator: DestinationsNavigator
) {
//    val context = LocalContext.current

    val importState by importDataViewModel.importStateFlow.collectAsStateWithLifecycle(
        ImportState()
    )

    val fileForImport by importDataViewModel.file.collectAsStateWithLifecycle()

    val listOfProcessesInFile by importDataViewModel.listOfProcessesInFile.collectAsStateWithLifecycle()

    val listOfOldProcesses by importDataViewModel.listOfOldProcesses.collectAsStateWithLifecycle()
    val listOfNewProcesses by importDataViewModel.listOfNewProcesses.collectAsStateWithLifecycle()
    val listOfClashingProcesses by importDataViewModel.listOfClashingProcesses.collectAsStateWithLifecycle()

    val hasOverlap = importDataViewModel.hasOverlap
    val override by importDataViewModel.override.collectAsStateWithLifecycle()
    val highestUidInProcessDB by importDataViewModel.highestUidInProcessDB.collectAsStateWithLifecycle()

    val errorMessage by importDataViewModel.errorMessage.collectAsStateWithLifecycle()

    val launcher = rememberFilePickerLauncher(
        type = PickerType.File(
            extensions = listOf("json")
        ),
        mode = PickerMode.Single,
        title = "Pick a JSON file"
    ) { file ->
        importDataViewModel.setFile(file)
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(innerPadding)
                    .padding(innerPadding)
                    .padding(8.dp)
            ) {
                Text(
                    text = "Import data",
                    style = MaterialTheme.typography.headlineSmall
                )
                DefaultSpacer()
                when (importState.state) {
                    ImportStateConstants.IDLE -> {
                        Text(text = "Start by selecting a file to import from")
                        DefaultSpacer()
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select file")
                        }
                    }

                    ImportStateConstants.ERROR -> {
                        Text(text = "Something went wrong!")
                        DefaultSpacer()
                        Text(text = "There was an error ('$errorMessage'). You could try importing a different file?")
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select different file")
                        }
                    }

                    ImportStateConstants.FILE_SELECTED -> {
                        Text(text = "File selected: ${fileForImport?.name}")
                        DefaultSpacer()
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select different file")
                        }
                    }

                    ImportStateConstants.FILE_ANALYSED -> {
                        Text(text = "File selected: ${fileForImport?.name}")
                        DefaultSpacer()
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select different file")
                        }
                        DefaultSpacer()
                        Text(text = "File contains ${listOfProcessesInFile.size} processes.")
                        DefaultSpacer()
                        if (override) {
                            Text(text = "Existing processes will be deleted, and ${listOfProcessesInFile.size} will be imported.")
                            DefaultSpacer()
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.5f)
                            ) {
                                items(items = listOfProcessesInFile) { process ->
                                    Text(text = "${process.uid} - ${process.name}")
                                }
                            }

                        } else {
                            if (listOfOldProcesses.isNotEmpty()) {
                                Text(text = "Processes that already exist in the database: ${listOfOldProcesses.size}")
                                DefaultSpacer()
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.5f)
                                ) {
                                    items(items = listOfOldProcesses) { process ->
                                        Text(text = "${process.uid} - ${process.name}")
                                    }
                                }
                            }
                            if (listOfClashingProcesses.isNotEmpty()) {
                                Text(text = "Processes that exist but are different: ${listOfClashingProcesses.size}")
                                DefaultSpacer()
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.5f)
                                ) {
                                    items(items = listOfClashingProcesses) { process ->
                                        Text(text = "${process.uid} - ${process.name}")
                                    }
                                }
                            }
                            if (listOfNewProcesses.isNotEmpty()) {
                                Text(text = "Processes that will be imported: ${listOfNewProcesses.size}")
                                DefaultSpacer()
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.5f)
                                ) {
                                    items(items = listOfNewProcesses) { process ->
                                        Text(text = "${process.uid} - ${process.name}")
                                    }
                                }
                            } else {
                                Text(
                                    modifier = Modifier.weight(0.5f),
                                    text = "Nothing to import.",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            DefaultSpacer()
                            if (hasOverlap) {
                                Text(
                                    text = "Uids overlap! This will cause an error on import!",
                                    color = MaterialTheme.colorScheme.error
                                )
                                DefaultSpacer()
                                Text(text = "The safe option is to delete all existing processes before import.")
                                DefaultSpacer()
                                Text(text = "You can also edit the JSON file and start over. When doing so, change the 'uid' fields for each process, and check whether any 'gotoId' fields have to be adjusted, too.")
                                DefaultSpacer()
                                Text(text = "The highest Uid in the database is $highestUidInProcessDB.")
                            }
                        }
                        TextAndSwitch(
                            text = "Delete existing processes before import?",
                            checked = override
                        ) {
                            importDataViewModel.setOverride(it)
                        }
                    }

                    ImportStateConstants.IMPORT_FINISHED -> {
                        Text(text = "Import successfully done.")
                    }
                }
            }
        },
        bottomBar = {
            BottomAppBar(
                actions = {
                    IconButton(onClick = {
                        destinationsNavigator.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancel"
                        )
                    }
                },
                floatingActionButton = {
                    if (importState.state == ImportStateConstants.FILE_ANALYSED
                        && (!hasOverlap || override)
                        && (listOfNewProcesses.isNotEmpty() || (override && listOfProcessesInFile.isNotEmpty()))
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Import") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Import"
                                )
                            },
                            onClick = {
                                importDataViewModel.commitImport {
//                                    Toast.makeText(context, "Date imported", Toast.LENGTH_LONG)
//                                        .show()
                                }
                                destinationsNavigator.navigateUp()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        )
                    } else {
                        ExtendedFloatingActionButton(
                            text = { Text(text = "Select file") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Select file"
                                )
                            },
                            onClick = {
                                launcher.launch()
                            },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                        )
                    }
                }
            )
        }
    )
}