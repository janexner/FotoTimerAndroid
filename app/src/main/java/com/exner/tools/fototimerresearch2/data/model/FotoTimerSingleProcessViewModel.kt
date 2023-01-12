package com.exner.tools.fototimerresearch2.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess

class FotoTimerSingleProcessViewModel() : ViewModel() {
    var name: String by mutableStateOf("Process Name")
    var processTime: Long by mutableStateOf(30L)
    var intervalTime: Long by mutableStateOf(10L)
    var hasSoundStart: Boolean by mutableStateOf(true)
    var soundStartId: Long? by mutableStateOf(1L)
    var hasSoundEnd: Boolean by mutableStateOf(true)
    var soundEndId: Long? by mutableStateOf(2L)
    var hasSoundInterval: Boolean by mutableStateOf(true)
    var soundIntervalId: Long? by mutableStateOf(3L)
    var hasSoundMetronome: Boolean by mutableStateOf(true)
    var hasLeadIn: Boolean by mutableStateOf(true)
    var leadInSeconds: Int? by mutableStateOf(5)
    var hasAutoChain: Boolean by mutableStateOf(true)
    var hasPauseBeforeChain: Boolean? by mutableStateOf(true)
    var pauseTime: Int? by mutableStateOf(3)
    var gotoId: Long? by mutableStateOf(null)
    var uid: Long by mutableStateOf(-1L)
        private set

    fun setVarsFromProcess(initialProcess: FotoTimerProcess) {
        name = initialProcess.name
        processTime = initialProcess.processTime
        intervalTime = initialProcess.intervalTime
        hasSoundStart = initialProcess.hasSoundStart
        soundStartId = initialProcess.soundStartId
        hasSoundEnd = initialProcess.hasSoundEnd
        soundEndId = initialProcess.soundEndId
        hasSoundInterval = initialProcess.hasSoundInterval
        soundIntervalId = initialProcess.soundIntervalId
        hasSoundMetronome = initialProcess.hasSoundMetronome
        hasLeadIn = initialProcess.hasLeadIn
        leadInSeconds = initialProcess.leadInSeconds
        hasAutoChain = initialProcess.hasAutoChain
        hasPauseBeforeChain = initialProcess.hasPauseBeforeChain
        pauseTime = initialProcess.pauseTime
        gotoId = initialProcess.gotoId
        uid = initialProcess.uid
    }
}
