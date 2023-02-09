package com.exner.tools.fototimerresearch2.data.model

import android.content.SharedPreferences
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimerresearch2.data.FotoTimerSampleProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcess
import com.exner.tools.fototimerresearch2.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class FotoTimerProcessLauncherViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    // get the process we are about to run
    private val processId = savedStateHandle.get<Long>("processId")
    private val process = processId?.let { getProcessById(it) }
    val noProcessSoGoBack = null == process

    init {
        if (null != process) {
            // TODO
        }
    }

    private fun getProcessById(id: Long): FotoTimerProcess? = runBlocking { repository.loadProcessById(id) }
}