package com.exner.tools.fototimerresearch2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class FotoTimerScreens(
    val icon: ImageVector
) {
    Overview (
        icon = Icons.Filled.Home
    ),
    ManageProcesses(
        icon = Icons.Filled.List
    ),
    Settings(
        icon = Icons.Filled.Settings
    )
}