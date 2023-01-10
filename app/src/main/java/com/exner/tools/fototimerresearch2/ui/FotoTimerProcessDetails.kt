package com.exner.tools.fototimerresearch2.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.exner.tools.fototimerresearch2.data.model.FotoTimerProcessViewModel

@Composable
fun FotoTimerProcessDetails(fotoTimerProcessViewModel: FotoTimerProcessViewModel, processId: String) {
    Text(text = "Process ID is $processId")
}
