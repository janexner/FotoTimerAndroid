package com.exner.tools.fototimer.ui.destinations

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.ui.DefaultSpacer
import com.exner.tools.fototimer.ui.ImportDataViewModel
import com.exner.tools.fototimer.ui.ImportState
import com.exner.tools.fototimer.ui.ImportStateConstants
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
    val context = LocalContext.current

    val importState by importDataViewModel.importStateFlow.collectAsStateWithLifecycle(
        ImportState()
    )

    val fileForImport by importDataViewModel.file.collectAsStateWithLifecycle()

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
                        Text(text = "Start by selecting a file to import")
                        DefaultSpacer()
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select file to import")
                        }
                    }

                    ImportStateConstants.FILE_SELECTED -> {
                        Text(text = "File selected: ${fileForImport?.name}")
                        DefaultSpacer()
                        Button(onClick = {
                            launcher.launch()
                        }) {
                            Text(text = "Select different file to import")
                        }

                    }

                    ImportStateConstants.FILE_READ -> TODO()
                    ImportStateConstants.IMPORT_FINISHED -> TODO()
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
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Import") },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Import"
                            )
                        },
                        onClick = {
                            importDataViewModel.commitImport()
                        },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    )
                }
            )
        }
    )
}