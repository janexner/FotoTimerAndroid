// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0-alpha13")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files

        // ignoring the above
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.46.1")
        // because https://dagger.dev/hilt/gradle-setup.html told me so
    }
}

plugins {
    id("com.google.dagger.hilt.android") version "2.46.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.20" apply false
}
