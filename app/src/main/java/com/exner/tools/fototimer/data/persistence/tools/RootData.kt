package com.exner.tools.fototimer.data.persistence.tools

import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RootData(
    val processes: List<FotoTimerProcess>
)
