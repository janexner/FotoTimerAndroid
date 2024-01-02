package com.exner.tools.fototimer.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessDAO
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppComponent {

    @Singleton
    @Provides
    fun provideDao(ftDatabase: FotoTimerProcessRoomDatabase): FotoTimerProcessDAO =
        ftDatabase.processDAO()

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        provider: Provider<FotoTimerProcessDAO>
    ): FotoTimerProcessRoomDatabase =
        Room.databaseBuilder(
            context.applicationContext,
            FotoTimerProcessRoomDatabase::class.java,
            "foto_timer_process_database"
        ).addCallback(ProcessDatabaseCallback(provider))
            .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5).build()

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

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE FotoTimerProcess ADD COLUMN has_lead_in_sound INTEGER NOT NULL DEFAULT FALSE;")
        }
    }

    class ProcessDatabaseCallback(
        private val provider: Provider<FotoTimerProcessDAO>
    ) : RoomDatabase.Callback() {

        private val applicationScope = CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch(Dispatchers.IO) {
                populateDatabaseWithSampleProcesses()
            }
        }

        private suspend fun populateDatabaseWithSampleProcesses() {
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
                    hasLeadIn = true,
                    5,
                    hasAutoChain = true,
                    hasPauseBeforeChain = true,
                    5,
                    2L,
                    keepsScreenOn = true,
                    hasPreBeeps = true,
                )
            provider.get().insert(fotoTimerProcess)
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
            provider.get().insert(fotoTimerProcess)
        }
    }
}
