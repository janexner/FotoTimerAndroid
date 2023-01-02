package com.exner.tools.fototimerresearch2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.exner.tools.fototimerresearch2.ui.FotoTimerNavHost
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FotoTimerNavHostTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: NavHostController

    @Before
    fun setupFotoTimerNavHost() {
        composeTestRule.setContent {
            navController = rememberNavController()
            FotoTimerNavHost(navController = navController)
        }
    }

    @Test
    fun fotoTimerNavHost() {
        composeTestRule
            .onNodeWithContentDescription("Overview Screen")
            .assertIsDisplayed()
    }
}