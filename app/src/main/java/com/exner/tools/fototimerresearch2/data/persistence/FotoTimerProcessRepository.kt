package com.exner.tools.fototimerresearch2.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FotoTimerProcessRepository(private val fotoTimerProcessDAO: FotoTimerProcessDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allProcesses: Flow<List<FotoTimerProcess>> =
        fotoTimerProcessDAO.getAllAlphabeticallyOrdered()

    @WorkerThread
    suspend fun loadProcessById(id: Long): FotoTimerProcess? {
        return fotoTimerProcessDAO.getFotoTimerProcess(id)
    }

    @WorkerThread
    suspend fun loadIdsAndNamesForAllProcesses(): List<FotoTimerProcessIdAndName>? {
        return fotoTimerProcessDAO.getIdsAndNamesOfAllProcesses()
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuppressModifier")
    @WorkerThread
    suspend fun insert(fotoTimerProcess: FotoTimerProcess) {
        fotoTimerProcessDAO.insert(fotoTimerProcess)
    }

    suspend fun update(fotoTimerProcess: FotoTimerProcess) {
        fotoTimerProcessDAO.update(fotoTimerProcess)
    }
}
