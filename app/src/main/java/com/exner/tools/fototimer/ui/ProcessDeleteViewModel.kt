package com.exner.tools.fototimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerChainingDependencies
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ProcessDeleteViewModel.ProcessDeleteViewModelFactory::class)
class ProcessDeleteViewModel @AssistedInject constructor(
    @Assisted val processId: Long,
    private val repository: FotoTimerProcessRepository
): ViewModel() {

    private val _processName: MutableLiveData<String> = MutableLiveData("")
    val processName: LiveData<String> = _processName

    private val _processIsTarget: MutableLiveData<Boolean> = MutableLiveData(false)
    val processIsTarget: LiveData<Boolean> = _processIsTarget

    private val _processChainingDependencies: MutableLiveData<FotoTimerChainingDependencies> = MutableLiveData(null)
    val processChainingDependencies: LiveData<FotoTimerChainingDependencies> = _processChainingDependencies

    init {
        if (processId != -1L) {
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _processName.value = process.name
                    val newDependentProcesses = repository.getIdsAndNamesOfDependentProcesses(process)
                    if (newDependentProcesses.isNotEmpty()) {
                        val chainingDependencies =
                            FotoTimerChainingDependencies(newDependentProcesses)
                        _processChainingDependencies.value = chainingDependencies
                        _processIsTarget.value = true
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface ProcessDeleteViewModelFactory {
        fun create(processId: Long): ProcessDeleteViewModel
    }

    fun deleteProcess(processId: Long) {
        viewModelScope.launch {
            val ftp = repository.loadProcessById(processId)
            if (ftp != null) {
                repository.delete(ftp)
            }
        }
    }

}
