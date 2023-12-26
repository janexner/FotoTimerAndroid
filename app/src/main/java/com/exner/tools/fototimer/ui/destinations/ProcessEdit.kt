package com.exner.tools.fototimer.ui.destinations

import android.util.Log
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ProcessEdit(
    processId: Long
) {
    Log.i("ProcessEditScreen", "entering composable...")
}
