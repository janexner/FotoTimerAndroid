package com.exner.tools.fototimerresearch2

import android.app.Application
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class FotoTimerApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { FotoTimerProcessRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { FotoTimerProcessRepository(database.processDAO()) }
}
