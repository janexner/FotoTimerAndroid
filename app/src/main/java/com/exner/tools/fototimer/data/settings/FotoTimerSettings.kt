package com.exner.tools.fototimer.data.settings

data class FotoTimerSettings (
    var expertMode: Boolean = false,

    // UI settings
    var nightMode: Boolean = false,
    var useDynamicColour: Boolean = true,
    var defaultKeepScreenOn: Boolean = true,
    var stopIsEverywhere: Boolean = false,

    // timer settings
    var defaultProcessTime: Int = 30,
    var defaultIntervalTime: Int = 10,
    var defaultLeadInTime: Int = 0,
    var defaultPauseTime: Int = 0,
    var pauseBeatsLeadIn: Boolean = true,

    // sound settings
    var numberOfPreBeeps: Int = 4,
)
