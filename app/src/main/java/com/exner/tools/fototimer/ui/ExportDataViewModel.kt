package com.exner.tools.fototimer.ui

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimer.data.persistence.tools.RootData
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportDataViewModel @Inject constructor(
    val repository: FotoTimerProcessRepository
) : ViewModel() {

    val allProcesses = repository.allProcesses

    @OptIn(ExperimentalStdlibApi::class)
    fun commitExport(context: Context) {
        viewModelScope.launch {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val jsonAdapter: JsonAdapter<RootData> =
                moshi.adapter<RootData>()
            val data = bundleData()
            val json: String = jsonAdapter.toJson(data)
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "foto-timer-export")
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri).use { stream ->
                    stream?.write(json.encodeToByteArray())
                }
            }
        }
    }

    private suspend fun bundleData(): RootData {
        val processes = repository.getAllProcesses()
        val data = RootData(processes)
        return data
    }
}
