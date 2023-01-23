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
import kotlinx.coroutines.*

@OptIn(ExperimentalCoroutinesApi::class)
class FotoTimerRunningProcessViewModel(private val process: FotoTimerProcess) : ViewModel() {
    private var startTime: Long = SystemClock.elapsedRealtime()
    private var stopTime = startTime + (1000L * process.processTime) + 1
    private var startSoundHasPlayed = false
    var numberOfIntervals =
        Math.ceil(process.processTime.toDouble() / process.intervalTime.toDouble()).toLong()
    private var indexOfLastPlayedIntervalSound = 0L

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
        val updateProcess: UpdateProcess = UpdateProcess()
        Log.i("jexner FTRPVM", "Launching in vm scope...")
        viewModelScope.launch {
            while (keepRunning && (stopTime - SystemClock.elapsedRealtime()) > 0) {
                updateProcess.run()
                val w = 999L - ((SystemClock.elapsedRealtime() - startTime) % 1000L)
                if (0 < w) {
                    Log.i("jexner Timer", "Going to wait for $w millis")
                    try {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(w)
                        }
                    } catch (e: InterruptedException) {
                        // TODO
                    }
                } else {
                    Log.i("jexner Timer", "We're late, no waiting!")
                }
            }
            val totalElapsedTime = SystemClock.elapsedRealtime() - startTime
            Log.i("jexner Timer", "done. Elapsed $totalElapsedTime.")
            // one more update, for visual satisfaction
            setElapsedProcessTimeCustom(process.processTime * 1000L)
            setElapsedIntervalTimeCustom(process.intervalTime * 1000L)
            setCurrentIntervalIndexCustom(
                Math.ceil(process.intervalTime.toDouble() / process.processTime.toDouble())
                    .toLong() + 1
            )
            setKeepRunningCustom(false)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("jexner FTRPVM", "Clearing (destroying)...")
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
                    // TODO
                }
            }
            if (currentIntervalIndex > indexOfLastPlayedIntervalSound) {
                indexOfLastPlayedIntervalSound = currentIntervalIndex
                if (process.hasSoundInterval) {
                    // TODO
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

    fun setCurrentIntervalIndexCustom(newIndex: Long) {
        currentIntervalIndex = newIndex
    }

    fun setKeepRunningCustom(newKeepRunnin: Boolean) {
        keepRunning = newKeepRunnin
    }

    fun cancelRunner() {
        Log.i("jexner FTRPVM", "Cancel Runner requested.")
        keepRunning = false
    }
}

class FotoTimerRunningProcessViewModelFactory(private val process: FotoTimerProcess) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(FotoTimerRunningProcessViewModel::class.java)) {
            Log.i(
                "jexner RunningProcessViewModelFactory",
                "Returning FTRPVM for process ${process.uid}..."
            )
            return FotoTimerRunningProcessViewModel(process) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
