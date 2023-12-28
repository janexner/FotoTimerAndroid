package com.exner.tools.fototimer.steps

open class ProcessStepAction(
    val processName: String
)

class ProcessStartAction(
    processName: String,
    val processID: Long
): ProcessStepAction(processName)

class ProcessDisplayStepAction(
    processName: String,
    val processParameters: String,
    val currentRound: Int,
    val totalRounds: Int,
    val currentProcessTime: Int,
    val currentIntervalTime: Int
) : ProcessStepAction(processName)

class ProcessLeadInDisplayStepAction(
    processName: String,
    val processParameters: String,
    val currentLeadInTime: Int
) : ProcessStepAction(processName)

class ProcessSoundAction(
    processName: String,
    val soundId: Long
) : ProcessStepAction(processName)

class ProcessPauseDisplayStepAction(
    processName: String,
    val processParameters: String,
    val currentPauseTime: Int
) : ProcessStepAction(processName)

class ProcessGotoAction(
    processName: String,
    val gotoId: Long
): ProcessStepAction(processName)

class ProcessJumpbackAction(
    processName: String,
    val stepNumber: Int
): ProcessStepAction(processName)