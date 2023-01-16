package com.exner.tools.fototimerresearch2.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

class FotoTimerSingleProcessViewModel() : ViewModel() {
    var name: String by mutableStateOf("Process Name")
    var processTime: String by mutableStateOf("30")
    var intervalTime: String by mutableStateOf("10")
    var hasSoundStart: Boolean by mutableStateOf(true)
    var soundStartId: Long? by mutableStateOf(1L)
    var hasSoundEnd: Boolean by mutableStateOf(true)
    var soundEndId: Long? by mutableStateOf(2L)
    var hasSoundInterval: Boolean by mutableStateOf(true)
    var soundIntervalId: Long? by mutableStateOf(3L)
    var hasSoundMetronome: Boolean by mutableStateOf(true)
    var hasLeadIn: Boolean by mutableStateOf(true)
    var leadInSeconds: String by mutableStateOf("5")
    var hasAutoChain: Boolean by mutableStateOf(true)
    var hasPauseBeforeChain: Boolean? by mutableStateOf(true)
    var pauseTime: String by mutableStateOf("3")
    var gotoId: Long? by mutableStateOf(null)
    var uid: Long by mutableStateOf(-1L)
        private set

    fun setVarsFromProcess(initialProcess: FotoTimerProcess) {
        name = initialProcess.name
        processTime = initialProcess.processTime.toString()
        intervalTime = initialProcess.intervalTime.toString()
        hasSoundStart = initialProcess.hasSoundStart
        soundStartId = initialProcess.soundStartId
        hasSoundEnd = initialProcess.hasSoundEnd
        soundEndId = initialProcess.soundEndId
        hasSoundInterval = initialProcess.hasSoundInterval
        soundIntervalId = initialProcess.soundIntervalId
        hasSoundMetronome = initialProcess.hasSoundMetronome
        hasLeadIn = initialProcess.hasLeadIn
        leadInSeconds = initialProcess.leadInSeconds.toString()
        hasAutoChain = initialProcess.hasAutoChain
        hasPauseBeforeChain = initialProcess.hasPauseBeforeChain
        pauseTime = initialProcess.pauseTime.toString()
        gotoId = initialProcess.gotoId
        uid = initialProcess.uid
    }

    fun getAsFotoTimerProcess(): FotoTimerProcess {
        return FotoTimerProcess(
            name = name,
            processTime = processTime.toLongOrNull() ?: 30L,
            intervalTime = intervalTime.toLongOrNull() ?: 10L,
            hasSoundStart = hasSoundStart,
            soundStartId = soundStartId,
            hasSoundEnd = hasSoundEnd,
            soundEndId = soundEndId,
            hasSoundInterval = hasSoundInterval,
            soundIntervalId = soundIntervalId,
            hasSoundMetronome = hasSoundMetronome,
            hasLeadIn = hasLeadIn,
            leadInSeconds = leadInSeconds.toIntOrNull() ?: 5,
            hasAutoChain = hasAutoChain,
            hasPauseBeforeChain = hasPauseBeforeChain,
            pauseTime = pauseTime.toIntOrNull() ?: 3,
            gotoId = gotoId,
            uid = uid,
        )
    }
}
