package com.exner.tools.fototimer.ui

import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context
): ViewModel() {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _expertMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val expertMode: LiveData<Boolean> = _expertMode

    private val _nightMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val nightMode: LiveData<Boolean> = _nightMode

    private val _useDynamicColour: MutableLiveData<Boolean> = MutableLiveData(true)
    val useDynamicColour: LiveData<Boolean> = _useDynamicColour

    private val _defaultKeepScreenOn: MutableLiveData<Boolean> = MutableLiveData(true)
    val defaultKeepScreenOn: LiveData<Boolean> = _defaultKeepScreenOn

    private val _stopIsEverywhere: MutableLiveData<Boolean> = MutableLiveData(false)
    val stopIsEverywhere: LiveData<Boolean> = _stopIsEverywhere

    private val _defaultProcessTime: MutableLiveData<Int> = MutableLiveData(30)
    val defaultProcessTime: LiveData<Int> = _defaultProcessTime

    private val _defaultIntervalTime: MutableLiveData<Int> = MutableLiveData(10)
    val defaultIntervalTime: LiveData<Int> = _defaultIntervalTime

    private val _defaultLeadInTime: MutableLiveData<Int> = MutableLiveData(0)
    val defaultLeadInTime: LiveData<Int> = _defaultLeadInTime

    private val _defaultPauseTime: MutableLiveData<Int> = MutableLiveData(0)
    val defaultPauseTime: LiveData<Int> = _defaultPauseTime

    private val _pauseBeatsLeadIn: MutableLiveData<Boolean> = MutableLiveData(true)
    val pauseBeatsLeadIn: LiveData<Boolean> = _pauseBeatsLeadIn

    private val _numberOfPreBeeps: MutableLiveData<Int> = MutableLiveData(4)
    val numberOfPreBeeps: LiveData<Int> = _numberOfPreBeeps

    private val _intervalTimeIsCentral: MutableLiveData<Boolean> = MutableLiveData(false)
    val interValTimeIsCentral: LiveData<Boolean> = _intervalTimeIsCentral

    init {
        _expertMode.value = sharedPreferences.getBoolean("preference_expert_mode", false)
        _nightMode.value = sharedPreferences.getBoolean("preference_night_mode", false)
        _useDynamicColour.value = sharedPreferences.getBoolean("preference_dynamic_color", true)
        _defaultKeepScreenOn.value = sharedPreferences.getBoolean("preference_screen_on", true)
        _stopIsEverywhere.value = sharedPreferences.getBoolean("preference_stop_is_everywhere", false)
        _defaultProcessTime.value = sharedPreferences.getInt("preference_process_time", 30)
        _defaultIntervalTime.value = sharedPreferences.getInt("preference_interval_time", 10)
        _defaultLeadInTime.value = sharedPreferences.getInt("preference_lead_in_time", 0)
        _defaultPauseTime.value = sharedPreferences.getInt("preference_pause_time", 0)
        _pauseBeatsLeadIn.value = sharedPreferences.getBoolean("preference_pause_beats_lead_in", true)
        _numberOfPreBeeps.value = sharedPreferences.getInt("preference_pre_beeps", 4)
        _intervalTimeIsCentral.value = sharedPreferences.getBoolean("preference_interval_time_is_central", false)
    }

    fun updateExpertMode(newExpertMode: Boolean) {
        sharedPreferences.edit().putBoolean("preference_expert_mode", newExpertMode).apply()
        _expertMode.value = newExpertMode
    }

    fun updateNightMode(newNightMode: Boolean) {
        sharedPreferences.edit().putBoolean("preference_night_mode", newNightMode).apply()
        _nightMode.value = newNightMode
    }

    fun updateUseDynamicColour(newUseDynamicColour: Boolean) {
        sharedPreferences.edit().putBoolean("preference_dynamic_color", newUseDynamicColour).apply()
        _useDynamicColour.value = newUseDynamicColour
    }

    fun updateDefaultKeepScreenOn(newDefaultKeepScreenOn: Boolean) {
        sharedPreferences.edit().putBoolean("preference_screen_on", newDefaultKeepScreenOn).apply()
        _defaultKeepScreenOn.value = newDefaultKeepScreenOn
    }

    fun updateStopIsEverywhere(newStopIsEverywhere: Boolean) {
        sharedPreferences.edit().putBoolean("preference_stop_is_everywhere", newStopIsEverywhere).apply()
        _stopIsEverywhere.value = newStopIsEverywhere
    }

    fun updateDefaultProcessTime(newDefaultProcessTime: Int) {
        sharedPreferences.edit().putInt("preference_process_time", newDefaultProcessTime).apply()
        _defaultProcessTime.value = newDefaultProcessTime
    }

    fun updateDefaultIntervalTime(newDefaultIntervalTime: Int) {
        sharedPreferences.edit().putInt("preference_interval_time", newDefaultIntervalTime).apply()
        _defaultIntervalTime.value = newDefaultIntervalTime
    }

    fun updateDefaultLeadInTime(newDefaultLeadInTime: Int) {
        sharedPreferences.edit().putInt("preference_lead_in_time", newDefaultLeadInTime).apply()
        _defaultLeadInTime.value = newDefaultLeadInTime
    }

    fun updateDefaultPauseTime(newDefaultPauseTime: Int) {
        sharedPreferences.edit().putInt("preference_pause_time", newDefaultPauseTime).apply()
        _defaultPauseTime.value = newDefaultPauseTime
    }

    fun updatePauseBeatsLeadIn(newPauseBeatsLeadIn: Boolean) {
        sharedPreferences.edit().putBoolean("preference_pause_beats_lead_in", newPauseBeatsLeadIn).apply()
        _pauseBeatsLeadIn.value = newPauseBeatsLeadIn
    }

    fun updateNumberOfPreBeeps(newNumberOfPreBeeps: Int) {
        sharedPreferences.edit().putInt("preference_pre_beeps", newNumberOfPreBeeps).apply()
        _numberOfPreBeeps.value = newNumberOfPreBeeps
    }

    fun updateIntervalTimeIsCentral(newIntervalTimeIsCentral: Boolean) {
        sharedPreferences.edit().putBoolean("preference_interval_time_is_central", newIntervalTimeIsCentral).apply()
        _intervalTimeIsCentral.value = newIntervalTimeIsCentral
    }
}