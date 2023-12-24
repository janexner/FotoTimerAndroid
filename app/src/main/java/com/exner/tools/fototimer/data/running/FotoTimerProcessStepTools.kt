package com.exner.tools.fototimer.data.running

import com.exner.tools.fototimer.data.persistence.FotoTimerProcess
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository

suspend fun getAsFTProcessStepList(processDatabase: FotoTimerProcessRepository, uid: Long): List<List<FotoTimerProcessStepAction>> {
    val result = mutableListOf<List<FotoTimerProcessStepAction>>()

    // loop detection
    val processIdList = mutableListOf<Long>()

    var currentID = uid
    var firstRound = true
    var noLoopDetectedSoFar = true
    while (currentID >= 0 && noLoopDetectedSoFar) {
        processIdList.add(currentID)
        val process = processDatabase.loadProcessById(currentID)
        if (process !== null) {
            val partialResult = getOneFTProcessStepList(process, firstRound)
            partialResult.forEach { actionList ->
                result.add(actionList)
            }
            // prepare for the next iteration
            firstRound = false
            currentID = if (process.gotoId !== null) process.gotoId else -1
            if (processIdList.contains(currentID)) {
                noLoopDetectedSoFar = false // LOOP!
                var earliestStepNumberForLoop = -1
                var i = 0
                while (i < result.size && earliestStepNumberForLoop < 0) {
                    val checkPoint = result.get(i)
                    checkPoint.forEach { action ->
                        if (action is FotoTimerProcessStartAction) {
                            if (action.processID == currentID) {
                                earliestStepNumberForLoop = i
                            }
                        }
                    }
                    i++
                }
                if (earliestStepNumberForLoop >= 0) {
                    // this has to replace the latest GotoAction
                    val latestActionList = result[result.lastIndex]
                    val lastAction = latestActionList[latestActionList.lastIndex]
                    if (lastAction is FotoTimerProcessGotoAction) { // it should be!
                        // remove the action list, it is not mutable
                        result.removeLast() // remove the action list, bcs we need a new one
                        val newActionsList = mutableListOf<FotoTimerProcessStepAction>()
                        latestActionList.forEach { processStepAction ->
                            if (processStepAction !is FotoTimerProcessGotoAction) {
                                newActionsList.add(processStepAction)
                            }
                        }
                        val ftpjumpAction = FotoTimerProcessJumpbackAction(
                            process.name,
                            earliestStepNumberForLoop
                        )
                        newActionsList.add(ftpjumpAction)
                        result.add(newActionsList)
                    }
                }
            }
        }
    }

    return result
}

fun getOneFTProcessStepList(process: FotoTimerProcess, considerLeadIn: Boolean = false): MutableList<List<FotoTimerProcessStepAction>> {
    val result = mutableListOf<List<FotoTimerProcessStepAction>>()

    // step length - only there so we can tweak it, if necessary
    val stepLengthInMilliseconds = 500

    // do we need steps for lead-in, and how many?
    if (considerLeadIn && process.hasLeadIn && process.leadInSeconds!! > 0) {
        val howManySteps = process.leadInSeconds * 1000 / stepLengthInMilliseconds
        for (i in 1..howManySteps) {
            val actionsList = mutableListOf<FotoTimerProcessStepAction>()
            // add actions as needed
            val ftpliAction = FotoTimerProcessLeadInDisplayStepAction(
                process.name,
                i * stepLengthInMilliseconds / 1000
            )
            actionsList.add(ftpliAction)
            if (process.hasPreBeeps && isFullSecond(i, stepLengthInMilliseconds)) {
                // add action for beep during lead in
                val ftpppbAction = FotoTimerProcessSoundAction(
                    process.name,
                    1L // TODO add proper sound ID - pre-beeps / lead-in
                )
                actionsList.add(ftpppbAction)
            }
            // add the chain of actions to the overall list
            result.add(actionsList)
        }
    }

    // how many steps do we need for the regular interval?
    val howManySteps = process.processTime * 1000 / stepLengthInMilliseconds
    for (i in 1..howManySteps) {
        val actionsList = mutableListOf<FotoTimerProcessStepAction>()
        // add actions as needed
        if (i == 1) {
            // first things first
            val ftpstartAction = FotoTimerProcessStartAction(
                process.name,
                process.uid
            )
            actionsList.add(ftpstartAction)
        }
        // calculate round and times and create the display action
        val currentProcessTime = i * stepLengthInMilliseconds / 1000L
        val currentIntervalTime = currentProcessTime % process.intervalTime
        val ftpdAction = FotoTimerProcessDisplayStepAction(
            process.name,
            1L + currentProcessTime / process.intervalTime,
            currentProcessTime,
            currentIntervalTime
        )
        actionsList.add(ftpdAction)
        // any sounds?
        if (i == 1 && process.hasSoundStart) {
            val ftpssAction = process.soundStartId?.let {
                FotoTimerProcessSoundAction(
                    process.name,
                    it
                )
            }
            if (ftpssAction != null) {
                actionsList.add(ftpssAction)
            }
        } else if (i == howManySteps && process.hasSoundEnd) {
            val ftpseAction = process.soundEndId?.let {
                FotoTimerProcessSoundAction(
                    process.name,
                    it
                )
            }
            if (ftpseAction != null) {
                actionsList.add(ftpseAction)
            }
        } else if (isFullSecond(i, stepLengthInMilliseconds)) {
            if ((i * stepLengthInMilliseconds / 1000.0 % process.intervalTime == 0.0) && process.hasSoundInterval) {
                val ftpsiAction = process.soundIntervalId?.let {
                    FotoTimerProcessSoundAction(
                        process.name,
                        it
                    )
                }
                if (ftpsiAction != null) {
                    actionsList.add(ftpsiAction)
                }
            } else if (process.hasSoundMetronome) {
                val ftpsmAction = FotoTimerProcessSoundAction(
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
    if (process.hasAutoChain && process.gotoId!! >= 0) {
        if (process.hasPauseBeforeChain == true && process.pauseTime!! >= 0) {
            val howManySteps = process.pauseTime * 1000 / stepLengthInMilliseconds
            for (i in 1..howManySteps) {
                val actionsList = mutableListOf<FotoTimerProcessStepAction>()
                val ftppdAction =
                    FotoTimerProcessPauseDisplayStepAction(process.name, i * stepLengthInMilliseconds / 1000)
                actionsList.add(ftppdAction)
                if (i == howManySteps) {
                    val ftpchainAction = FotoTimerProcessGotoAction(
                        process.name,
                        process.gotoId
                    )
                    actionsList.add(ftpchainAction)
                }
                result.add(actionsList)
            }
        } else {
            val actionsList = mutableListOf<FotoTimerProcessStepAction>()
            val ftpchainAction = FotoTimerProcessGotoAction(
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