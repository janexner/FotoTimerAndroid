package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    onNavigateToProcessDetails: (processId: Long) -> Unit,
    modifier: Modifier = Modifier,
    selectedProcessId: Long? = -1,
) {
    val ftpList by fotoTimerProcessViewModel.allProcesses.observeAsState()
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = modifier)
    {
        ftpList?.let {
            items(count = it.size) { fotoTimerProcess ->
                val ftProcess = ftpList!![fotoTimerProcess]
                var sColor = MaterialTheme.colorScheme.surface
                if (selectedProcessId == ftProcess.uid) {
                    sColor = MaterialTheme.colorScheme.secondary
                }
                Surface(
                    modifier = Modifier
                        .clickable { onNavigateToProcessDetails(ftProcess.uid) },
                    color = sColor
                ) {
                    var supText = "${ftProcess.processTime}/${ftProcess.intervalTime}"
                    if (ftProcess.keepsScreenOn) {
                        supText += " keeps screen on"
                    }
                    if (ftProcess.hasAutoChain && null != ftProcess.gotoId && ftProcess.gotoId >= 0) {
                        supText += " then ${ftProcess.gotoId}"
                    }
                    ListItem(
                        headlineText = { HeaderText(text = ftProcess.name) },
                        supportingText = { BodyText(text = supText) }
                    )
                }
            }
        }
    }
}

