package com.exner.tools.fototimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessDetailsViewModel @Inject constructor(
    private val repository: FotoTimerProcessRepository
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _processTime: MutableLiveData<String> = MutableLiveData("30")
    val processTime: LiveData<String> = _processTime

    private val _intervalTime: MutableLiveData<String> = MutableLiveData("10")
    val intervalTime: LiveData<String> = _intervalTime

    private val _hasSoundStart: MutableLiveData<Boolean> = MutableLiveData(true)
    val hasSoundStart: LiveData<Boolean> = _hasSoundStart

    private val _hasSoundEnd: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasSoundEnd: LiveData<Boolean> = _hasSoundEnd

    private val _hasSoundInterval: MutableLiveData<Boolean> = MutableLiveData(true)
    val hasSoundInterval: LiveData<Boolean> = _hasSoundInterval

    private val _hasSoundMetronome: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasSoundMetronome: LiveData<Boolean> = _hasSoundMetronome

    private val _hasPreBeeps: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasPreBeeps: LiveData<Boolean> = _hasPreBeeps

    private val _hasLeadIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasLeadIn: LiveData<Boolean> = _hasLeadIn

    private val _leadInSeconds: MutableLiveData<String> = MutableLiveData("5")
    val leadInSeconds: LiveData<String> = _leadInSeconds

    private val _hasLeadInSound: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasLeadInSound: LiveData<Boolean> = _hasLeadInSound

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _hasPauseBeforeChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasPauseBeforeChain: LiveData<Boolean> = _hasPauseBeforeChain

    private val _pauseTime: MutableLiveData<String> = MutableLiveData("5")
    val pauseTime: LiveData<String> = _pauseTime

    private val _gotoId: MutableLiveData<Long> = MutableLiveData(-1L)
    val gotoId: LiveData<Long> = _gotoId

    private val _nextProcessesName: MutableLiveData<String> = MutableLiveData("")
    val nextProcessesName: LiveData<String> = _nextProcessesName

    fun getProcess(processId: Long) {
        if (processId != -1L) {
            _uid.value = processId
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _name.value = process.name
                    _processTime.value = process.processTime.toString()
                    _intervalTime.value = process.intervalTime.toString()
                    _hasLeadIn.value = process.hasLeadIn
                    _leadInSeconds.value = process.leadInSeconds.toString()
                    _hasPreBeeps.value = process.hasPreBeeps
                    _hasAutoChain.value = process.hasAutoChain
                    _hasPauseBeforeChain.value = process.hasPauseBeforeChain ?: false
                    _pauseTime.value = process.pauseTime.toString()
                    _gotoId.value = process.gotoId ?: -1L
                    _hasSoundStart.value = process.hasSoundStart
                    _hasSoundEnd.value = process.hasSoundEnd
                    _hasSoundInterval.value = process.hasSoundInterval
                    _hasSoundMetronome.value = process.hasSoundMetronome
                    _hasLeadInSound.value = process.hasLeadInSound
                    if (process.gotoId != null && process.gotoId != -1L) {
                        val nextProcess = repository.loadProcessById(process.gotoId)
                        if (nextProcess != null) {
                            _nextProcessesName.value = nextProcess.name
                        }
                    }
                }
            }
        }
    }
}
