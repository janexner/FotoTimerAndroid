package com.exner.tools.fototimer.data.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.exner.tools.fototimer.data.settings.FotoTimerSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FotoTimerSettingsViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private val _preferencesState = MutableStateFlow(FotoTimerSettings())
    val preferenceState: StateFlow<FotoTimerSettings> = _preferencesState.asStateFlow()

    init {
        _preferencesState.value.expertMode = sharedPreferences.getBoolean("preference_expert_mode", false)
        _preferencesState.value.useDynamicColour = sharedPreferences.getBoolean("preference_dynamic_color", true)
        _preferencesState.value.defaultKeepScreenOn = sharedPreferences.getBoolean("preference_screen_on", true)
        _preferencesState.value.stopIsEverywhere = sharedPreferences.getBoolean("preference_stop_is_everywhere", false)
        _preferencesState.value.defaultProcessTime = sharedPreferences.getLong("preference_process_time", 30L)
        _preferencesState.value.defaultIntervalTime = sharedPreferences.getLong("preference_interval_time", 10L)
        _preferencesState.value.defaultLeadInTime = sharedPreferences.getLong("preference_lead_in_time", 0L)
        _preferencesState.value.defaultPauseTime = sharedPreferences.getInt("preference_pause_time", 5)
        _preferencesState.value.pauseBeatsLeadIn = sharedPreferences.getBoolean("preference_pause_beats_lead_in", true)
        _preferencesState.value.numberOfPreBeeps = sharedPreferences.getInt("preference_pre_beeps", 4)
    }

    fun setExpertMode(newExpertMode: Boolean) {
        sharedPreferences.edit().putBoolean("preference_expert_mode", newExpertMode).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                expertMode = newExpertMode
            )
        }
    }

    fun setUseDynamicColour(newDynamicColour: Boolean) {
        sharedPreferences.edit().putBoolean("preference_dynamic_color", newDynamicColour).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                useDynamicColour =  newDynamicColour
            )
        }
    }

    fun getUseDynamicColour(): Boolean {
        return _preferencesState.value.useDynamicColour
    }

    fun setDefaultKeepScreenOn(newKeepScreenOn: Boolean) {
        sharedPreferences.edit().putBoolean("preference_screen_on", newKeepScreenOn).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                defaultKeepScreenOn = newKeepScreenOn
            )
        }
    }

    fun setStopIsEverywhere(newStopIsEverywhere: Boolean) {
        sharedPreferences.edit().putBoolean("preference_stop_is_everywhere", newStopIsEverywhere).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                stopIsEverywhere = newStopIsEverywhere
            )
        }
    }

    fun setDefaultProcessTime(newProcessTime: Long) {
        sharedPreferences.edit().putLong("preference_process_time", newProcessTime).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                defaultProcessTime = newProcessTime
            )
        }
    }

    fun setDefaultIntervalTime(newProcessTime: Long) {
        sharedPreferences.edit().putLong("preference_interval_time", newProcessTime).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                defaultIntervalTime = newProcessTime
            )
        }
    }

    fun setDefaultLeadInTime(newLeadInTime: Long) {
        sharedPreferences.edit().putLong("preference_lead_in_time", newLeadInTime).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                defaultLeadInTime = newLeadInTime
            )
        }
    }

    fun setDefaultPauseTime(newPauseTime: Int) {
        sharedPreferences.edit().putInt("preference_pause_time", newPauseTime).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                defaultPauseTime = newPauseTime
            )
        }
    }

    fun setPauseBeatsLeadIn(newPauseBeatsLeadIn: Boolean) {
        sharedPreferences.edit().putBoolean("preference_pause_beats_lead_in", newPauseBeatsLeadIn).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                pauseBeatsLeadIn = newPauseBeatsLeadIn
            )
        }
    }

    fun setNumberOfPreBeeps(newPreBeeps: Int) {
        sharedPreferences.edit().putInt("preference_pre_beeps", newPreBeeps).apply()
        _preferencesState.update { currentSettings ->
            currentSettings.copy(
                numberOfPreBeeps = newPreBeeps
            )
        }
    }
}
