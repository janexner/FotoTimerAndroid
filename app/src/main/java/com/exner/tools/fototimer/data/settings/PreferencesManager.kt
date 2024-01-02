package com.exner.tools.fototimer.data.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val USER_PREFERENCES_NAME = "user_preferences"

private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

private object PreferencesKeys {
    val EXPERT_MODE = booleanPreferencesKey("expertMode")
    val NIGHT_MODE = booleanPreferencesKey("nightMode")
    val DEFAULT_KEEP_SCREEN_ON = booleanPreferencesKey("defaultKeepScreenOn")
    val STOP_IS_EVERYWHERE = booleanPreferencesKey("stopIsEverywhere")
    val DEFAULT_PROCESS_TIME = intPreferencesKey("defaultProcessTime")
    val DEFAULT_INTERVAL_TIME = intPreferencesKey("defaultIntervalTime")
    val DEFAULT_LEAD_IN_TIME = intPreferencesKey("defaultLeadInTime")
    val DEFAULT_PAUSE_TIME = intPreferencesKey("defaultPauseTime")
    val PAUSE_BEATS_LEAD_IN = booleanPreferencesKey("pauseBeatsLeadIn")
    val NUMBER_OF_PRE_BEEPS = intPreferencesKey("numberOfPreBeeps")
    val INTERVAL_TIME_IS_CENTRAL = booleanPreferencesKey("intervalTimeIsCentral")
}

@Singleton //You can ignore this annotation as return `datastore` from `preferencesDataStore` is singleton
class PreferencesManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val preferencesDataStore = appContext.dataStore

    suspend fun setExpertMode(mode: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.EXPERT_MODE] = mode
        }
    }

    val expertMode: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.EXPERT_MODE] ?: false
    }

    suspend fun setNightMode(mode: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.NIGHT_MODE] = mode
        }
    }

    val nightMode: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.NIGHT_MODE] ?: false
    }

    suspend fun setDefaultKeepScreenOn(keepOn: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_KEEP_SCREEN_ON] = keepOn
        }
    }

    val defaultKeepScreenOn: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_KEEP_SCREEN_ON] ?: true
    }

    suspend fun setStopIsEverywhere(everywhere: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.STOP_IS_EVERYWHERE] = everywhere
        }
    }

    val stopIsEverywhere: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.STOP_IS_EVERYWHERE] ?: false
    }

    suspend fun setDefaultProcessTime(time: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PROCESS_TIME] = time
        }
    }

    val defaultProcessTime: Flow<Int> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_PROCESS_TIME] ?: 30
    }

    suspend fun setDefaultIntervalTime(time: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_INTERVAL_TIME] = time
        }
    }

    val defaultIntervalTime: Flow<Int> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_INTERVAL_TIME] ?: 10
    }

    suspend fun setDefaultLeadInTime(time: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_LEAD_IN_TIME] = time
        }
    }

    val defaultLeadInTime: Flow<Int> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_LEAD_IN_TIME] ?: 5
    }

    suspend fun setDefaultPauseTime(time: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.DEFAULT_PAUSE_TIME] = time
        }
    }

    val defaultPauseTime: Flow<Int> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.DEFAULT_PAUSE_TIME] ?: 30
    }

    suspend fun setPauseBeatsLeadIn(beats: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.PAUSE_BEATS_LEAD_IN] = beats
        }
    }

    val pauseBeatsLeadIn: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.PAUSE_BEATS_LEAD_IN] ?: true
    }

    suspend fun setNumberOfPreBeeps(number: Int) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.NUMBER_OF_PRE_BEEPS] = number
        }
    }

    val numberOfPreBeeps: Flow<Int> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.NUMBER_OF_PRE_BEEPS] ?: 4
    }

    suspend fun setIntervalTimeIsCentral(central: Boolean) {
        preferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.INTERVAL_TIME_IS_CENTRAL] = central
        }
    }

    val intervalTimeIsCentral: Flow<Boolean> = preferencesDataStore.data.map { preferences ->
        preferences[PreferencesKeys.INTERVAL_TIME_IS_CENTRAL] ?: false
    }
}
