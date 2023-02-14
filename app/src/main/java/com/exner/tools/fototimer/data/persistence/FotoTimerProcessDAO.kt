package com.exner.tools.fototimer.data.persistence

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoTimerProcessDAO {
    @Query("SELECT * FROM fototimerprocess")
    fun getAll(): Flow<List<FotoTimerProcess>>

    @Query("SELECT * FROM fototimerprocess ORDER BY name ASC")
    fun getAllAlphabeticallyOrdered(): Flow<List<FotoTimerProcess>>

    @Query("SELECT uid, name FROM fototimerprocess ORDER BY name ASC")
    suspend fun getIdsAndNamesOfAllProcesses(): List<FotoTimerProcessIdAndName>

    @Query("SELECT * FROM fototimerprocess WHERE uid=:id")
    suspend fun getFotoTimerProcess(id : Long): FotoTimerProcess?

    @Query("SELECT count(uid) FROM fototimerprocess")
    suspend fun getNumberOfProcesses(): Int

    @Insert
    suspend fun insert(fotoTimerProcess: FotoTimerProcess)

    @Update
    suspend fun update(fotoTimerProcess: FotoTimerProcess)

    @Delete
    suspend fun delete(fotoTimerProcess: FotoTimerProcess)
}

// see https://developer.android.com/training/data-storage/room#kts
// see https://developer.android.com/training/data-storage/room/async-queries
// see https://stackoverflow.com/questions/46935262/get-item-by-id-in-room
