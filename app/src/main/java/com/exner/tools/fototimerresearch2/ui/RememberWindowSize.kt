package com.exner.tools.fototimerresearch2.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.window.layout.WindowMetricsCalculator

// Copyright 2022 Google LLC.
// SPDX-License-Identifier: Apache-2.0
/**
 * Remembers the [Size] in pixels of the window corresponding to the current window
 * metrics.
 */
@Composable
fun Activity.rememberWindowSize(): Size {
    val configuration = LocalConfiguration.current
    // WindowMetricsCalculator implicitly depends on the configuration
    // through the activity, so re-calculate it upon changes.
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate()
            .computeCurrentWindowMetrics(this)
    }
    return windowMetrics.bounds.toComposeRect().size
}

// from https://medium.com/androiddevelopers/jetnews-for-every-screen-4d8e7927752