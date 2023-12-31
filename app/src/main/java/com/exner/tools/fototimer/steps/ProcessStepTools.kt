package com.exner.tools.fototimer.steps

import com.exner.tools.fototimer.audio.SoundIDs
import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import kotlin.math.ceil

const val STEP_LENGTH_IN_MILLISECONDS = 1000

fun getProcessStepListForOneProcess(
    process: FotoTimerProcess,
    considerLeadIn: Boolean = false,
    numberOfPreBeeps: Int = 4
): MutableList<List<ProcessStepAction>> {
    val result = mutableListOf<List<ProcessStepAction>>()

    var processParameters = ""
    if (considerLeadIn && process.hasLeadIn && process.leadInSeconds != null && process.leadInSeconds > 0) {
        processParameters += "${process.leadInSeconds} > "
    }
    processParameters += "${process.processTime} / ${process.intervalTime}"
    if (process.hasAutoChain && process.gotoId != null && process.gotoId >= 0 && process.hasPauseBeforeChain == true && process.pauseTime != null && process.pauseTime > 0) {
        processParameters += " > ${process.pauseTime}"
    }

    // do we need steps for lead-in, and how many?
    if (considerLeadIn && process.hasLeadIn) {
        if (process.leadInSeconds != null && process.leadInSeconds > 0) {
            val howManySteps = process.leadInSeconds * 1000 / STEP_LENGTH_IN_MILLISECONDS
            for (i in 1..howManySteps) {
                val actionsList = mutableListOf<ProcessStepAction>()
                // add actions as needed
                val ftpliAction = ProcessLeadInDisplayStepAction(
                    process.name,
                    processParameters,
                    i * STEP_LENGTH_IN_MILLISECONDS / 1000
                )
                actionsList.add(ftpliAction)
                if (isFullSecond(i) && process.hasLeadInSound) {
                    val ftplisAction = ProcessSoundAction(
                        process.name,
                        SoundIDs.SOUND_ID_LEAD_IN
                    )
                    actionsList.add(ftplisAction)
                }
                // add the chain of actions to the overall list
                result.add(actionsList)
            }
        }
    }

    // how many steps do we need for the regular interval?
    val howManySteps = process.processTime * 1000 / STEP_LENGTH_IN_MILLISECONDS
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
        val currentProcessTime = i * STEP_LENGTH_IN_MILLISECONDS / 1000
        val currentIntervalTime = currentProcessTime % process.intervalTime
        val ftpdAction = ProcessDisplayStepAction(
            process.name,
            processParameters,
            1 + currentProcessTime / process.intervalTime,
            ceil(process.processTime.toDouble() / process.intervalTime).toInt(),
            currentProcessTime,
            currentIntervalTime
        )
        actionsList.add(ftpdAction)
        // any sounds?
        if (i == 1 && process.hasSoundStart) {
            val ftpssAction = ProcessSoundAction(
                process.name,
                SoundIDs.SOUND_ID_PROCESS_START
            )
            actionsList.add(ftpssAction)
        } else if (i == howManySteps && process.hasSoundEnd) {
            val ftpseAction = ProcessSoundAction(
                process.name,
                SoundIDs.SOUND_ID_PROCESS_END
            )
            actionsList.add(ftpseAction)
        } else if (isFullSecond(i)) {
            if ((i * STEP_LENGTH_IN_MILLISECONDS / 1000.0 % process.intervalTime == 0.0) && process.hasSoundInterval) {
                val ftpsiAction = ProcessSoundAction(
                    process.name,
                    SoundIDs.SOUND_ID_INTERVAL
                )
                actionsList.add(ftpsiAction)
            } else {
                val relativePosition =
                    (i * STEP_LENGTH_IN_MILLISECONDS / 1000) % process.intervalTime
                if (process.hasPreBeeps && relativePosition >= (process.intervalTime - numberOfPreBeeps)) {
                        val ftpspbAction = ProcessSoundAction(
                            process.name,
                            SoundIDs.SOUND_ID_PRE_BEEPS
                        )
                        actionsList.add(ftpspbAction)
                } else if (process.hasSoundMetronome) {
                    val ftpsmAction = ProcessSoundAction(
                        process.name,
                        SoundIDs.SOUND_ID_METRONOME
                    )
                    actionsList.add(ftpsmAction)
                }
            }
        }
        // add the chain of actions to the overall list
        result.add(actionsList)
    }

    // does this process chain?
    if (process.hasAutoChain && process.gotoId != null && process.gotoId >= 0) {
        if (process.hasPauseBeforeChain == true && process.pauseTime != null && process.pauseTime > 0) {
            // TODO not treating the pauseTime == 0 case
            val howManyPauseSteps = process.pauseTime * 1000 / STEP_LENGTH_IN_MILLISECONDS
            for (i in 1..howManyPauseSteps) {
                val actionsList = mutableListOf<ProcessStepAction>()
                val ftppdAction =
                    ProcessPauseDisplayStepAction(
                        process.name,
                        processParameters,
                        i * STEP_LENGTH_IN_MILLISECONDS / 1000
                    )
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

private fun isFullSecond(stepIndex: Int): Boolean {
    return (stepIndex * STEP_LENGTH_IN_MILLISECONDS) % 1000.0 == 0.0
}