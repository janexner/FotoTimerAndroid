package com.exner.tools.fototimer.data.persistence

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class FotoTimerProcessRepository @Inject constructor(private val fotoTimerProcessDAO: FotoTimerProcessDAO) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allProcesses: Flow<List<FotoTimerProcess>> =
        fotoTimerProcessDAO.getAllAlphabeticallyOrdered()

    @WorkerThread
    suspend fun loadProcessById(id: Long): FotoTimerProcess? {
        return fotoTimerProcessDAO.getFotoTimerProcess(id)
    }

    @WorkerThread
    suspend fun loadIdsAndNamesForAllProcesses(): List<FotoTimerProcessIdAndName> {
        return fotoTimerProcessDAO.getIdsAndNamesOfAllProcesses()
    }

    @WorkerThread
    suspend fun getIdsAndNamesOfDependentProcesses(fotoTimerProcess: FotoTimerProcess): List<FotoTimerProcessIdAndName> {
        return fotoTimerProcessDAO.getIdsAndNamesOfDependantProcesses(fotoTimerProcess.uid)
    }

    @WorkerThread
    suspend fun insert(fotoTimerProcess: FotoTimerProcess) {
        fotoTimerProcessDAO.insert(fotoTimerProcess)
    }

    @WorkerThread
    suspend fun update(fotoTimerProcess: FotoTimerProcess) {
        fotoTimerProcessDAO.update(fotoTimerProcess)
    }

    @WorkerThread
    suspend fun delete(fotoTimerProcess: FotoTimerProcess) {
        fotoTimerProcessDAO.delete(fotoTimerProcess)
    }
}