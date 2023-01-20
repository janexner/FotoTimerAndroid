package com.exner.tools.fototimerresearch2.data.model

import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
class FotoTimerRunningProcessViewModel(private val process: FotoTimerProcess) : ViewModel() {
    private val startTime: Long = SystemClock.elapsedRealtime()
    private lateinit var runnerThread: Thread
    private var keepRunning: Boolean = true

    // state vars
    var processName by mutableStateOf(process.name)
        private set
    var elapsedProcessTime by mutableStateOf(0L)

    init {
        snapshotFlow { elapsedProcessTime }
            .mapLatest { elapsedProcessTime }
            .onEach { elapsedProcessTime = it }
            .launchIn(viewModelScope)
    }

    inner class UpdateProcess : Runnable {
        override fun run() {
            val now = SystemClock.elapsedRealtime()
            elapsedProcessTime = now - startTime
            Log.i("jexner Runnable", "running... elapsed $elapsedProcessTime")
        }
    }

    private fun workEverySecond(r: Runnable): Thread {
        val stopTime = startTime + (1000L * process.processTime)
        val t = Thread {
            while (keepRunning && (stopTime - SystemClock.elapsedRealtime()) > 0) {
                r.run()
                val w = 999L - ((SystemClock.elapsedRealtime() - startTime) % 1000L)
                if (0 < w) {
                    Log.i("jexner Timer", "Going to wait for $w millis")
                    try {
                        Thread.sleep(w)
                    } catch (e: InterruptedException) {
                        // TODO
                    }
                } else {
                    Log.i("jexner Timer", "We're late, no waiting!")
                }
            }
            val totalElapsedTime = SystemClock.elapsedRealtime() - startTime
            Log.i("jexner Timer", "done. Elapsed $totalElapsedTime.")
        }
        Log.i(
            "jexner RunningProcessViewModel",
            "Start $startTime, Stop $stopTime. Waiting for start command..."
        )
        return t
    }

    fun startRunner() {
        // start the "timer"
        Log.i("jexner RunningProcessViewModel", "Creating Runner and Timer...")
        val updateProcess: UpdateProcess = UpdateProcess()
        runnerThread = workEverySecond(updateProcess)

        Log.i("jexner RunningProcessViewModel", "Starting Runner now...")
        runnerThread.start()
    }

    fun cancelRunner() {
        Log.i("jexner RunningProcessViewModel", "Cancel Runner requested.")
        keepRunning = false
    }
}
