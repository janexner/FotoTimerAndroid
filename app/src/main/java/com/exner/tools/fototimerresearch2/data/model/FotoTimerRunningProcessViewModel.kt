package com.exner.tools.fototimerresearch2.data.model

import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimerresearch2.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimerresearch2.sound.SoundStuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class FotoTimerRunningProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository,
) : ViewModel() {
    // get the process we are about to run
    private val processId = savedStateHandle.get<Long>("processId")
    private val process = processId?.let { getProcessById(it) } ?: FotoTimerSampleProcess.getFotoTimerSampleProcess()

    // ticks per "loop" - 50L would b 0.05s, roughly
    private val LOOP_TIME: Duration = 50.milliseconds
    private val TICKS_PER_SECOND = 1000L
    private val TICKS_PER_DECISECOND = 100L
    private val FACTOR_MILLIS = 1000L

    // ticks at the beginning of all of this
    private var startTicks: Long = SystemClock.elapsedRealtime()

    // ticks at the beginning of each loop
    private var inticks: Long = 0L

    // ticks at the nearest future point where something should happen
    private var targetticks: Long = 0L

    var numberOfIntervals =
        ceil(process.processTime.toDouble() / process.intervalTime.toDouble()).toLong()
    var keepsScreenOn = process.keepsScreenOn
    val numPreBeeps = 4 // TODO read from sharedPreferences

    // state vars
    val counterState by mutableStateOf(FotoTimerCounterStateHolder())

    // vars that are used in Screens
    var processName by mutableStateOf(process.name)
        private set

    var processTime by mutableStateOf(process.processTime.seconds)
        private set
    var timeLeftUntilEndOfProcess by mutableStateOf(Duration.ZERO)
    var intervalTime by mutableStateOf(process.intervalTime.seconds)
        private set
    var elapsedIntervalTime by mutableStateOf(Duration.ZERO)

    init {
        val updateProcess = UpdateProcess()
        viewModelScope.launch {
            // 1 - setup
            counterState.initAtStartOfProcess(
                process.processTime.seconds,
                process.intervalTime.seconds,
                process.uid
            )
            // play process start sound
            if (process.hasSoundStart) {
                FotoTimerSoundPoolHolder.playSound(SoundStuff.SOUND_ID_PROCESS_START)
            }
            // 2 - loop
            while (FotoTimerCounterState.COUNTING == counterState.state) {
                // 2.1 - note when this iteration of the loop started
                inticks = SystemClock.elapsedRealtime()
                // set target for next loop to default (1/10th s in the future)
                targetticks = inticks + TICKS_PER_DECISECOND

                // 2.2 - do what needs to be done
                // - update values
                // - play sounds
                updateProcess.run()
                // ATTENTION - side effects! counterState will be updated within updateProcess!
                // TODO see whether that can be avoided
                if (targetticks > counterState.nextDisplay) {
                    targetticks = counterState.nextDisplay
                }
                if (targetticks > counterState.nextRound) {
                    targetticks = counterState.nextRound
                }
                if ((counterState.countBase < counterState.nextFinish) && (targetticks > counterState.nextFinish)) {
                    targetticks = counterState.nextFinish
                }

                // 2.3 - calculate wait time
                val targetDuration =
                    (targetticks - inticks).milliseconds // calculate time left for next target
                var looptime: Duration
                if (LOOP_TIME < targetDuration) {
                    looptime = targetDuration.div(LOOP_TIME).milliseconds
                    if (Duration.ZERO == looptime) {
                        looptime = LOOP_TIME
                    }
                } else {
                    looptime = targetDuration
                }

                // 2.4 - wait
                if (looptime.isPositive()) {
                    try {
                        withContext(Dispatchers.IO) {
                            Thread.sleep(looptime.inWholeMilliseconds)
                        }
                    } catch (e: InterruptedException) {
                        Log.d("jexner FTRPVM", "Exception while waiting 1s: ${e.localizedMessage}")
                    }
                }
            }

            // 3 - done
            // one more update, for visual satisfaction
            setTimeLeftUntilEndOfProcessCustom(process.processTime * FACTOR_MILLIS)
            setElapsedIntervalTimeCustom(process.intervalTime * FACTOR_MILLIS)
            setCurrentIntervalIndexCustom(
                ceil(process.intervalTime.toDouble() / process.processTime.toDouble())
                    .toInt() + 1
            )
            // if there is an end sound, and this was not a cancellation, play end sound
            if (process.hasSoundEnd && FotoTimerCounterState.COMPLETED == counterState.state) {
                FotoTimerSoundPoolHolder.playSound(SoundStuff.SOUND_ID_PROCESS_END)
            }
            setCounterComplete()

            // 4 - chain
            // if there is a next process, start it
            if (process.hasAutoChain && null != process.gotoId && process.gotoId >= 0L) {
                // onStartNextProcess.invoke()
            }
        }
    }

    inner class UpdateProcess : Runnable {
        override fun run() {
            // calculate times and rounds
            var soundId = SoundStuff.SOUND_ID_NO_SOUND
            // nextcount, will help calculate targetticks
            val nextcount = SystemClock.elapsedRealtime()
            if ((counterState.countBase < counterState.nextFinish) && (counterState.nextFinish <= nextcount)) {
                // all counters are done!
                counterState.state = FotoTimerCounterState.COMPLETED
            } else {
                var msecs = Duration.ZERO
                if (counterState.nextRound <= nextcount) {
                    // interval done
                    if (process.hasSoundInterval) {
                        soundId = SoundStuff.SOUND_ID_INTERVAL
                    }
                    counterState.countBase += intervalTime.inWholeMilliseconds
                    counterState.nextRound += intervalTime.inWholeMilliseconds
                    counterState.roundNumber++
                }
                if (counterState.nextDisplay <= nextcount) {
                    // a decisecond
                    msecs = (nextcount - counterState.countBase).milliseconds
                    var oldTime = msecs

                    // update display
                    if (Duration.ZERO == msecs) {
                        msecs = process.intervalTime.seconds
                    }
                    setElapsedIntervalTimeCustom(msecs.inWholeMilliseconds)
                    if (0L < process.processTime) {
                        oldTime += ((counterState.roundNumber - 1L) * process.intervalTime).seconds
                    }
                    setTimeLeftUntilEndOfProcessCustom(startTicks + (process.processTime * TICKS_PER_SECOND) - inticks + 1000)

                    counterState.nextDisplay += TICKS_PER_DECISECOND
                }
                if (counterState.nextBeep <= nextcount) {
                    // a second

                    // intro beeps? metronome?
                    if (SoundStuff.SOUND_ID_NO_SOUND == soundId) {
                        if ((msecs >= (process.processTime - 10 * numPreBeeps).seconds) && (msecs < process.intervalTime.seconds) && process.hasPreBeeps) {
                            soundId = SoundStuff.SOUND_ID_PRE_BEEPS
                        } else if (process.hasSoundMetronome) {
                            soundId = SoundStuff.SOUND_ID_METRONOME
                        }
                        counterState.nextBeep += TICKS_PER_DECISECOND * 10
                    }
                }
            }
            // play any sounds?
            FotoTimerSoundPoolHolder.playSound(soundId)
        }
    }

    fun chainToNextProcess(processId: Long) {

    }

    fun setTimeLeftUntilEndOfProcessCustom(newTime: Long) {
        timeLeftUntilEndOfProcess = newTime.milliseconds
    }

    fun setElapsedIntervalTimeCustom(newTime: Long) {
        elapsedIntervalTime = newTime.milliseconds
    }

    private fun setCurrentIntervalIndexCustom(newIndex: Int) {
        counterState.roundNumber = newIndex
    }

    private fun setCounterComplete() {
        counterState.state = FotoTimerCounterState.COMPLETED
    }

    fun cancelRunner() {
        counterState.state = FotoTimerCounterState.CANCELLED
    }

    private fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }

}
