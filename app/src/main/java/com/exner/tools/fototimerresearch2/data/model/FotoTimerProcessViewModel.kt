package com.exner.tools.fototimerresearch2.data.model

import androidx.lifecycle.*
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FotoTimerProcessViewModel(private val repository: FotoTimerProcessRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allProcesses: LiveData<List<FotoTimerProcess>> =
        repository.allProcesses.asLiveData()

    fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(fotoTimerProcess: FotoTimerProcess) =
        viewModelScope.launch { repository.insert(fotoTimerProcess) }

}

class FotoTimerProcessViewModelFactory(private val repository: FotoTimerProcessRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FotoTimerProcessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FotoTimerProcessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
