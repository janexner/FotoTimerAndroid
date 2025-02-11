package com.exner.tools.fototimer.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

enum class ImportStateConstants {
    IDLE,
    FILE_SELECTED,
    FILE_READ,
    IMPORT_FINISHED
}

data class ImportState(
    val state: ImportStateConstants = ImportStateConstants.IDLE
)

@HiltViewModel
class ImportDataViewModel @Inject constructor(
    repository: FotoTimerProcessRepository
) : ViewModel() {

    private val _importStateFlow = MutableStateFlow(ImportState())
    val importStateFlow: StateFlow<ImportState> = _importStateFlow

    private var _file: MutableStateFlow<PlatformFile?> = MutableStateFlow<PlatformFile?>(null)
    val file: StateFlow<PlatformFile?> = _file

    fun commitImport() {
        // TODO
    }

    fun setFile(file: PlatformFile?) {
        if (file != null) {
            _file.value = file
            _importStateFlow.value = ImportState(ImportStateConstants.FILE_SELECTED)
        }
    }
}
