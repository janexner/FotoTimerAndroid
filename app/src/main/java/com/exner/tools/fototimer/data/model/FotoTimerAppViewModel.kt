package com.exner.tools.fototimer.data.model

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FotoTimerAppViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {
    private val _darkTheme =
        MutableLiveData(sharedPreferences.getBoolean("preference_night_mode", false))
    val darkTheme: LiveData<Boolean> = _darkTheme

    fun setDarkTheme(newDarkTheme: Boolean) {
        _darkTheme.value = newDarkTheme
    }
}
