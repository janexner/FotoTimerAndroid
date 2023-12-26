package com.exner.tools.fototimer.ui.destinations

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.exner.tools.fototimer.ui.BodyText
import com.exner.tools.fototimer.ui.LockScreenOrientation
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.exner.tools.fototimer.ui.TextAndSwitch
import com.exner.tools.fototimer.ui.TextFieldForTimes
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

sealed class SettingsTabs(val name: String) {
    object UiTab : SettingsTabs("UI")
    object TimersTab : SettingsTabs("Times")
    object SoundsTab : SettingsTabs("Sounds")
}

@Destination
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    Log.i("SettingsScreen", "entering composable...")

    val expertMode by settingsViewModel.expertMode.observeAsState()
    val useDynamicColour by settingsViewModel.useDynamicColour.observeAsState()
    val defaultKeepScreenOn by settingsViewModel.defaultKeepScreenOn.observeAsState()
    val stopIsEverywhere by settingsViewModel.stopIsEverywhere.observeAsState()
    val defaultProcessTime by settingsViewModel.defaultProcessTime.observeAsState()
    val defaultIntervalTime by settingsViewModel.defaultIntervalTime.observeAsState()
    val defaultLeadInTime by settingsViewModel.defaultLeadInTime.observeAsState()
    val defaultPauseTime by settingsViewModel.defaultPauseTime.observeAsState()
    val pauseBeatsLeadIn by settingsViewModel.pauseBeatsLeadIn.observeAsState()
    val numberOfPreBeeps by settingsViewModel.numberOfPreBeeps.observeAsState()

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
                    useDynamicColour,
                    { settingsViewModel.updateUseDynamicColour(it) },
                    defaultKeepScreenOn,
                    { settingsViewModel.updateDefaultKeepScreenOn(it) },
                )
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode == true
                ) {
                    settingsViewModel.updateExpertMode(it)
                }
                AnimatedVisibility(visible = expertMode == true) {
                    ExpertSettingsUI(
                        stopIsEverywhere
                    ) { settingsViewModel.updateStopIsEverywhere(it) }
                }
            }
            1 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = expertMode == true) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsDefaultTimes(
                            defaultProcessTime, { settingsViewModel.updateDefaultProcessTime(it) },
                            defaultIntervalTime, { settingsViewModel.updateDefaultIntervalTime(it) },
                            defaultLeadInTime, { settingsViewModel.updateDefaultLeadInTime(it) },
                            defaultPauseTime, { settingsViewModel.updateDefaultPauseTime(it) },
                            pauseBeatsLeadIn, { settingsViewModel.updatePauseBeatsLeadIn(it) }
                        )
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode == true
                ) {
                    settingsViewModel.updateExpertMode(it)
                }
            }
            2 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedVisibility(visible = expertMode == true) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExpertSettingsSound(
                            numberOfPreBeeps
                        ) { settingsViewModel.updateNumberOfPreBeeps(it) }
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode == true
                ) {
                    settingsViewModel.updateExpertMode(it)
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
    stopIsEverywhere: Boolean?, updateStopIsEverywhere: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAndSwitch(
            text = "Running timer can be stopped by tapping anywhere on the screen",
            checked = stopIsEverywhere == true
        ) {
            updateStopIsEverywhere(it)
        }
    }
}

@Composable
private fun ExpertSettingsSound(
    preBeeps: Int?, setPreBeeps: (Int) -> Unit
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
    processTime: Int?, setProcessTime: (Int) -> Unit,
    intervalTime: Int?, setIntervalTime: (Int) -> Unit,
    leadInTime: Int?, setLeadInTime: (Int) -> Unit,
    pauseTime: Int?, setPauseTime: (Int) -> Unit,
    pauseBeatsLeadIn: Boolean?, setPauseBeatsLeadIn: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextFieldForTimes(
            value = processTime.toString(),
            label = { Text(text = "Default Process time") },
            onValueChange = {
                val newProcessTime = it.toIntOrNull() ?: 0
                setProcessTime(newProcessTime)
            }
        ) { Text(text = "30") }
        TextFieldForTimes(
            value = intervalTime.toString(),
            label = { Text(text = "Default Interval time") },
            onValueChange = {
                val newIntervalTime = it.toIntOrNull() ?: 0
                setIntervalTime(newIntervalTime)
            },
        ) { Text(text = "10") }
        TextFieldForTimes(
            value = leadInTime.toString(),
            label = { Text(text = "Default Lead-in time") },
            onValueChange = {
                val newLeadInTime = it.toIntOrNull() ?: 0
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
        ) { Text(text = "4") }
        TextAndSwitch(
            text = "Pause 'beats' Lead-in",
            checked = pauseBeatsLeadIn == true,
            onCheckedChange = {
                setPauseBeatsLeadIn(it)
            }
        )
    }
}

@Composable
private fun StandardSettingsColumn(
    dynamicColor: Boolean?,
    updateDynamicColor: (Boolean) -> Unit,
    keepScreenOn: Boolean?,
    updateKeepScreenOn: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAndSwitch(
            text = "Dynamic colours (needs a restart)",
            checked = dynamicColor == true,
            onCheckedChange = {
                updateDynamicColor(it)
            }
        )
        TextAndSwitch(
            text = "Default to keep screen on while counting",
            checked = keepScreenOn == true
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
        Settings()
    }
}
