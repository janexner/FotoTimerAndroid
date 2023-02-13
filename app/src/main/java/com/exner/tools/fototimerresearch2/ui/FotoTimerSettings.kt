package com.exner.tools.fototimerresearch2.ui

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

sealed class SettingsTabs(val name: String) {
    object UiTab : SettingsTabs("UI")
    object TimersTab : SettingsTabs("Times")
    object SoundsTab : SettingsTabs("Sounds")
}

@Destination
@Composable
fun FotoTimerSettings() {
    val context = LocalContext.current
    val sharedSettings = PreferenceManager.getDefaultSharedPreferences(context)
    var expertMode by remember {
        mutableStateOf(
            sharedSettings.getBoolean(
                "preference_expert_mode",
                false
            )
        )
    }
    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)
    var tabIndex by remember { mutableStateOf(0) }
    val tabItems = listOf(SettingsTabs.UiTab, SettingsTabs.TimersTab, SettingsTabs.SoundsTab)

    // show vertically
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = tabIndex,
        ) {
            tabItems.forEachIndexed { index, settingsTabs ->
                Tab(
                    selected = index == tabIndex,
                    onClick = { tabIndex = index },
                    text = { BodyText(text = settingsTabs.name) }
                )
            }
        }
        when (tabIndex) {
            0 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                StandardSettingsColumn(sharedSettings = sharedSettings)
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    sharedSettings.edit().putBoolean("preference_expert_mode", it)
                        .apply()
                    expertMode = !expertMode
                }
                AnimatedVisibility(visible = expertMode) {
                    ExpertSettingsUI(sharedSettings)
                }
            }
            1 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsDefaultTimes(sharedSettings)
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    sharedSettings.edit().putBoolean("preference_expert_mode", it)
                        .apply()
                    expertMode = !expertMode
                }
            }
            2 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsSound(sharedSettings)
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    sharedSettings.edit().putBoolean("preference_expert_mode", it)
                        .apply()
                    expertMode = !expertMode
                }
            }
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

@Composable
private fun ExpertSettingsUI(sharedSettings: SharedPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        var stopIsEverywhere by remember {
            mutableStateOf(
                sharedSettings.getBoolean("preference_stop_is_everywhere", false)
            )
        }
        TextAndSwitch(
            text = "Running timer can be stopped by tapping anywhere on the screen",
            checked = stopIsEverywhere
        ) {
            sharedSettings.edit().putBoolean("preference_stop_is_everywhere", it).apply()
            stopIsEverywhere = it
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpertSettingsSound(sharedSettings: SharedPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        var preBeepsText by remember {
            mutableStateOf(
                (sharedSettings.getInt(
                    "preference_pre_beeps",
                    4
                )).toString()
            )
        }
        OutlinedTextField(
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

@Composable
private fun ExpertSettingsDefaultTimes(sharedSettings: SharedPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        var processTimeText by remember {
            mutableStateOf(
                (sharedSettings.getLong(
                    "preference_process_time",
                    30L
                )).toString()
            )
        }
        TextFieldForTimes(
            value = processTimeText,
            label = { Text(text = "Default Process time") },
            onValueChange = {
                processTimeText = it
                sharedSettings.edit()
                    .putLong(
                        "preference_process_time",
                        processTimeText.toLongOrNull() ?: 0L
                    )
                    .apply()
            }
        ) { Text(text = "30") }
        var intervalTimeText by remember {
            mutableStateOf(
                (sharedSettings.getLong(
                    "preference_interval_time",
                    10L
                )).toString()
            )
        }
        TextFieldForTimes(
            value = intervalTimeText,
            label = { Text(text = "Default Interval time") },
            onValueChange = {
                intervalTimeText = it
                sharedSettings.edit()
                    .putLong(
                        "preference_interval_time",
                        intervalTimeText.toLongOrNull() ?: 0L
                    )
                    .apply()
            },
        ) { Text(text = "10") }
        var leadInTimeText by remember {
            mutableStateOf(
                (sharedSettings.getLong(
                    "preference_lead_in_time",
                    0L
                )).toString()
            )
        }
        TextFieldForTimes(
            value = leadInTimeText,
            label = { Text(text = "Default Lead-in time") },
            onValueChange = {
                leadInTimeText = it
                sharedSettings.edit()
                    .putLong(
                        "preference_lead_in_time",
                        leadInTimeText.toLongOrNull() ?: 0L
                    )
                    .apply()
            },
        ) { Text(text = "0") }
        var pauseTimeText by remember {
            mutableStateOf(
                (sharedSettings.getLong(
                    "preference_pause_time",
                    5L
                )).toString()
            )
        }
        TextFieldForTimes(
            value = pauseTimeText,
            label = { Text(text = "Default Pause time") },
            onValueChange = {
                pauseTimeText = it
                sharedSettings.edit()
                    .putLong(
                        "preference_pause_time",
                        pauseTimeText.toLongOrNull() ?: 0L
                    )
                    .apply()
            },
        ) { Text(text = "5") }
        var pauseBeatsLeadIn by remember { mutableStateOf(sharedSettings.getBoolean("preference_pause_beats_lead_in", true)) }
        TextAndSwitch(
            text = "Pause 'beats' Lead-in",
            checked = pauseBeatsLeadIn,
            onCheckedChange = {
                pauseBeatsLeadIn = it
                sharedSettings.edit().putBoolean("preference_pause_beats_lead_in", pauseBeatsLeadIn).apply()
            }
        )
    }
}

@Composable
private fun StandardSettingsColumn(
    sharedSettings: SharedPreferences,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        var night by remember {
            mutableStateOf(
                sharedSettings.getBoolean(
                    "preference_night_mode",
                    false
                )
            )
        }
        TextAndSwitch(
            text = "Night mode (needs a restart)",
            checked = night
        ) {
            sharedSettings.edit().putBoolean("preference_night_mode", it).apply()
            night = !night
        }
        var keepScreenOn by remember {
            mutableStateOf(
                sharedSettings.getBoolean(
                    "preference_screen_on",
                    true
                )
            )
        }
        TextAndSwitch(
            text = "Default to keep screen on while counting",
            checked = keepScreenOn
        ) {
            sharedSettings.edit().putBoolean("preference_screen_on", it).apply()
            keepScreenOn = !keepScreenOn
        }
    }
}

@Preview(name = "Nexus 5", group = "Medium", showSystemUi = true, device = Devices.NEXUS_5)
@Preview(name = "Pixel 4 XL", group = "Medium", showSystemUi = true, device = Devices.PIXEL_4_XL)
@Preview(name = "Big", group = "Medium", showSystemUi = true, fontScale = 1.3f)
@Preview(
    name = "Phone normal Font",
    group = "Medium Landscape",
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Preview(
    name = "Phone big Font",
    group = "Medium Landscape",
    showSystemUi = true,
    device = "spec:width=288dp,height=608dp,dpi=560,isRound=false,chinSize=0dp,orientation=landscape",
    fontScale = 1.3f
)
@Composable
fun SettingsPreview() {
    FotoTimerTheme {
        FotoTimerSettings()
    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_C
)
@Preview(
    showSystemUi = true,
    device = Devices.TABLET
)
@Composable
fun BigSettingsPreview() {
    FotoTimerTheme {
        FotoTimerSettings()
    }
}
