package com.exner.tools.fototimerresearch2.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

// Copyright 2022 Google LLC.
// SPDX-License-Identifier: Apache-2.0

enum class WindowSize { Compact, Medium, Expanded }

/**
 * Remembers the [WindowSize] class for the window corresponding to the
 * current window metrics.
 */
@Composable
fun Activity.rememberWindowSizeClass(): WindowSize {
    // Get the size (in pixels) of the window
    val windowSize = rememberWindowSize()

    // Convert the window size to [Dp]
    val windowDpSize = with(LocalDensity.current) {
        windowSize.toDpSize()
    }

    // Calculate the window size class
    return getWindowSizeClass(windowDpSize)
}

/**
 * Partitions a [DpSize] into an enumerated [WindowSize] class.
 */
fun getWindowSizeClass(windowDpSize: DpSize): WindowSize = when {
    windowDpSize.width < 0.dp ->
        throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSize.Compact
    windowDpSize.width < 840.dp -> WindowSize.Medium
    else -> WindowSize.Expanded
}

// from https://medium.com/androiddevelopers/jetnews-for-every-screen-4d8e7927752