package com.exner.tools.fototimerresearch2.data.model

import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimerresearch2.sound.SoundStuff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ceil

class FotoTimerRunningProcessViewModel(private val process: FotoTimerProcess) : ViewModel() {
    private var startTime: Long = SystemClock.elapsedRealtime()
    private var stopTime = startTime + (1000L * process.processTime) + 1
    private var startSoundHasPlayed = false
    var numberOfIntervals =
        ceil(process.processTime.toDouble() / process.intervalTime.toDouble()).toLong()
    private var indexOfLastPlayedIntervalSound = 0L
    var keepsScreenOn = process.keepsScreenOn
    val prebeeps = process.hasPreBeeps
    val numPreBeeps = 4 // TODO read from sharedPreferences

    // state vars
    var processName by mutableStateOf(process.name)
        private set
    var processTime by mutableStateOf(process.processTime)
        private set
    var elapsedProcessTime by mutableStateOf(0L)
    var intervalTime by mutableStateOf(process.intervalTime)
        private set
    var elapsedIntervalTime by mutableStateOf(0L)
    var currentIntervalIndex by mutableStateOf(0L)
        private set
    var keepRunning by mutableStateOf(true)
        private set

    init {
        val updateProcess = UpdateProcess()
        viewModelScope.launch {
            while (keepRunning && (stopTime - SystemClock.elapsedRealtime()) > 0) {
                updateProcess.run()
                val w = 999L - ((SystemClock.elapsedRealtime() - startTime) % 1000L)
                if (0 < w) {
                    try {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(w)
                        }
                    } catch (e: InterruptedException) {
                        Log.d("jexner FTRPVM", "Exception while waiting 1s: ${e.localizedMessage}")
                    }
                }
            }
            val totalElapsedTime = SystemClock.elapsedRealtime() - startTime
            // one more update, for visual satisfaction
            setElapsedProcessTimeCustom(process.processTime * 1000L)
            setElapsedIntervalTimeCustom(process.intervalTime * 1000L)
            setCurrentIntervalIndexCustom(
                ceil(process.intervalTime.toDouble() / process.processTime.toDouble())
                    .toLong() + 1
            )
            // if there is an end sound, and this was not a cancellation, play end sound
            if (process.hasSoundEnd && keepRunning) {
                FotoTimerSoundPoolHolder.playSound(SoundStuff.SOUND_ID_PROCESS_END)
            }
            setKeepRunningCustom(false)
        }
    }

    inner class UpdateProcess : Runnable {
        override fun run() {
            // calculate times and rounds
            val now = SystemClock.elapsedRealtime()
            val currentProcessTime = now - startTime
            val currentIntervalTime = currentProcessTime % (1000L * intervalTime)
            currentIntervalIndex = currentProcessTime / (1000L * intervalTime)
            // update times
            setElapsedProcessTimeCustom(currentProcessTime)
            setElapsedIntervalTimeCustom(currentIntervalTime)
            // play any sounds?
            if (!startSoundHasPlayed) {
                startSoundHasPlayed = true
                // play process start sound
                if (process.hasSoundStart) {
                    FotoTimerSoundPoolHolder.playSound(SoundStuff.SOUND_ID_PROCESS_START)
                }
            }
            if (currentIntervalIndex > indexOfLastPlayedIntervalSound) {
                indexOfLastPlayedIntervalSound = currentIntervalIndex
                if (process.hasSoundInterval) {
                    FotoTimerSoundPoolHolder.playSound(SoundStuff.SOUND_ID_INTERVAL)
                }
            }
            Log.i("jexner Runnable", "running... elapsed $elapsedProcessTime")
        }
    }

    fun setElapsedProcessTimeCustom(newTime: Long) {
        elapsedProcessTime = newTime
    }

    fun setElapsedIntervalTimeCustom(newTime: Long) {
        elapsedIntervalTime = newTime
    }

    private fun setCurrentIntervalIndexCustom(newIndex: Long) {
        currentIntervalIndex = newIndex
    }

    private fun setKeepRunningCustom(newKeepRunning: Boolean) {
        keepRunning = newKeepRunning
    }

    fun cancelRunner() {
        keepRunning = false
    }
}

@Suppress("UNCHECKED_CAST")
class FotoTimerRunningProcessViewModelFactory(private val process: FotoTimerProcess) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(FotoTimerRunningProcessViewModel::class.java)) {
            return FotoTimerRunningProcessViewModel(process) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
