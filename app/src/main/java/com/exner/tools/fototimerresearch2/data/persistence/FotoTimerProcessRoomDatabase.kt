package com.exner.tools.fototimerresearch2.data.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [FotoTimerProcess::class],
    version = 3,
    exportSchema = false
)
public abstract class FotoTimerProcessRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): FotoTimerProcessDAO

    companion object {
        // Singleton to prevent multiple DBs being opened
        @Volatile
        private var INSTANCE: FotoTimerProcessRoomDatabase? = null;

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN keeps_screen_on INTEGER NOT NULL DEFAULT TRUE;")
            }
        }

        fun getDatabase(context: Context, scope: CoroutineScope): FotoTimerProcessRoomDatabase {
            // check that INSTANCE is not null, then return it.
            // Otherwise, create it first
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FotoTimerProcessRoomDatabase::class.java,
                    "foto_timer_process_database"
                ).addCallback(ProcessDatabaseCallback(scope)).addMigrations(MIGRATION_2_3).build()
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
                    populateDatabaseWithSampleProcesses(database.processDAO())
                }
            }
        }

        suspend fun populateDatabaseWithSampleProcesses(fotoTimerProcessDao: FotoTimerProcessDAO) {
            // Add sample words.
            var fotoTimerProcess =
                FotoTimerProcess(
                    "Test Process 1",
                    30,
                    10,
                    true,
                    0,
                    hasSoundEnd = true,
                    0,
                    hasSoundInterval = true,
                    0,
                    hasSoundMetronome = false,
                    hasLeadIn = false,
                    0,
                    hasAutoChain = false,
                    hasPauseBeforeChain = false,
                    0,
                    -1L,
                    keepsScreenOn = false
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
                    hasAutoChain = false,
                    hasPauseBeforeChain = false,
                    0,
                    -1L,
                    keepsScreenOn = true
                )
            fotoTimerProcessDao.insert(fotoTimerProcess)
        }
    }
}
