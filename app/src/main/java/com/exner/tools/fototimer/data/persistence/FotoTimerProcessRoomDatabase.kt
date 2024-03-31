package com.exner.tools.fototimer.data.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FotoTimerProcess::class],
    version = 6,
    exportSchema = false
)
abstract class FotoTimerProcessRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): FotoTimerProcessDAO

}
