package com.exner.tools.fototimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProcessEditViewModel.ProcessEditViewModelFactory::class)
class ProcessEditViewModel @AssistedInject constructor(
    @Assisted val processId: Long,
    private val repository: FotoTimerProcessRepository,
): ViewModel() {

    private val _uid: MutableLiveData<Long> = MutableLiveData(-1L)
    val uid: LiveData<Long> = _uid

    private val _name: MutableLiveData<String> = MutableLiveData("Name")
    val name: LiveData<String> = _name

    private val _processTime: MutableLiveData<Int> = MutableLiveData(30)
    val processTime: LiveData<Int> = _processTime

    private val _intervalTime: MutableLiveData<Int> = MutableLiveData(10)
    val intervalTime: LiveData<Int> = _intervalTime

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

    private val _leadInSeconds: MutableLiveData<Int?> = MutableLiveData(5)
    val leadInSeconds: LiveData<Int?> = _leadInSeconds

    private val _hasLeadInSound: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasLeadInSound: LiveData<Boolean> = _hasLeadInSound

    private val _hasAutoChain: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasAutoChain: LiveData<Boolean> = _hasAutoChain

    private val _hasPauseBeforeChain: MutableLiveData<Boolean?> = MutableLiveData(false)
    val hasPauseBeforeChain: LiveData<Boolean?> = _hasPauseBeforeChain

    private val _pauseTime: MutableLiveData<Int?> = MutableLiveData(5)
    val pauseTime: LiveData<Int?> = _pauseTime

    private val _gotoId: MutableLiveData<Long?> = MutableLiveData(-1L)

    private val _nextProcessesName: MutableLiveData<String?> = MutableLiveData("")
    val nextProcessesName: LiveData<String?> = _nextProcessesName

    private val _processIdsAndNames: MutableLiveData<List<FotoTimerProcessIdAndName>> = MutableLiveData(
        emptyList()
    )
    val processIdsAndNames: LiveData<List<FotoTimerProcessIdAndName>> = _processIdsAndNames

    init {
        if (processId != -1L) {
            _uid.value = processId
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _name.value = process.name
                    _processTime.value = process.processTime
                    _intervalTime.value = process.intervalTime
                    _hasLeadIn.value = process.hasLeadIn
                    _leadInSeconds.value = process.leadInSeconds
                    _hasPreBeeps.value = process.hasPreBeeps
                    _hasAutoChain.value = process.hasAutoChain
                    _hasPauseBeforeChain.value = process.hasPauseBeforeChain ?: false
                    _pauseTime.value = process.pauseTime
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
                // get IDs and names for the "next process" drop down
                val temp = repository.loadIdsAndNamesForAllProcesses()
                _processIdsAndNames.value = temp
            }
        }
    }

    @AssistedFactory
    interface ProcessEditViewModelFactory {
        fun create(processId: Long): ProcessEditViewModel
    }

    fun commitProcess() {
        if (_uid.value != null) {
            viewModelScope.launch {
                val process = FotoTimerProcess(
                    uid = _uid.value!!.toLong(),
                    name = _name.value.toString(),
                    processTime = if (_processTime.value != null) _processTime.value!!.toInt() else 30,
                    intervalTime = if (_intervalTime.value != null) _intervalTime.value!!.toInt() else 10,
                    hasLeadIn = _hasLeadIn.value == true,
                    leadInSeconds = if (_leadInSeconds.value != null) _leadInSeconds.value!!.toInt() else null,
                    hasPreBeeps = _hasPreBeeps.value == true,
                    hasAutoChain =  _hasAutoChain.value == true,
                    hasPauseBeforeChain = _hasPauseBeforeChain.value,
                    pauseTime = if (_pauseTime.value != null) _pauseTime.value!!.toInt() else null,
                    gotoId = if (_gotoId.value != null) _gotoId.value!!.toLong() else null,
                    hasSoundStart = _hasSoundStart.value == true,
                    hasSoundEnd = _hasSoundEnd.value == true,
                    hasSoundInterval = _hasSoundInterval.value == true,
                    hasSoundMetronome = _hasSoundMetronome.value == true,
                    hasLeadInSound = _hasLeadInSound.value == true,
                    soundStartId = 0L, // TODO
                    soundEndId = 0L, // TODO
                    soundIntervalId = 0L // TODO
                )
                if (_uid.value == -1L) {
                    repository.insert(process.copy(
                        uid = 0
                    ))
                } else {
                    repository.update(process)
                }
            }
        }
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateProcessTime(processTime: Int) {
        _processTime.value = processTime
    }

    fun updateIntervalTime(intervalTime: Int) {
        _intervalTime.value = intervalTime
    }

    fun updateHasLeadIn(hasLeadIn: Boolean) {
        _hasLeadIn.value = hasLeadIn
    }

    fun updateLeadInSeconds(leadInSeconds: Int?) {
        _leadInSeconds.value = leadInSeconds
    }

    fun updateHasPreBeeps(hasPreBeeps: Boolean) {
        _hasPreBeeps.value = hasPreBeeps
    }

    fun updateHasAutoChain(hasAutoChain: Boolean) {
        _hasAutoChain.value = hasAutoChain
    }

    fun updateHasPauseBeforeChain(hasPauseBeforeChain: Boolean?) {
        _hasPauseBeforeChain.value = hasPauseBeforeChain
    }

    fun updatePauseTime(pauseTime: Int?) {
        _pauseTime.value = pauseTime
    }

    fun updateGotoId(gotoId: Long?) {
        _gotoId.value = gotoId
    }

    fun updateHasSoundStart(hasSoundStart: Boolean) {
        _hasSoundStart.value = hasSoundStart
    }

    fun updateHasSoundEnd(hasSoundEnd: Boolean) {
        _hasSoundEnd.value = hasSoundEnd
    }

    fun updateHasSoundInterval(hasSoundInterval: Boolean) {
        _hasSoundInterval.value = hasSoundInterval
    }

    fun updateHasSoundMetronome(hasSoundMetronome: Boolean) {
        _hasSoundMetronome.value = hasSoundMetronome
    }

    fun updateHasLeadInSound(hasLeadInSound: Boolean) {
        _hasLeadInSound.value = hasLeadInSound
    }

    fun updateNextProcessesName(nextProcessesName: String?) {
        _nextProcessesName.value = nextProcessesName
    }
}
