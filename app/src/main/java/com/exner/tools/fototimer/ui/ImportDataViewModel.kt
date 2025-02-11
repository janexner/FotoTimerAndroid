package com.exner.tools.fototimer.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ImportStateConstants {
    IDLE,
    FILE_SELECTED,
    FILE_ANALYSED,
    IMPORT_FINISHED,
    ERROR
}

data class ImportState(
    val state: ImportStateConstants = ImportStateConstants.IDLE
)

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    val repository: FotoTimerProcessRepository
) : ViewModel() {

    private val _importStateFlow = MutableStateFlow(ImportState())
    val importStateFlow: StateFlow<ImportState> = _importStateFlow

    private var _file: MutableStateFlow<PlatformFile?> = MutableStateFlow(null)
    val file: StateFlow<PlatformFile?> = _file

    private val _override: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val override: StateFlow<Boolean> = _override

    private val _listOfProcessesInFile: MutableStateFlow<List<FotoTimerProcess>> = MutableStateFlow(
        emptyList()
    )
    val listOfProcessesInFile: StateFlow<List<FotoTimerProcess>> = _listOfProcessesInFile

    private val _listOfOldProcesses: MutableStateFlow<List<FotoTimerProcess>> = MutableStateFlow(
        emptyList()
    )
    val listOfOldProcesses: StateFlow<List<FotoTimerProcess>> = _listOfOldProcesses
    private val _listOfNewProcesses: MutableStateFlow<List<FotoTimerProcess>> = MutableStateFlow(
        emptyList()
    )
    val listOfNewProcesses: StateFlow<List<FotoTimerProcess>> = _listOfNewProcesses
    private val _listOfClashingProcesses: MutableStateFlow<List<FotoTimerProcess>> =
        MutableStateFlow(
            emptyList()
        )
    val listOfClashingProcesses: StateFlow<List<FotoTimerProcess>> = _listOfClashingProcesses
    val hasOverlap: Boolean = listOfClashingProcesses.value.isNotEmpty()

    private val _errorMessage: MutableStateFlow<String> = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _highestUidInProcessDB: MutableStateFlow<Long> = MutableStateFlow(-1)
    val highestUidInProcessDB: StateFlow<Long> = _highestUidInProcessDB

    fun setOverride(override: Boolean) {
        _override.value = override
    }

    fun commitImport(
        successCallback: () -> Unit
    ) {
        if (listOfNewProcesses.value.isNotEmpty()) {
            viewModelScope.launch {
                if (override.value) {
                    repository.deleteAllProcesses()
                    listOfProcessesInFile.value.forEach { process ->
                        repository.insert(process)
                    }
                } else {
                    listOfNewProcesses.value.forEach { process ->
                        repository.insert(process)
                    }
                }
                successCallback()
                _importStateFlow.value = ImportState(ImportStateConstants.IMPORT_FINISHED)
            }
        }
    }

    fun setFile(file: PlatformFile?) {
        if (file != null) {
            _file.value = file
            _importStateFlow.value = ImportState(ImportStateConstants.FILE_SELECTED)
            analyseFile(file)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun analyseFile(file: PlatformFile) {
        viewModelScope.launch {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<List<FotoTimerProcess>> =
                moshi.adapter<List<FotoTimerProcess>>()
            try {
                val fileContent = file.readBytes().toString(Charsets.UTF_8)
                Log.d("ImportDataVM", "File content: '$fileContent'")
                val newProcesses: List<FotoTimerProcess>? = jsonAdapter.fromJson(fileContent)
                // compare with existing
                if (newProcesses != null) {
                    _listOfProcessesInFile.value = newProcesses
                    val oldProcesses = repository.getAllProcesses()
                    val oldUids: MutableList<Long> = mutableListOf()
                    oldProcesses.forEach { oldProcess ->
                        oldUids.add(oldProcess.uid)
                        if (oldProcess.uid > highestUidInProcessDB.value) {
                            _highestUidInProcessDB.value = oldProcess.uid
                        }
                    }
                    _listOfOldProcesses.value = emptyList()
                    _listOfClashingProcesses.value = emptyList()
                    _listOfNewProcesses.value = emptyList()
                    newProcesses.forEach { newProcess ->
                        if (oldUids.contains(newProcess.uid)) {
                            // is it the same?
                            if (newProcess == repository.loadProcessById(newProcess.uid)) {
                                // it is the same. No need to import
                                val temp = listOfOldProcesses.value.toMutableList()
                                temp.add(newProcess)
                                _listOfOldProcesses.value = temp
                            } else {
                                val temp = listOfClashingProcesses.value.toMutableList()
                                temp.add(newProcess)
                                _listOfClashingProcesses.value = temp
                            }
                        } else {
                            val temp = listOfNewProcesses.value.toMutableList()
                            temp.add(newProcess)
                            _listOfNewProcesses.value = temp
                        }
                    }
                }
                // done
                _importStateFlow.value = ImportState(ImportStateConstants.FILE_ANALYSED)
            } catch (exception: Exception) {
                Log.d("ImportDataVM", "Exception: ${exception.message}")
                _errorMessage.value = exception.message.toString()
                _importStateFlow.value = ImportState(ImportStateConstants.ERROR)
            }
        }
    }
}
