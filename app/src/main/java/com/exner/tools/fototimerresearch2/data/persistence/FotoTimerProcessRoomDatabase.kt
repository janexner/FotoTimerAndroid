package com.exner.tools.fototimerresearch2.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = arrayOf(FotoTimerProcess::class),
    version = 2,
    exportSchema = false
)
public abstract class FotoTimerProcessRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): FotoTimerProcessDAO

    companion object {
        // Singleton to prevent multiple DBs being opened
        @Volatile
        private var INSTANCE: FotoTimerProcessRoomDatabase? = null;

        fun getDatabase(context: Context, scope: CoroutineScope): FotoTimerProcessRoomDatabase {
            // check that INSTANCE is not null, then return it.
            // Otherwise, create it first
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FotoTimerProcessRoomDatabase::class.java,
                    "foto_timer_process_database"
                ).addCallback(ProcessDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class ProcessDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.processDAO())
                }
            }
        }

        suspend fun populateDatabase(fotoTimerProcessDao: FotoTimerProcessDAO) {
            // Add sample words.
            var fotoTimerProcess =
                FotoTimerProcess(
                    "Test Process 1",
                    30,
                    10,
                    false,
                    0,
                    hasSoundEnd = false,
                    0,
                    hasSoundInterval = false,
                    0,
                    hasSoundMetronome = false,
                    hasLeadIn = false,
                    0,
                    false,
                    false,
                    0,
                    0
                )
            fotoTimerProcessDao.insert(fotoTimerProcess)
            fotoTimerProcess =
                FotoTimerProcess(
                    "Test Process 2",
                    15,
                    5,
                    false,
                    0,
                    hasSoundEnd = false,
                    0,
                    hasSoundInterval = false,
                    0,
                    hasSoundMetronome = true,
                    hasLeadIn = false,
                    0,
                    false,
                    false,
                    0,
                    0
                )
            fotoTimerProcessDao.insert(fotoTimerProcess)
        }
    }
}
