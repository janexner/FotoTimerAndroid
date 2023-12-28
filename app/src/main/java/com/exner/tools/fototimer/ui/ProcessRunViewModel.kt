package com.exner.tools.fototimer.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.audio.SoundPoolHolder
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimer.steps.ProcessDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessGotoAction
import com.exner.tools.fototimer.steps.ProcessJumpbackAction
import com.exner.tools.fototimer.steps.ProcessLeadInDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessPauseDisplayStepAction
import com.exner.tools.fototimer.steps.ProcessSoundAction
import com.exner.tools.fototimer.steps.ProcessStartAction
import com.exner.tools.fototimer.steps.ProcessStepAction
import com.exner.tools.fototimer.steps.STEP_LENGTH_IN_MILLISECONDS
import com.exner.tools.fototimer.steps.getProcessStepListForOneProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessRunViewModel @Inject constructor(
    private val repository: FotoTimerProcessRepository
) : ViewModel() {

    private val _displayAction: MutableLiveData<ProcessStepAction> = MutableLiveData(null)
    val displayAction: LiveData<ProcessStepAction> = _displayAction

    private val _numberOfSteps: MutableLiveData<Int> = MutableLiveData(0)
    val numberOfSteps: LiveData<Int> = _numberOfSteps

    private val _currentStepNumber: MutableLiveData<Int> = MutableLiveData(0)
    val currentStepNumber: LiveData<Int> = _currentStepNumber

    private val _keepScreenOn: MutableLiveData<Boolean> = MutableLiveData(false)
    val keepScreenOn: LiveData<Boolean> = _keepScreenOn

    private val _hasLoop: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasLoop: LiveData<Boolean> = _hasLoop

    private val _hasHours: MutableLiveData<Boolean> = MutableLiveData(false)
    val hasHours: LiveData<Boolean> = _hasHours

    private var job: Job? = null

    private var isRunning: Boolean = false

    @OptIn(DelicateCoroutinesApi::class)
    fun initialiseRun(processId: Long, numberOfPreBeeps: Int) {
        val result = mutableListOf<List<ProcessStepAction>>()

        Log.d("ProcessRunVM", "init...")

        if (!isRunning) {
            isRunning = true

            // create list of list of actions and run it
            viewModelScope.launch {
                // loop detection
                val processIdList = mutableListOf<Long>()
                var currentID = processId
                var noLoopDetectedSoFar = true
                var firstRound = true
                var keepScreenOnAcrossList = false

                while (currentID >= 0 && noLoopDetectedSoFar) {
                    processIdList.add(currentID)
                    val process = repository.loadProcessById(currentID)
                    if (process != null) {
                        val partialResult =
                            getProcessStepListForOneProcess(process, firstRound, numberOfPreBeeps)
                        partialResult.forEach { actionList ->
                            result.add(actionList)
                        }
                        // screen on?
                        keepScreenOnAcrossList = keepScreenOnAcrossList || process.keepsScreenOn
                        // do we need hours in the display?
                        _hasHours.value = hasHours.value == true || process.processTime > 3600
                        // prepare for the next iteration
                        firstRound = false
                        if (process.gotoId != null && process.gotoId != -1L) {
                            currentID = process.gotoId
                            if (processIdList.contains(currentID)) {
                                noLoopDetectedSoFar = false // LOOP!
                                _hasLoop.value = true
                                var earliestStepNumberForLoop = -1
                                var i = 0
                                while (i < result.size && earliestStepNumberForLoop < 0) {
                                    val checkPoint = result[i]
                                    checkPoint.forEach { action ->
                                        if (action is ProcessStartAction) {
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
                                    if (lastAction is ProcessGotoAction) { // it should be!
                                        // remove the action list, it is not mutable
                                        result.removeLast() // remove the action list, bcs we need a new one
                                        val newActionsList = mutableListOf<ProcessStepAction>()
                                        latestActionList.forEach { processStepAction ->
                                            if (processStepAction !is ProcessGotoAction) {
                                                newActionsList.add(processStepAction)
                                            }
                                        }
                                        val ftpJumpAction = ProcessJumpbackAction(
                                            process.name,
                                            earliestStepNumberForLoop
                                        )
                                        newActionsList.add(ftpJumpAction)
                                        result.add(newActionsList)
                                    }
                                }
                            }
                        } else {
                            // that's it, no chain
                            currentID = -1
                        }
                    }
                }
                // this is where the list is ready
                _numberOfSteps.value = result.size

                _keepScreenOn.value = keepScreenOnAcrossList

                // go into a loop, but in a coroutine
                job = GlobalScope.launch(Dispatchers.Main) {
                    val startTime = System.currentTimeMillis()
                    var actualStep = 0
                    while (isActive) {
                        val step: Int = currentStepNumber.value?.toInt() ?: 0
                        if (step >= result.size) {
                            Log.d("ProcessRunVM", "Step $step, job done, quitting...")
                            break
                        } else {
                            val elapsed = System.currentTimeMillis() - startTime
                            Log.d(
                                "ProcessRunVM",
                                "$elapsed: Now doing step $step/$actualStep of ${numberOfSteps.value}..."
                            )
                            // update display action and do sounds
                            val actionsList = result[step]
                            actionsList.forEach { action ->
                                when (action) {
                                    is ProcessLeadInDisplayStepAction, is ProcessDisplayStepAction, is ProcessPauseDisplayStepAction -> {
                                        _displayAction.value = action
                                    }

                                    is ProcessJumpbackAction -> {
                                        Log.d("ProcessRunVM", "Jump back to ${action.stepNumber}...")
                                        _currentStepNumber.value = action.stepNumber - 1 // aim left
                                        // bcs 4 lines down, we count up by one
                                    }

                                    is ProcessSoundAction -> {
                                        SoundPoolHolder.playSound(action.soundId)
                                        // TODO debug this
                                    }
                                }
                            }
                            // count up
                            _currentStepNumber.value = currentStepNumber.value!! + 1
                            // sleep till next step
                            actualStep++
                            val targetTimeForNextStep =
                                startTime + (actualStep * STEP_LENGTH_IN_MILLISECONDS)
                            val newDelay = targetTimeForNextStep - System.currentTimeMillis()
                            delay(newDelay)
                        }
                    }
                }
            }
        }
    }

    fun cancel() {
        job?.cancel()
    }
}