package com.exner.tools.fototimerresearch2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    var expertMode by remember { mutableStateOf(sharedSettings.getBoolean("preference_expert_mode", false)) }

    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderText(
            text = "UI",
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BodyText(
                    text = "Night mode (needs a restart)",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                var night by remember { mutableStateOf(sharedSettings.getBoolean("preference_night_mode", false)) }
                Switch(
                    checked = night,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onCheckedChange = {
                        sharedSettings.edit().putBoolean("preference_night_mode", it).apply()
                        night = !night
                    }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BodyText(
                    text = "Default to keep screen on while counting",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                var keepScreenOn by remember { mutableStateOf(sharedSettings.getBoolean("preference_screen_on", true)) }
                Switch(
                    checked = keepScreenOn,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onCheckedChange = {
                        sharedSettings.edit().putBoolean("preference_screen_on", it).apply()
                        keepScreenOn = !keepScreenOn
                    }
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                BodyText(
                    text = "Expert mode (more options everywhere)",
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Switch(
                    checked = expertMode,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onCheckedChange = {
                        sharedSettings.edit().putBoolean("preference_expert_mode", it).apply()
                        expertMode = !expertMode
                    }
                )
            }
        }
        if (expertMode) {
            HeaderText(
                text = "Times"
            )
            var processTimeText by remember {
                mutableStateOf(
                    (sharedSettings.getLong(
                        "preference_process_time",
                        30L
                    )).toString()
                )
            }
            TextField(
                value = processTimeText,
                label = { Text(text = "Default Process time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    processTimeText = it
                    sharedSettings.edit()
                        .putLong("preference_process_time", processTimeText.toLongOrNull() ?: 0L)
                        .apply()
                },
                placeholder = { Text(text = "30") },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            var intervalTimeText by remember {
                mutableStateOf(
                    (sharedSettings.getLong(
                        "preference_interval_time",
                        10L
                    )).toString()
                )
            }
            TextField(
                value = intervalTimeText,
                label = { Text(text = "Default Interval time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    intervalTimeText = it
                    sharedSettings.edit()
                        .putLong("preference_interval_time", intervalTimeText.toLongOrNull() ?: 0L)
                        .apply()
                },
                placeholder = { Text(text = "10") },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            var leadInTimeText by remember {
                mutableStateOf(
                    (sharedSettings.getLong(
                        "preference_lead_in_time",
                        0L
                    )).toString()
                )
            }
            TextField(
                value = leadInTimeText,
                label = { Text(text = "Default Lead-in time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    leadInTimeText = it
                    sharedSettings.edit()
                        .putLong("preference_lead_in_time", leadInTimeText.toLongOrNull() ?: 0L)
                        .apply()
                },
                placeholder = { Text(text = "0") },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            var pauseTimeText by remember {
                mutableStateOf(
                    (sharedSettings.getLong(
                        "preference_pause_time",
                        5L
                    )).toString()
                )
            }
            TextField(
                value = pauseTimeText,
                label = { Text(text = "Default Pause time") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    pauseTimeText = it
                    sharedSettings.edit()
                        .putLong("preference_pause_time", pauseTimeText.toLongOrNull() ?: 0L)
                        .apply()
                },
                placeholder = { Text(text = "5") },
                textStyle = MaterialTheme.typography.bodyLarge
            )
            HeaderText(
                text = "Sound"
            )
            var preBeepsText by remember {
                mutableStateOf(
                    (sharedSettings.getInt(
                        "preference_pre_beeps",
                        4
                    )).toString()
                )
            }
            TextField(
                value = preBeepsText,
                label = { Text(text = "Pre-beeps (small beeps before the interval)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = {
                    preBeepsText = it
                    sharedSettings.edit()
                        .putInt("preference_pre_beeps", preBeepsText.toIntOrNull() ?: 0)
                        .apply()
                },
                placeholder = { Text(text = "30") },
                textStyle = MaterialTheme.typography.bodyLarge
            )
        }
    }
    // invisible preferences TODO
    // metronome sound
    // lead-in sound
    // default sounds
    // - process start
    // - process end
    // - interval
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    FotoTimerTheme {
        Settings()
    }
}
