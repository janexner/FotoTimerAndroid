package com.exner.tools.fototimer.data.persistence

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider

class FotoTimerProcessCallback(
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
                hasLeadIn = false,
                0,
                hasAutoChain = false,
                hasPauseBeforeChain = false,
                0,
                -1L,
                keepsScreenOn = false,
                hasPreBeeps = false,
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
