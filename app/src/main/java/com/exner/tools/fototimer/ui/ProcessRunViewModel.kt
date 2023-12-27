package com.exner.tools.fototimer.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exner.tools.fototimer.data.persistence.FotoTimerProcessRepository
import com.exner.tools.fototimer.steps.ProcessGotoAction
import com.exner.tools.fototimer.steps.ProcessJumpbackAction
import com.exner.tools.fototimer.steps.ProcessStartAction
import com.exner.tools.fototimer.steps.ProcessStepAction
import com.exner.tools.fototimer.steps.getProcessStepListForOneProcess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProcessRunViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: FotoTimerProcessRepository
): ViewModel() {

    private val _actionsListList: MutableLiveData<MutableList<List<ProcessStepAction>>> = MutableLiveData(mutableListOf())
    val actionsListList: LiveData<MutableList<List<ProcessStepAction>>> = _actionsListList

    private val _displayAction: MutableLiveData<ProcessStepAction> = MutableLiveData(null)
    val displayAction: LiveData<ProcessStepAction> = _displayAction

    private val _numberOfSteps: MutableLiveData<Int> = MutableLiveData(0)
    val numberOfSteps: LiveData<Int> = _numberOfSteps

    private val _currentStepNumber: MutableLiveData<Int> = MutableLiveData(0)
    val currentStepNumber: LiveData<Int> = _currentStepNumber

    fun initialiseRun(processId: Long, numberOfPreBeeps: Int) {
        val result = mutableListOf<List<ProcessStepAction>>()

        viewModelScope.launch {
            // loop detection
            val processIdList = mutableListOf<Long>()
            var currentID = processId
            var noLoopDetectedSoFar = true
            var firstRound = true

            while (currentID >= 0 && noLoopDetectedSoFar) {
                processIdList.add(currentID)
                val process = repository.loadProcessById(currentID)
                if (process != null) {
                    val partialResult =
                        getProcessStepListForOneProcess(process, firstRound, numberOfPreBeeps)
                    partialResult.forEach { actionList ->
                        result.add(actionList)
                    }
                    // prepare for the next iteration
                    firstRound = false
                    if (process.gotoId != null && process.gotoId != -1L) {
                        currentID = process.gotoId
                        if (processIdList.contains(currentID)) {
                            noLoopDetectedSoFar = false // LOOP!
                            var earliestStepNumberForLoop = -1
                            var i = 0
                            while (i < result.size && earliestStepNumberForLoop < 0) {
                                val checkPoint = result.get(i)
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
                                    val ftpjumpAction = ProcessJumpbackAction(
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
            }
            // this is where the list is ready
            _actionsListList.value = result
        }
    }

    fun go() {

    }
}