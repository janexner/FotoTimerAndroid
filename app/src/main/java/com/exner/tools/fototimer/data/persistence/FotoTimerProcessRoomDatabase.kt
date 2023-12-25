package com.exner.tools.fototimer.data.persistence

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
    version = 4,
    exportSchema = false
)
abstract class FotoTimerProcessRoomDatabase : RoomDatabase() {
    abstract fun processDAO(): FotoTimerProcessDAO

    companion object {
        // Singleton to prevent multiple DBs being opened
        @Volatile
        private var INSTANCE: FotoTimerProcessRoomDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN keeps_screen_on INTEGER NOT NULL DEFAULT TRUE;")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN has_pre_beeps INTEGER NOT NULL DEFAULT FALSE;")
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
                ).addCallback(ProcessDatabaseCallback(scope))
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class ProcessDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

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
                    keepsScreenOn = false,
                    hasPreBeeps = false,
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
                    keepsScreenOn = true,
                    hasPreBeeps = false,
                )
            fotoTimerProcessDao.insert(fotoTimerProcess)
        }
    }
}