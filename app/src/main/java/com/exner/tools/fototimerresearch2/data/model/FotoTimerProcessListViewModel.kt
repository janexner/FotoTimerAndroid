package com.exner.tools.fototimerresearch2.data.model

import androidx.lifecycle.*
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessIdAndName
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FotoTimerProcessListViewModel @Inject constructor(
    val savedStateHandle: SavedStateHandle,
    val repository: FotoTimerProcessRepository
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

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(fotoTimerProcess: FotoTimerProcess) =
        viewModelScope.launch { repository.insert(fotoTimerProcess) }

    fun update(fotoTimerProcess: FotoTimerProcess) =
        viewModelScope.launch { repository.update(fotoTimerProcess) }
}
