package com.exner.tools.fototimerresearch2.data.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FotoTimerSingleProcessViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository,
) : ViewModel() {
    private val processId = savedStateHandle.get<Long>("processId")
    private val initProcess = if (null != processId && processId >= 0L) getProcessById(processId) else null
    private val newProcess = FotoTimerSampleProcess.getFotoTimerSampleProcess()
    var name: String by mutableStateOf(initProcess?.name ?: newProcess.name)
    var processTime: String by mutableStateOf((initProcess?.processTime ?: newProcess.processTime).toString())
    var intervalTime: String by mutableStateOf((initProcess?.intervalTime ?: newProcess.intervalTime).toString())
    var hasSoundStart: Boolean by mutableStateOf(initProcess?.hasSoundStart ?: newProcess.hasSoundStart)
    var soundStartId: Long? by mutableStateOf(initProcess?.soundStartId ?: newProcess.soundStartId)
    var hasSoundEnd: Boolean by mutableStateOf(initProcess?.hasSoundEnd ?: newProcess.hasSoundEnd)
    var soundEndId: Long? by mutableStateOf(initProcess?.soundEndId ?: newProcess.soundEndId)
    var hasSoundInterval: Boolean by mutableStateOf(initProcess?.hasSoundInterval ?: newProcess.hasSoundInterval)
    var soundIntervalId: Long? by mutableStateOf(initProcess?.soundIntervalId ?: newProcess.soundIntervalId)
    var hasSoundMetronome: Boolean by mutableStateOf(initProcess?.hasSoundMetronome ?: newProcess.hasSoundMetronome)
    var hasLeadIn: Boolean by mutableStateOf(initProcess?.hasLeadIn ?: newProcess.hasLeadIn)
    var leadInSeconds: String by mutableStateOf((initProcess?.leadInSeconds ?: newProcess.leadInSeconds).toString())
    var hasAutoChain: Boolean by mutableStateOf(initProcess?.hasAutoChain ?: newProcess.hasAutoChain)
    var hasPauseBeforeChain: Boolean? by mutableStateOf(initProcess?.hasPauseBeforeChain ?: newProcess.hasPauseBeforeChain)
    var pauseTime: String by mutableStateOf((initProcess?.pauseTime ?: newProcess.pauseTime).toString())
    var gotoId: Long? by mutableStateOf(initProcess?.gotoId)
    var keepsScreenOn: Boolean by mutableStateOf(initProcess?.keepsScreenOn ?: newProcess.keepsScreenOn)
    var hasPreBeeps: Boolean by mutableStateOf(initProcess?.hasPreBeeps ?: newProcess.hasPreBeeps)
    var uid: Long by mutableStateOf(initProcess?.uid ?: newProcess.uid)
        private set

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
            keepsScreenOn = keepsScreenOn,
            hasPreBeeps = hasPreBeeps,
            uid = uid,
        )
    }

    private fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }

    fun getNameOfNextProcess(): String? {
        if (null != gotoId && gotoId!! >= 0) {
            val nextProcess = getProcessById(gotoId!!)
            if (nextProcess != null) {
                return nextProcess.name
            }
        }
        return null
    }
}
