package com.exner.tools.fototimer.data.running

open class FotoTimerProcessStepAction(
    val processName: String
)

class FotoTimerProcessStartAction(
    processName: String,
    val processID: Long
): FotoTimerProcessStepAction(processName)

class FotoTimerProcessDisplayStepAction(
    processName: String,
    val currentRound: Long,
    val currentProcessTime: Long,
    val currentIntervalTime: Long
) : FotoTimerProcessStepAction(processName)

class FotoTimerProcessLeadInDisplayStepAction(
    processName: String,
    val currentLeadInTime: Int
) : FotoTimerProcessStepAction(processName)

class FotoTimerProcessSoundAction(
    processName: String,
    val soundId: Long
) : FotoTimerProcessStepAction(processName)

class FotoTimerProcessPauseDisplayStepAction(
    processName: String,
    val currentPauseTime: Int
) : FotoTimerProcessStepAction(processName)

class FotoTimerProcessGotoAction(
    processName: String,
    val gotoId: Long
): FotoTimerProcessStepAction(processName)

class FotoTimerProcessJumpbackAction(
    processName: String,
    val stepNumber: Int
): FotoTimerProcessStepAction(processName)