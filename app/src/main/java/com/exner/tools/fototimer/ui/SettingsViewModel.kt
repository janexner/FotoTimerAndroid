package com.exner.tools.fototimer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.preferences.FotoTimerPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: FotoTimerPreferencesManager
) : ViewModel() {

    val expertMode: StateFlow<Boolean> = userPreferencesManager.expertMode().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val nightMode: StateFlow<Boolean> = userPreferencesManager.nightMode().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )
    val defaultKeepScreenOn: StateFlow<Boolean> =
        userPreferencesManager.defaultKeepScreenOn().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )
    val defaultProcessTime: StateFlow<Int> = userPreferencesManager.defaultProcessTime().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        30
    )
    val defaultIntervalTime: StateFlow<Int> = userPreferencesManager.defaultIntervalTime().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        10
    )
    val defaultLeadInTime: StateFlow<Int> = userPreferencesManager.defaultLeadInTime().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        0
    )
    val defaultPauseTime: StateFlow<Int> = userPreferencesManager.defaultPauseTime().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        5
    )
    val numberOfPreBeeps: StateFlow<Int> = userPreferencesManager.numberOfPreBeeps().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        5
    )
    val interValTimeIsCentral: StateFlow<Boolean> =
        userPreferencesManager.intervalTimeIsCentral().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )
    val vibrateEnabled: StateFlow<Boolean> = userPreferencesManager.vibrateEnabled().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false
    )

    fun updateExpertMode(newExpertMode: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setExpertMode(newExpertMode)
        }
    }

    fun updateNightMode(newNightMode: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNightMode(newNightMode)
        }
    }

    fun updateDefaultKeepScreenOn(newDefaultKeepScreenOn: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultKeepScreenOn(newDefaultKeepScreenOn)
        }
    }

    fun updateDefaultProcessTime(newDefaultProcessTime: Int) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultProcessTime(newDefaultProcessTime)
        }
    }

    fun updateDefaultIntervalTime(newDefaultIntervalTime: Int) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultIntervalTime(newDefaultIntervalTime)
        }
    }

    fun updateDefaultLeadInTime(newDefaultLeadInTime: Int) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultLeadInTime(newDefaultLeadInTime)
        }
    }

    fun updateDefaultPauseTime(newDefaultPauseTime: Int) {
        viewModelScope.launch {
            userPreferencesManager.setDefaultPauseTime(newDefaultPauseTime)
        }
    }

    fun updateNumberOfPreBeeps(newNumberOfPreBeeps: Int) {
        viewModelScope.launch {
            userPreferencesManager.setNumberOfPreBeeps(newNumberOfPreBeeps)
        }
    }

    fun updateIntervalTimeIsCentral(newIntervalTimeIsCentral: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setIntervalTimeIsCentral(newIntervalTimeIsCentral)
        }
    }

    fun updateVibrateEnabled(newVibrateEnabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setVibrateEnabled(newVibrateEnabled)
        }
    }
}