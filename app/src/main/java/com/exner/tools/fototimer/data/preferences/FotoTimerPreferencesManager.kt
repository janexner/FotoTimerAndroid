package com.exner.tools.fototimer.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.exner.tools.fototimer.ui.theme.Theme
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("preferences")

@Singleton
class FotoTimerPreferencesManager @Inject constructor(
    @ApplicationContext appContext: Context
) {
    private val userDataStorePreferences = appContext.dataStore

    fun expertMode(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_EXPERT_MODE] ?: false
        }
    }

    suspend fun setExpertMode(newExpertMode: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_EXPERT_MODE] = newExpertMode
        }
    }

    fun theme(): Flow<Theme> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            val wasDark = preferences[KEY_NIGHT_MODE] ?: false
            val default = if (wasDark) Theme.Dark.name else Theme.Auto.name
            Theme.valueOf(preferences[KEY_THEME] ?: default)
        }
    }

    suspend fun setTheme(newTheme: Theme) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_THEME] = newTheme.name
        }
    }

    fun defaultProcessTime(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_DEFAULT_PROCESS_TIME] ?: 30
        }
    }

    suspend fun setDefaultProcessTime(newDefaultProcessTime: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_DEFAULT_PROCESS_TIME] = newDefaultProcessTime
        }
    }

    fun defaultIntervalTime(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_DEFAULT_INTERVAL_TIME] ?: 5
        }
    }

    suspend fun setDefaultIntervalTime(newDefaultProcessTime: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_DEFAULT_INTERVAL_TIME] = newDefaultProcessTime
        }
    }

    fun defaultLeadInTime(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_DEFAULT_LEAD_IN_TIME] ?: 5
        }
    }

    suspend fun setDefaultLeadInTime(newDefaultLeadInTime: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_DEFAULT_LEAD_IN_TIME] = newDefaultLeadInTime
        }
    }

    fun defaultPauseTime(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_DEFAULT_PAUSE_TIME] ?: 5
        }
    }

    suspend fun setDefaultPauseTime(newDefaultPauseTime: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_DEFAULT_PAUSE_TIME] = newDefaultPauseTime
        }
    }

    fun numberOfPreBeeps(): Flow<Int> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_NUMBER_OF_PRE_BEEPS] ?: 5
        }
    }

    suspend fun setNumberOfPreBeeps(newNumberOfPreBeeps: Int) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_NUMBER_OF_PRE_BEEPS] = newNumberOfPreBeeps
        }
    }

    fun intervalTimeIsCentral(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_INTERVAL_TIME_IS_CENTRAL] ?: false
        }
    }

    suspend fun setIntervalTimeIsCentral(newVibrateEnabled: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_INTERVAL_TIME_IS_CENTRAL] = newVibrateEnabled
        }
    }

    fun vibrateEnabled(): Flow<Boolean> {
        return userDataStorePreferences.data.catch {
            emit(emptyPreferences())
        }.map { preferences ->
            preferences[KEY_VIBRATE_ENABLED] ?: false
        }
    }

    suspend fun setVibrateEnabled(newVibrateEnabled: Boolean) {
        userDataStorePreferences.edit { preferences ->
            preferences[KEY_VIBRATE_ENABLED] = newVibrateEnabled
        }
    }

    private companion object {
        val KEY_EXPERT_MODE = booleanPreferencesKey(name = "preference_expert_mode")
        val KEY_NIGHT_MODE = booleanPreferencesKey(name = "preference_night_mode")
        val KEY_DEFAULT_PROCESS_TIME = intPreferencesKey(name = "preference_process_time")
        val KEY_DEFAULT_INTERVAL_TIME = intPreferencesKey(name = "preference_interval_time")
        val KEY_DEFAULT_LEAD_IN_TIME = intPreferencesKey(name = "preference_lead_in_time")
        val KEY_DEFAULT_PAUSE_TIME = intPreferencesKey(name = "preference_pause_time")
        val KEY_NUMBER_OF_PRE_BEEPS = intPreferencesKey(name = "preference_pre_beeps")
        val KEY_INTERVAL_TIME_IS_CENTRAL = booleanPreferencesKey(name = "preference_interval_time_is_central")
        val KEY_VIBRATE_ENABLED = booleanPreferencesKey(name = "preference_vibrate_enabled")
        val KEY_THEME = stringPreferencesKey(name = "preference_theme")
    }
}
