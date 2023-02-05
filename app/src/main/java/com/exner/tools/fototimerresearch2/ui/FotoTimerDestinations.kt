package com.exner.tools.fototimerresearch2.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

interface FotoTimerDestinations {
    val icon: ImageVector
    val route: String
    val contentDescription: String
}

object ProcessList : FotoTimerDestinations {
    override val icon = Icons.Filled.List
    override val route = "home"
    override val contentDescription = "Home"
}

object ProcessDetails : FotoTimerDestinations {
    override val icon = Icons.Filled.Star
    override val route = "process"
    override val contentDescription = "Process Details"
}

object ProcessEdit : FotoTimerDestinations {
    override val icon = Icons.Filled.Edit
    override val route = "edit"
    override val contentDescription = "Edit Process"
}

object Settings : FotoTimerDestinations {
    override val icon = Icons.Filled.Settings
    override val route = "settings"
    override val contentDescription = "Settings"
}

object RunningProcess : FotoTimerDestinations {
    override val icon = Icons.Filled.ArrowForward
    override val route = "running"
    override val contentDescription = "Run Process"
}

object ChainingProcess : FotoTimerDestinations {
    override val icon = Icons.Filled.ArrowForward
    override val route = "chaining"
    override val contentDescription = "Chain Process"
}