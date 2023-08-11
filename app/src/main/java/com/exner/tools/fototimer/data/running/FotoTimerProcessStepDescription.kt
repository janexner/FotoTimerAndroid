package com.exner.tools.fototimer.data.running

enum class FotoTimerStepType {
    PROCESS, CHAIN
}

/***
 * The StepDescription contains information about a single step in the list of steps
 * that make a whole running process.
 * The only mandatory field is the targetElapsedTime, which the runner uses to decide
 * when to do this step.
 *
 * Other fields are:
 * - totalTime - the total run time of the steps list
 * - totalElapsedTime - the elapsed time of the steps list
 * - processTime - the total run time of the current process
 * - processElapsedTime - the elapsed time for the current process
 * - intervalTime - the interval time of the current process
 * - intervalNum - which interval we're in right now
 * - type - process or chain
 * - activities - a list of things to do when this step is handled (display, sound, chain, ...)
 */
class FotoTimerProcessStepDescription(
    val targetElapsedTime: Long,
    private val activities: MutableList<FotoTimerProcessStepActivity> = mutableListOf(),
    totalTime: Long = 0,
    totalElapsedTime: Long = 0,
    processTime: Long = 0,
    processElapsedTime: Long = 0,
    intervalTime: Long = 0,
    intervalNum: Int = 0,
    type: FotoTimerStepType = FotoTimerStepType.PROCESS
) {
    fun addActivity(activity: FotoTimerProcessStepActivity) {
        activities.add(activity)
    }

    fun doActivities() {
        for (activity: FotoTimerProcessStepActivity in activities) {
            activity.doActivity()
        }
    }
}
