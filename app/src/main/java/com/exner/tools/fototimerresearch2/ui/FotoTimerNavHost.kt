package com.exner.tools.fototimerresearch2

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.exner.tools.fototimerresearch2.ui.FotoTimerScreens
import com.exner.tools.fototimerresearch2.ui.overview.OverviewBody
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@Composable
fun FotoTimerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    FotoTimerTheme() {
        Scaffold() {
            NavHost(
                navController = navController,
                startDestination = FotoTimerScreens.Overview.name,
                modifier = modifier
            ) {
                composable(FotoTimerScreens.Overview.name) {
                    OverviewBody()
                }
            }
        }
    }
}