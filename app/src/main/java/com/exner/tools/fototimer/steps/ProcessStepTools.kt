package com.exner.tools.fototimer.steps

import com.exner.tools.fototimer.data.persistence.FotoTimerProcess

const val STEP_LENGTH_IN_MILLISECONDS = 500

fun getProcessStepListForOneProcess(
    process: FotoTimerProcess,
    considerLeadIn: Boolean = false,
    numberOfPreBeeps: Int = 4
): MutableList<List<ProcessStepAction>> {
    val result = mutableListOf<List<ProcessStepAction>>()

    // step length - only there so we can tweak it, if necessary
    val stepLengthInMilliseconds = STEP_LENGTH_IN_MILLISECONDS

    // do we need steps for lead-in, and how many?
    if (considerLeadIn && process.hasLeadIn) {
        if (process.leadInSeconds != null && process.leadInSeconds > 0) {
            val howManySteps = process.leadInSeconds * 1000 / stepLengthInMilliseconds
            for (i in 1..howManySteps) {
                val actionsList = mutableListOf<ProcessStepAction>()
                // add actions as needed
                val ftpliAction = ProcessLeadInDisplayStepAction(
                    process.name,
                    i * stepLengthInMilliseconds / 1000
                )
                actionsList.add(ftpliAction)
                // add the chain of actions to the overall list
                result.add(actionsList)
            }
        }
    }

    // how many steps do we need for the regular interval?
    val howManySteps = process.processTime * 1000 / stepLengthInMilliseconds
    for (i in 1..howManySteps) {
        val actionsList = mutableListOf<ProcessStepAction>()
        // add actions as needed
        if (i == 1) {
            // first things first
            val ftpstartAction = ProcessStartAction(
                process.name,
                process.uid
            )
            actionsList.add(ftpstartAction)
        }
        // calculate round and times and create the display action
        val currentProcessTime = i * stepLengthInMilliseconds / 1000L
        val currentIntervalTime = currentProcessTime % process.intervalTime
        val ftpdAction = ProcessDisplayStepAction(
            process.name,
            1L + currentProcessTime / process.intervalTime,
            currentProcessTime,
            currentIntervalTime
        )
        actionsList.add(ftpdAction)
        // any sounds?
        if (i == 1 && process.hasSoundStart) {
            val ftpssAction = ProcessSoundAction(
                process.name,
                process.soundStartId ?: 0L
            )
            actionsList.add(ftpssAction)
        } else if (i == howManySteps && process.hasSoundEnd) {
            val ftpseAction = ProcessSoundAction(
                process.name,
                process.soundEndId ?: 0L
            )
            actionsList.add(ftpseAction)
        } else if (isFullSecond(i, stepLengthInMilliseconds)) {
            if ((i * stepLengthInMilliseconds / 1000.0 % process.intervalTime == 0.0) && process.hasSoundInterval) {
                val ftpsiAction = ProcessSoundAction(
                    process.name,
                    process.soundIntervalId ?: 0L
                )
                actionsList.add(ftpsiAction)
            } else if (process.hasPreBeeps) {
                val relativePosition = (i * stepLengthInMilliseconds / 1000) % process.intervalTime
                if (relativePosition >= (process.intervalTime - numberOfPreBeeps)) {
                    val ftpspbAction = ProcessSoundAction(
                        process.name,
                        6L
                    )
                    actionsList.add(ftpspbAction)
                }
            } else if (process.hasSoundMetronome) {
                val ftpsmAction = ProcessSoundAction(
                    process.name,
                    5L // TODO add proper sound ID - metronome
                )
                actionsList.add(ftpsmAction)
            }
        }
        // add the chain of actions to the overall list
        result.add(actionsList)
    }

    // does this process chain?
    if (process.hasAutoChain && process.gotoId != null && process.gotoId >= 0) {
        if (process.hasPauseBeforeChain == true && process.pauseTime != null && process.pauseTime > 0) {
            // TODO not treating the pauseTime == 0 case
            val howManyPauseSteps = process.pauseTime * 1000 / stepLengthInMilliseconds
            for (i in 1..howManyPauseSteps) {
                val actionsList = mutableListOf<ProcessStepAction>()
                val ftppdAction =
                    ProcessPauseDisplayStepAction(process.name, i * stepLengthInMilliseconds / 1000)
                actionsList.add(ftppdAction)
                if (i == howManyPauseSteps) {
                    val ftpchainAction = ProcessGotoAction(
                        process.name,
                        process.gotoId
                    )
                    actionsList.add(ftpchainAction)
                }
                result.add(actionsList)
            }
        } else {
            val actionsList = mutableListOf<ProcessStepAction>()
            val ftpchainAction = ProcessGotoAction(
                process.name,
                process.gotoId
            )
            actionsList.add(ftpchainAction)
            result.add(actionsList)
        }
    }

    return result
}

private fun isFullSecond(stepIndex: Int, stepLengthInMilliseconds: Int): Boolean {
    return (stepIndex * stepLengthInMilliseconds) % 1000.0 == 0.0
}