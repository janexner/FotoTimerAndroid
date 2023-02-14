package com.exner.tools.fototimerresearch2.data.model

import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimerresearch2.data.model.FotoTimerLoopValues.LOOP_TIME
import com.exner.tools.fototimerresearch2.data.model.FotoTimerLoopValues.TICKS_PER_DECISECOND
import com.exner.tools.fototimerresearch2.data.model.FotoTimerLoopValues.TICKS_PER_SECOND
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimerresearch2.sound.FotoTimerSoundPoolHolder
import com.exner.tools.fototimerresearch2.sound.SoundStuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class FotoTimerProcessLauncherViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository,
    sharedPreferences: SharedPreferences,
) : ViewModel() {
    // get the process we are about to run, and how
    private val processId = savedStateHandle.get<Long>("processId")
    private val process = processId?.let { getProcessById(it) }
    val noProcessSoGoBack = null == process
    private val nextState = savedStateHandle.get<Int>("nextState") ?: FotoTimerCounterState.LEADIN
    private val pause = savedStateHandle.get<Int>("pause")

    // ticks at the beginning of all of this
    private var startTicks: Long = SystemClock.elapsedRealtime()

    // ticks at the beginning of each loop
    private var inticks: Long = 0L

    // ticks at the nearest future point where something should happen
    private var targetticks: Long = 0L

    // state vars
    val counterState by mutableStateOf(FotoTimerCounterStateHolder())

    // preferences that we need
    val ticksWhileChaining = sharedPreferences.getBoolean("preference_tick_while_chaining", false)

    var keepsScreenOn = process?.keepsScreenOn ?: false

    // vars that are used in Screens
    var processName by mutableStateOf(process?.name ?: "No Process")
        private set
    private var processGotoId by mutableStateOf(process?.gotoId ?: -1L)
        private set
    var pauseTime by mutableStateOf(pause ?: 0)
        private set
    private var timeLeftUntilEndOfChain by mutableStateOf(Duration.ZERO)
    var elapsedChainTime by mutableStateOf(Duration.ZERO)

    init {
        Log.i("jexner FTPLVM", "init... $processId, $nextState")
        counterState.state = nextState

        // get a couple of values right before we loop
        var leadInOrPauseTime = 0.seconds
        if (nextState == FotoTimerCounterState.LEADIN) {
            if (process?.hasLeadIn == true) {
                leadInOrPauseTime = (process.leadInSeconds ?: 0).seconds
            }
        } else if (nextState == FotoTimerCounterState.CHAINING) {
            leadInOrPauseTime = (pause ?: 0).seconds
        }
        Log.i("jexner FTPLVM", "Lead-in/chain time $leadInOrPauseTime")
        if (0.seconds == leadInOrPauseTime) {
            setChainComplete()
        }

        val updateProcess = UpdateProcess()
        viewModelScope.launch {
            // 1 - setup
            counterState.initAtStartOfChain(
                chainTime = leadInOrPauseTime,
                counterState = nextState,
                processId = processId ?: 0L,
                nextProcessId = processGotoId
            )
            // 2 - loop
            while ((0.seconds < leadInOrPauseTime) && ((FotoTimerCounterState.CHAINING == counterState.state) || (FotoTimerCounterState.LEADIN == counterState.state))) {
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
                        Log.d("jexner FTPLVM", "Exception while waiting 1s: ${e.localizedMessage}")
                    }
                }
            }

            // 3 - done

            if (FotoTimerCounterState.CANCELLED != counterState.state) {
                setChainComplete()
//                // this will trigger chaining, if necessary
//                if (process.hasAutoChain && null != process.gotoId && process.gotoId >= 0L) {
//                    setCounterChaining()
//                }
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
                if (counterState.nextDisplay <= nextcount) {
                    // a decisecond
                    var msecs = (nextcount - counterState.countBase).milliseconds

                    // update display
                    if (Duration.ZERO == msecs) {
                        msecs = (process?.pauseTime ?: 0).seconds
                    }
                    setElapsedChainTimeCustom(msecs.inWholeMilliseconds)
                    setTimeLeftUntilEndOfChainCustom(
                        startTicks + ((process?.pauseTime ?: 0) * TICKS_PER_SECOND) - inticks + 1000
                    )

                    counterState.nextDisplay += TICKS_PER_DECISECOND
                }
                if (counterState.nextBeep <= nextcount) {
                    // a second
                    // ticks?
                    if (SoundStuff.SOUND_ID_NO_SOUND == soundId) {
                        if (ticksWhileChaining) {
                            soundId = SoundStuff.SOUND_ID_TICK_WHILE_CHAINING
                        }
                        counterState.nextBeep += TICKS_PER_DECISECOND * 10
                    }
                }
            }
            // play any sounds?
            if (soundId != SoundStuff.SOUND_ID_NO_SOUND) {
                FotoTimerSoundPoolHolder.playSound(soundId)
            }
        }
    }

    private fun setElapsedChainTimeCustom(inWholeMilliseconds: Long) {
        elapsedChainTime = inWholeMilliseconds.milliseconds
    }

    private fun setTimeLeftUntilEndOfChainCustom(timeLeft: Long) {
        timeLeftUntilEndOfChain = timeLeft.milliseconds
    }

    private fun setChainComplete() {
        counterState.state = FotoTimerCounterState.COMPLETED
    }

    fun cancelRunner() {
        counterState.state = FotoTimerCounterState.CANCELLED
    }

    private fun getProcessById(id: Long): FotoTimerProcess? =
        runBlocking { repository.loadProcessById(id) }
}