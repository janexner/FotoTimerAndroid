package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FotoTimerProcessList(
    fotoTimerProcessViewModel: FotoTimerProcessViewModel,
    onNavigateToProcessDetails: (processId: Long) -> Unit
) {
    val ftpList by fotoTimerProcessViewModel.allProcesses.observeAsState()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp))
    {
        ftpList?.let {
            items(count = it.size) { fotoTimerProcess ->
                val ftProcess = ftpList!![fotoTimerProcess]
                Surface(
                    modifier = Modifier
                        .clickable { onNavigateToProcessDetails(ftProcess.uid) }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = ftProcess.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Row() {
                            Text(text = "${ftProcess.processTime}/${ftProcess.intervalTime}")
                            Text(text = " -> ${ftProcess.gotoId}")
                        }
                    }
                }
            }
        }
    }
}
