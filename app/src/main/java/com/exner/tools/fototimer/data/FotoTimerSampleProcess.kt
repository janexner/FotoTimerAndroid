package com.exner.tools.fototimer.data

import com.exner.tools.fototimer.data.persistence.FotoTimerProcess

object FotoTimerSampleProcess {
    fun getFotoTimerSampleProcess(
        name: String = "Test Process 1",
        processTime: Int = 30,
        intervalTime: Int = 10,
        hasSoundStart: Boolean = true,
        soundStartId: Long = 0L,
        hasSoundEnd: Boolean = true,
        soundEndId: Long = 0L,
        hasSoundInterval: Boolean = true,
        soundIntervalId: Long = 0,
        hasSoundMetronome: Boolean = false,
        hasLeadIn: Boolean = false,
        leadInSeconds: Int = 0,
        hasAutoChain: Boolean = false,
        hasPauseBeforeChain: Boolean = false,
        pauseTime: Int = 0,
        gotoId: Long = -1L,
        keepsScreenOn: Boolean = false,
        hasPreBeeps: Boolean = false,
    ): FotoTimerProcess {
        return FotoTimerProcess(
            name = name,
            processTime = processTime,
            intervalTime = intervalTime,
            hasSoundStart = hasSoundStart,
            soundStartId = soundStartId,
            hasSoundEnd = hasSoundEnd,
            soundEndId = soundEndId,
            hasSoundInterval = hasSoundInterval,
            soundIntervalId = soundIntervalId,
            hasSoundMetronome = hasSoundMetronome,
            hasLeadIn = hasLeadIn,
            leadInSeconds = leadInSeconds,
            hasAutoChain = hasAutoChain,
            hasPauseBeforeChain = hasPauseBeforeChain,
            pauseTime = pauseTime,
            gotoId = gotoId,
            keepsScreenOn = keepsScreenOn,
            hasPreBeeps = hasPreBeeps
        )
    }
}
