plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.exner.tools.fototimer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.exner.tools.fototimer"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
        }
        debug {
            isDebuggable = true
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.6"
        // see https://developer.android.com/jetpack/androidx/releases/compose-kotlin
    }
    buildToolsVersion = "34.0.0"
}

kotlin {
    jvmToolchain(17)

    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    // now sorted by https://developer.android.com/jetpack/androidx/releases

    // jetpack
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.legacy.support.core.utils)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewModelCompose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.window.manager)

    // hilt
    implementation(libs.hilt.android)
    implementation(libs.datastore.core)
    implementation(libs.androidx.core.ktx)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Room components
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // others
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material)

    // compose destinations
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
}

hilt {
    enableAggregatingTask = true
}

ksp {
    arg("room.schemaLocation", "$projectDir/src/main/assets/schemas")
    arg("room.incremental"   , "true")
}
