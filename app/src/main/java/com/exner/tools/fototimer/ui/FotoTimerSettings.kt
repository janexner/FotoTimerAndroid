package com.exner.tools.fototimer.ui

import android.content.pm.ActivityInfo
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.data.model.FotoTimerSettingsViewModel
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

sealed class SettingsTabs(val name: String) {
    object UiTab : SettingsTabs("UI")
    object TimersTab : SettingsTabs("Times")
    object SoundsTab : SettingsTabs("Sounds")
}

@Destination
@Composable
fun FotoTimerSettings(
    settingsViewModel: FotoTimerSettingsViewModel = hiltViewModel()
) {
    val fotoTimerSettings by settingsViewModel.preferenceState.collectAsState()

    Log.i("jexner Settings", "SVM: $settingsViewModel")

    // unlock screen rotation
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR)
    var tabIndex by rememberSaveable { mutableIntStateOf(0) }
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
                StandardSettingsColumn(
                    fotoTimerSettings.useDynamicColour,
                    { settingsViewModel.setUseDynamicColour(it) },
                    fotoTimerSettings.defaultKeepScreenOn,
                    { settingsViewModel.setDefaultKeepScreenOn(it) },
                )
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = fotoTimerSettings.expertMode
                ) {
                    settingsViewModel.setExpertMode(it)
                }
                AnimatedVisibility(visible = fotoTimerSettings.expertMode) {
                    ExpertSettingsUI(
                        fotoTimerSettings.stopIsEverywhere
                    ) { settingsViewModel.setStopIsEverywhere(it) }
                }
            }
            1 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = fotoTimerSettings.expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsDefaultTimes(
                            fotoTimerSettings.defaultProcessTime, { settingsViewModel.setDefaultProcessTime(it) },
                            fotoTimerSettings.defaultIntervalTime, { settingsViewModel.setDefaultIntervalTime(it) },
                            fotoTimerSettings.defaultLeadInTime, { settingsViewModel.setDefaultLeadInTime(it) },
                            fotoTimerSettings.defaultPauseTime, { settingsViewModel.setDefaultPauseTime(it) },
                            fotoTimerSettings.pauseBeatsLeadIn, { settingsViewModel.setPauseBeatsLeadIn(it) }
                        )
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = fotoTimerSettings.expertMode
                ) {
                    settingsViewModel.setExpertMode(it)
                }
            }
            2 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = fotoTimerSettings.expertMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsSound(
                            fotoTimerSettings.numberOfPreBeeps
                        ) { settingsViewModel.setNumberOfPreBeeps(it) }
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = fotoTimerSettings.expertMode
                ) {
                    settingsViewModel.setExpertMode(it)
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
private fun ExpertSettingsUI(
    stopIsEverywhere: Boolean, updateStopIsEverywhere: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAndSwitch(
            text = "Running timer can be stopped by tapping anywhere on the screen",
            checked = stopIsEverywhere
        ) {
            updateStopIsEverywhere(it)
        }
    }
}

@Composable
private fun ExpertSettingsSound(
    preBeeps: Int, setPreBeeps: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = preBeeps.toString(),
            label = { Text(text = "Pre-beeps (small beeps before the interval)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            onValueChange = {
                val newPreBeeps = it.toIntOrNull() ?: 0
                setPreBeeps(newPreBeeps)
            },
            placeholder = { Text(text = "4") },
            textStyle = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ExpertSettingsDefaultTimes(
    processTime: Long, setProcessTime: (Long) -> Unit,
    intervalTime: Long, setIntervalTime: (Long) -> Unit,
    leadInTime: Long, setLeadInTime: (Long) -> Unit,
    pauseTime: Int, setPauseTime: (Int) -> Unit,
    pauseBeatsLeadIn: Boolean, setPauseBeatsLeadIn: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextFieldForTimes(
            value = processTime.toString(),
            label = { Text(text = "Default Process time") },
            onValueChange = {
                val newProcessTime = it.toLongOrNull() ?: 0
                setProcessTime(newProcessTime)
            }
        ) { Text(text = "30") }
        TextFieldForTimes(
            value = intervalTime.toString(),
            label = { Text(text = "Default Interval time") },
            onValueChange = {
                val newIntervalTime = it.toLongOrNull() ?: 0
                setIntervalTime(newIntervalTime)
            },
        ) { Text(text = "10") }
        TextFieldForTimes(
            value = leadInTime.toString(),
            label = { Text(text = "Default Lead-in time") },
            onValueChange = {
                val newLeadInTime = it.toLongOrNull() ?: 0
                setLeadInTime(newLeadInTime)
            },
        ) { Text(text = "0") }
        TextFieldForTimes(
            value = pauseTime.toString(),
            label = { Text(text = "Default Pause time") },
            onValueChange = {
                val newPauseTime = it.toIntOrNull() ?: 0
                setPauseTime(newPauseTime)
            },
        ) { Text(text = "5") }
        TextAndSwitch(
            text = "Pause 'beats' Lead-in",
            checked = pauseBeatsLeadIn,
            onCheckedChange = {
                setPauseBeatsLeadIn(it)
            }
        )
    }
}

@Composable
private fun StandardSettingsColumn(
    dynamicColor: Boolean,
    updateDynamicColor: (Boolean) -> Unit,
    keepScreenOn: Boolean,
    updateKeepScreenOn: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAndSwitch(
            text = "Dynamic colours (needs a restart)",
            checked = dynamicColor,
            onCheckedChange = {
                updateDynamicColor(it)
            }
        )
        TextAndSwitch(
            text = "Default to keep screen on while counting",
            checked = keepScreenOn
        ) {
            updateKeepScreenOn(it)
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
