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
    val currentRound: Long,
    val currentProcessTime: Long,
    val currentIntervalTime: Long
) : ProcessStepAction(processName)

class ProcessLeadInDisplayStepAction(
    processName: String,
    val currentLeadInTime: Int
) : ProcessStepAction(processName)

class ProcessSoundAction(
    processName: String,
    val soundId: Long
) : ProcessStepAction(processName)

class ProcessPauseDisplayStepAction(
    processName: String,
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