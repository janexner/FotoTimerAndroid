package com.exner.tools.fototimer.data.settings

data class FotoTimerSettings (
    var expertMode: Boolean = false,

    // UI settings
    var nightMode: Boolean = false,
    var useDynamicColour: Boolean = true,
    var defaultKeepScreenOn: Boolean = true,
    var stopIsEverywhere: Boolean = false,

    // timer settings
    var defaultProcessTime: Long = 30L,
    var defaultIntervalTime: Long = 10L,
    var defaultLeadInTime: Long = 0L,
    var defaultPauseTime: Int = 0,
    var pauseBeatsLeadIn: Boolean = true,

    // sound settings
    var numberOfPreBeeps: Int = 4,
)
