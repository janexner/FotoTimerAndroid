package com.exner.tools.fototimer.data.model

import android.content.SharedPreferences
import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimer.data.FotoTimerSampleProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class FotoTimerRunningProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    // get the process we are about to run
    private val processId = savedStateHandle.get<Long>("processId")
    private val process = processId?.let { getProcessById(it) } ?: FotoTimerSampleProcess.getFotoTimerSampleProcess()
    val pauseTime = process.pauseTime

    // ticks at the beginning of all of this
    private var startTicks: Long = SystemClock.elapsedRealtime()

    // ticks at the beginning of each loop
    private var inticks: Long = 0L

    // ticks at the nearest future point where something should happen
    private var targetticks: Long = 0L

    var numberOfIntervals =
        ceil(process.processTime.toDouble() / process.intervalTime.toDouble()).toLong()
    var keepsScreenOn = process.keepsScreenOn
    val numPreBeeps = sharedPreferences.getInt("preference_pre_beeps", 4)

    // vars that are used in Screens
    var processName by mutableStateOf(process.name)
        private set
    var processGotoId by mutableStateOf(process.gotoId)
        private set
    var processTime by mutableStateOf(process.processTime.seconds)
        private set
    var timeLeftUntilEndOfProcess by mutableStateOf(Duration.ZERO)
    var intervalTime by mutableStateOf(process.intervalTime.seconds)
        private set
    var elapsedIntervalTime by mutableStateOf(Duration.ZERO)

    fun setTimeLeftUntilEndOfProcessCustom(newTime: Long) {
        timeLeftUntilEndOfProcess = newTime.milliseconds
    }

    fun setElapsedIntervalTimeCustom(newTime: Long) {
        elapsedIntervalTime = newTime.milliseconds
    }

    private fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }

}
