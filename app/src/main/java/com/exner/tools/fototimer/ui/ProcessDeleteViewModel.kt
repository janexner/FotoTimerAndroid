package com.exner.tools.fototimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerChainingDependencies
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessDeleteViewModel @Inject constructor(
    private val repository: FotoTimerProcessRepository
): ViewModel() {

    private val _processName: MutableLiveData<String> = MutableLiveData("")
    val processName: LiveData<String> = _processName

    private val _processIsTarget: MutableLiveData<Boolean> = MutableLiveData(false)
    val processIsTarget: LiveData<Boolean> = _processIsTarget

    private val _processChainingDependencies: MutableLiveData<FotoTimerChainingDependencies> = MutableLiveData(null)
    val processChainingDependencies: LiveData<FotoTimerChainingDependencies> = _processChainingDependencies

    fun checkProcess(processId: Long) {
        if (processId != -1L) {
            viewModelScope.launch {
                val process = repository.loadProcessById(processId)
                if (process != null) {
                    _processName.value = process.name
                    val newDependentProcesses = repository.getIdsAndNamesOfDependentProcesses(process)
                    val chainingDependencies = FotoTimerChainingDependencies(true, newDependentProcesses)
                    _processChainingDependencies.value = chainingDependencies
                    _processIsTarget.value = true
                }
            }
        }
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