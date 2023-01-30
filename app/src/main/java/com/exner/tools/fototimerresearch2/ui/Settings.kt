package com.exner.tools.fototimerresearch2.ui

import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.preference.PreferenceManager
import com.exner.tools.fototimerresearch2.ui.theme.FotoTimerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(
    windowSize: WindowSize,
    modifier: Modifier = Modifier
) {
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
    val isExpandedScreen = windowSize == WindowSize.Expanded

    // what's the orientation, right now?
    val configuration = LocalConfiguration.current
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // show horizontally
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.weight(0.58f)) {
                    StandardSettingsColumn(sharedSettings = sharedSettings)
                    TextAndSwitch(
                        text = "Expert mode (more options everywhere)",
                        checked = expertMode,
                        onCheckedChange = {
                            sharedSettings.edit().putBoolean("preference_expert_mode", it).apply()
                            expertMode = !expertMode
                        }
                    )
//                    AnimatedVisibility(visible = expertMode) {
                        ExpertSettingsSound(sharedSettings = sharedSettings)
//                    }
                }
                Spacer(modifier = Modifier.weight(0.04f))
                Column(modifier = Modifier.weight(0.38f)) {
//                    AnimatedVisibility(visible = expertMode) {
                            ExpertSettingsDefaultTimes(sharedSettings = sharedSettings)
//                  }
                }
            }
        }
        else -> {
            // show vertically
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                StandardSettingsColumn(sharedSettings = sharedSettings)
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode,
                    onCheckedChange = {
                        sharedSettings.edit().putBoolean("preference_expert_mode", it).apply()
                        expertMode = !expertMode
                    }
                )
                AnimatedVisibility(visible = expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsDefaultTimes(sharedSettings)
                        ExpertSettingsSound(sharedSettings)
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
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExpertSettingsSound(sharedSettings: SharedPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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

@Composable
private fun ExpertSettingsDefaultTimes(sharedSettings: SharedPreferences) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
        TextFieldForTimes(
            value = processTimeText,
            label = { Text(text = "Default Process time") },
            placeholder = { Text(text = "30") },
            onValueChange = {
                processTimeText = it
                sharedSettings.edit()
                    .putLong(
                        "preference_process_time",
                        processTimeText.toLongOrNull() ?: 0L
                    )
                    .apply()
            }
        )
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
            placeholder = { Text(text = "10") },
        )
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
            placeholder = { Text(text = "0") },
        )
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
            placeholder = { Text(text = "5") },
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
        HeaderText(
            text = "UI",
        )
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
            checked = night,
            onCheckedChange = {
                sharedSettings.edit().putBoolean("preference_night_mode", it).apply()
                night = !night
            }
        )
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
            checked = keepScreenOn,
            onCheckedChange = {
                sharedSettings.edit().putBoolean("preference_screen_on", it).apply()
                keepScreenOn = !keepScreenOn
            }
        )
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
        Settings(WindowSize.Medium)
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
        Settings(WindowSize.Expanded)
    }
}
