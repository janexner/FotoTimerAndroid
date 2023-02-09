package com.exner.tools.fototimerresearch2

import android.app.Application
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRoomDatabase
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class FotoTimerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // let's try dynamic colours
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}
