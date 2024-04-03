package com.exner.tools.fototimer.ui.destinations

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.ui.BodyText
import com.exner.tools.fototimer.ui.LockScreenOrientation
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.exner.tools.fototimer.ui.TextAndSwitch
import com.exner.tools.fototimer.ui.TextFieldForTimes
import com.exner.tools.fototimer.ui.theme.FotoTimerTheme
import com.ramcosta.composedestinations.annotation.Destination

sealed class SettingsTabs(val name: String) {
    data object UiTab : SettingsTabs("UI")
    data object TimersTab : SettingsTabs("Times")
    data object SoundsTab : SettingsTabs("Sounds")
}

@Destination
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {

    val expertMode by settingsViewModel.expertMode.collectAsStateWithLifecycle()
    val nightMode by settingsViewModel.nightMode.collectAsStateWithLifecycle()
    val defaultProcessTime by settingsViewModel.defaultProcessTime.collectAsStateWithLifecycle()
    val defaultIntervalTime by settingsViewModel.defaultIntervalTime.collectAsStateWithLifecycle()
    val defaultLeadInTime by settingsViewModel.defaultLeadInTime.collectAsStateWithLifecycle()
    val defaultPauseTime by settingsViewModel.defaultPauseTime.collectAsStateWithLifecycle()
    val numberOfPreBeeps by settingsViewModel.numberOfPreBeeps.collectAsStateWithLifecycle()
    val intervalTimeIsCentral by settingsViewModel.interValTimeIsCentral.collectAsStateWithLifecycle()
    val vibrateEnabled by settingsViewModel.vibrateEnabled.collectAsStateWithLifecycle()

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
                    nightMode,
                    { settingsViewModel.updateNightMode(it) },
                    intervalTimeIsCentral,
                    { settingsViewModel.updateIntervalTimeIsCentral(it) }
                )
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    settingsViewModel.updateExpertMode(it)
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
                        ExpertSettingsDefaultTimes(
                            defaultProcessTime,
                            { settingsViewModel.updateDefaultProcessTime(it) },
                            defaultIntervalTime,
                            { settingsViewModel.updateDefaultIntervalTime(it) },
                            defaultLeadInTime,
                            { settingsViewModel.updateDefaultLeadInTime(it) },
                            defaultPauseTime,
                            { settingsViewModel.updateDefaultPauseTime(it) },
                        )
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    settingsViewModel.updateExpertMode(it)
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
                        ExpertSettingsSound(
                            numberOfPreBeeps,
                            { settingsViewModel.updateNumberOfPreBeeps(it) },
                            vibrateEnabled,
                            { settingsViewModel.updateVibrateEnabled(it) }
                        )
                    }
                }
                TextAndSwitch(
                    text = "Expert mode (more options everywhere)",
                    checked = expertMode
                ) {
                    settingsViewModel.updateExpertMode(it)
                }
            }
        }
    }
// invisible preferences TODO
// default sounds
// - process start
// - process end
// - interval
}

@Composable
private fun ExpertSettingsSound(
    preBeeps: Int, setPreBeeps: (Int) -> Unit,
    vibrate: Boolean, setVibrate: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().imePadding()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            TextAndSwitch(
                text = "Vibrate when playing sounds",
                checked = vibrate
            ) {
                setVibrate(it)
            }
        }
    }
}

@Composable
private fun ExpertSettingsDefaultTimes(
    processTime: Int, setProcessTime: (Int) -> Unit,
    intervalTime: Int, setIntervalTime: (Int) -> Unit,
    leadInTime: Int, setLeadInTime: (Int) -> Unit,
    pauseTime: Int, setPauseTime: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextFieldForTimes(
            value = processTime,
            label = { Text(text = "Default Process time") },
            onValueChange = {
                setProcessTime(it)
            }
        ) { Text(text = "30") }
        TextFieldForTimes(
            value = intervalTime,
            label = { Text(text = "Default Interval time") },
            onValueChange = {
                setIntervalTime(it)
            },
        ) { Text(text = "10") }
        TextFieldForTimes(
            value = leadInTime,
            label = { Text(text = "Default Lead-in time") },
            onValueChange = {
                setLeadInTime(it)
            },
        ) { Text(text = "0") }
        TextFieldForTimes(
            value = pauseTime,
            label = { Text(text = "Default Pause time") },
            onValueChange = {
                setPauseTime(it)
            },
        ) { Text(text = "5") }
    }
}

@Composable
private fun StandardSettingsColumn(
    nightMode: Boolean,
    updateNightMode: (Boolean) -> Unit,
    intervalTimeIsCentral: Boolean,
    updateIntervalTimeIsCentral: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextAndSwitch(text = "Force night mode", checked = nightMode) {
            updateNightMode(it)
        }
        TextAndSwitch(
            text = "Use current interval time as central display, rather than current process time",
            checked = intervalTimeIsCentral
        ) {
            updateIntervalTimeIsCentral(it)
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
