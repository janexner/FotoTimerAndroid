package com.exner.tools.fototimerresearch2.data.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
@kotlinx.serialization.Serializable
data class FotoTimerProcess (
    @ColumnInfo(name = "name") val name : String,
    @ColumnInfo(name = "process_time") val processTime : Long = 30,
    @ColumnInfo(name = "interval_time") val intervalTime: Long = 10,

    @ColumnInfo(name = "has_sound_start") val hasSoundStart: Boolean = false,
    @ColumnInfo(name = "sound_start_id") val soundStartId: Long?,

    @ColumnInfo(name = "has_sound_end") val hasSoundEnd: Boolean = false,
    @ColumnInfo(name = "sound_end_id") val soundEndId: Long?,

    @ColumnInfo(name = "has_sound_interval") val hasSoundInterval: Boolean = false,
    @ColumnInfo(name = "sound_interval_id") val soundIntervalId: Long?,

    @ColumnInfo(name = "has_sound_metronome") val hasSoundMetronome: Boolean = false,

    @ColumnInfo(name = "has_lead_in") val hasLeadIn: Boolean = false,
    @ColumnInfo(name = "lead_in_seconds") val leadInSeconds: Int?,

    @ColumnInfo(name = "has_auto_chain") val hasAutoChain: Boolean = false,
    @ColumnInfo(name = "has_pause_before_chain") val hasPauseBeforeChain: Boolean?,
    @ColumnInfo(name = "pause_time") val pauseTime: Int?,
    @ColumnInfo(name = "goto_id") val gotoId: Long?,

    @PrimaryKey(autoGenerate = true) val uid: Long = 0
)
