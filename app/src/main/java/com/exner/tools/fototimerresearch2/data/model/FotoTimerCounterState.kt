package com.exner.tools.fototimerresearch2.data.model

import android.os.SystemClock

object FotoTimerCounterState {
    val CANCELLED = 0
    val COMPLETED = 1
    val IDLE = 2
    val COUNTING = 3
    val PAUSED = 4
}

class FotoTimerCounterStateHolder {
    var state = FotoTimerCounterState.IDLE
    var countbase = 0L
    var roundNumber = 0
    var nextFinish = 0L
    var nextRound = 0L
    var nextDisplay = 0L
    var nextBeep = 0L
//    var nextPauseDisplay = 0L
//    var nextPauseFinish = 0L
//    var inChain = false
//    var chainTime = 0L
    var startProcessId = 0L
//    var posInChain = 0
//    var stopReason = 0;

    fun initAtStartOfProcess(processTime: Long, intervalTime: Long, processId: Long) {
        state = FotoTimerCounterState.COUNTING
        countbase = SystemClock.elapsedRealtime()
        roundNumber = 1
        nextFinish = countbase + 1000L * processTime
        nextRound = countbase + 1000L * intervalTime
        nextDisplay = countbase + 100L
        startProcessId = processId
    }
}
