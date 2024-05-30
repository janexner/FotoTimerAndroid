package com.exner.tools.fototimer.ui.destinations

import android.content.pm.ActivityInfo
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exner.tools.fototimer.ui.BodyText
import com.exner.tools.fototimer.ui.LockScreenOrientation
import com.exner.tools.fototimer.ui.SettingsViewModel
import com.exner.tools.fototimer.ui.TextAndSwitch
import com.exner.tools.fototimer.ui.TextAndTriStateToggle
import com.exner.tools.fototimer.ui.TextFieldForTimes
import com.exner.tools.fototimer.ui.theme.Theme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

sealed class SettingsTabs(val name: String) {
    data object UiTab : SettingsTabs("UI")
    data object TimersTab : SettingsTabs("Times")
    data object SoundsTab : SettingsTabs("Sounds")
}

@Destination<RootGraph>
@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    windowSizeClass: WindowSizeClass
) {

    val expertMode by settingsViewModel.expertMode.collectAsStateWithLifecycle()
    val userSelectedTheme by settingsViewModel.userSelectedTheme.collectAsStateWithLifecycle()
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

    // check width here, and make layout dependent
    var layoutAsTabs = true
    if (windowSizeClass.widthSizeClass >= WindowWidthSizeClass.Expanded) {
        layoutAsTabs = false
    }
    // show settings
    if (layoutAsTabs) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = tabIndex
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
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextAndTriStateToggle(
                            text = "Theme",
                            currentTheme = userSelectedTheme,
                            updateTheme = { it: Theme ->
                                settingsViewModel.updateUserSelectedTheme(
                                    it
                                )
                            }
                        )
                        TextAndSwitch(
                            text = "Use current interval time as central display, rather than current process time",
                            checked = intervalTimeIsCentral
                        ) {
                            settingsViewModel.updateIntervalTimeIsCentral(it)
                        }
                    }
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
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item (
                span = { GridItemSpan(maxLineSpan) }
            ) {
                Text(
                    text = "UI",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            item {
                TextAndTriStateToggle(
                    text = "Theme",
                    currentTheme = userSelectedTheme,
                    updateTheme = { settingsViewModel.updateUserSelectedTheme(it) }
                )
            }
            item {
                TextAndSwitch(
                    text = "Use current interval time as central display, rather than current process time",
                    checked = intervalTimeIsCentral
                ) {
                    settingsViewModel.updateIntervalTimeIsCentral(it)
                }
            }

            item (
                span = { GridItemSpan(maxLineSpan) }
            ) {
                AnimatedVisibility(visible = expertMode) {
                    Text(
                        text = "Times",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                AnimatedVisibility(visible = expertMode) {
                    TextFieldForTimes(
                        value = defaultProcessTime,
                        label = { Text(text = "Default Process time") },
                        onValueChange = {
                            settingsViewModel.updateDefaultProcessTime(it)
                        }
                    ) { Text(text = "30") }
                }
            }

            item {
                AnimatedVisibility(visible = expertMode) {
                    TextFieldForTimes(
                        value = defaultIntervalTime,
                        label = { Text(text = "Default Interval time") },
                        onValueChange = {
                            settingsViewModel.updateDefaultIntervalTime(it)
                        },
                    ) { Text(text = "10") }
                }
            }

            item {
                AnimatedVisibility(visible = expertMode) {
                    TextFieldForTimes(
                        value = defaultLeadInTime,
                        label = { Text(text = "Default Lead-in time") },
                        onValueChange = {
                            settingsViewModel.updateDefaultLeadInTime(it)
                        },
                    ) { Text(text = "0") }
                }
            }

            item {
                AnimatedVisibility(visible = expertMode) {
                    TextFieldForTimes(
                        value = defaultPauseTime,
                        label = { Text(text = "Default Pause time") },
                        onValueChange = {
                            settingsViewModel.updateDefaultPauseTime(it)
                        },
                    ) { Text(text = "5") }
                }
            }

            item (
                span = { GridItemSpan(maxLineSpan) }
            ) {
                AnimatedVisibility(visible = expertMode) {
                    Text(
                        text = "Sounds",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                AnimatedVisibility(visible = expertMode) {
                    OutlinedTextField(
                        value = numberOfPreBeeps.toString(),
                        label = { Text(text = "Pre-beeps (small beeps before the interval)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = {
                            val newPreBeeps = it.toIntOrNull() ?: 0
                            settingsViewModel.updateNumberOfPreBeeps(newPreBeeps)
                        },
                        placeholder = { Text(text = "4") },
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            item {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AnimatedVisibility(visible = expertMode) {
                        TextAndSwitch(
                            text = "Vibrate when playing sounds",
                            checked = vibrateEnabled
                        ) {
                            settingsViewModel.updateVibrateEnabled(it)
                        }
                    }
                }
            }

            item (
                span = { GridItemSpan(maxLineSpan) }
            ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
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

