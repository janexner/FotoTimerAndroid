package com.exner.tools.fototimer.data.persistence

class FotoTimerChainingDependencies(
    var changed: Boolean,
    var dependentProcessIdsAndNames: List<FotoTimerProcessIdAndName>
)