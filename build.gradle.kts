// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.android.application") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
    // before updating this, check compatibilities on https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility
    // and https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    // and, obv https://github.com/google/ksp/releases
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
}
