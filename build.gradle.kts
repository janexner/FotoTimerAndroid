// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false

    // before updating this, check compatibilities on https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility
    // and https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    // and, obv https://github.com/google/ksp/releases
}
