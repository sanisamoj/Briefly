package com.sanisamoj.utils.schedule.models

data class StartRoutineData(
    val name: String,
    val description: String? = null,
    val group: RoutineGroups,
    val interval: Long,
    val repeatForever: Boolean = true
)
