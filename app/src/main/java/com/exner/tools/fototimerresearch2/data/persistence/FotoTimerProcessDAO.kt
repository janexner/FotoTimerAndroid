package com.exner.tools.fototimerresearch2.data.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FotoTimerProcessDAO {
    @Query("SELECT * FROM fototimerprocess")
    fun getAll(): Flow<List<FotoTimerProcess>>

    @Query("SELECT * FROM fototimerprocess ORDER BY name ASC")
    fun getAllAlphabeticallyOrdered(): Flow<List<FotoTimerProcess>>

    @Query("SELECT * FROM fototimerprocess WHERE uid=:id")
    fun getFotoTimerProcess(id : Long): FotoTimerProcess?

    @Insert
    suspend fun insert(fotoTimerProcess: FotoTimerProcess)

    @Delete
    suspend fun delete(fotoTimerProcess: FotoTimerProcess)
}

// see https://developer.android.com/training/data-storage/room#kts
// see https://developer.android.com/training/data-storage/room/async-queries
// see https://stackoverflow.com/questions/46935262/get-item-by-id-in-room
