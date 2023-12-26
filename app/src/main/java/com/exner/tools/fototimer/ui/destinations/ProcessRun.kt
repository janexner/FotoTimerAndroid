package com.exner.tools.fototimer.ui.destinations

import android.util.Log
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ProcessRun(
    processId: Long
) {
    Log.i("ProcessRunScreen", "entering composable...")
}
