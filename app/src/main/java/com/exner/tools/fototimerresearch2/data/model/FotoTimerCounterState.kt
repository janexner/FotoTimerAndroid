package com.exner.tools.fototimerresearch2.data.model

import android.os.SystemClock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


object FotoTimerCounterState {
    const val CANCELLED = 0
    const val COMPLETED = 1
    const val IDLE = 2
    const val COUNTING = 3

    //    const val PAUSED = 4
    const val CHAINING = 5
    const val LEADIN = 6
}

class FotoTimerCounterStateHolder {
    var state = FotoTimerCounterState.IDLE
    var countBase = 0L
    var roundNumber = 0
    var nextFinish = 0L
    var nextRound = 0L
    var nextDisplay = 0L
    var nextBeep = 0L

    //    var nextPauseDisplay = 0L
//    var nextPauseFinish = 0L
//    var inChain = false
//    var chainTime = 0L
    private var startProcessId = 0L
    private var nextProcessId = 0L
//    var posInChain = 0
//    var stopReason = 0;

    fun initAtStartOfProcess(processTime: Duration, intervalTime: Duration, processId: Long) {
        state = FotoTimerCounterState.COUNTING
        countBase = SystemClock.elapsedRealtime()
        roundNumber = 1
        nextFinish = countBase + processTime.inWholeMilliseconds
        nextRound = countBase + intervalTime.inWholeMilliseconds
        nextDisplay = countBase + 100L
        startProcessId = processId
    }

    fun initAtStartOfChain(chainTime: Duration, counterState: Int, processId: Long, nextProcessId: Long) {
            state = counterState
        countBase = SystemClock.elapsedRealtime()
        nextFinish = countBase + chainTime.inWholeMilliseconds
        nextDisplay = countBase + 100L
        startProcessId = processId
        this.nextProcessId = nextProcessId
    }
}

object FotoTimerLoopValues {
    // ticks per "loop" - 50L would b 0.05s, roughly
    val LOOP_TIME: Duration = 50.milliseconds
    val TICKS_PER_SECOND = 1000L
    val TICKS_PER_DECISECOND = 100L
    val FACTOR_MILLIS = 1000L
}
