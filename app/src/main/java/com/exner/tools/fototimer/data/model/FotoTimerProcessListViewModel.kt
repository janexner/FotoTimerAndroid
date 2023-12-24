package com.exner.tools.fototimer.data.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FotoTimerProcessListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository
) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allProcesses: LiveData<List<FotoTimerProcess>> =
        repository.allProcesses.asLiveData()

    fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }

    fun getIdsAndNamesOfAllProcesses(): List<FotoTimerProcessIdAndName> =
        runBlocking { repository.loadIdsAndNamesForAllProcesses() }

    fun getRepository(): FotoTimerProcessRepository =
        repository

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(fotoTimerProcess: FotoTimerProcess) =
        viewModelScope.launch { repository.insert(fotoTimerProcess) }

    fun update(fotoTimerProcess: FotoTimerProcess) =
        viewModelScope.launch { repository.update(fotoTimerProcess) }
}
