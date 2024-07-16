package com.sanisamoj.utils.schedule.models

import java.util.concurrent.TimeUnit

data class StartRoutineData(
    val name: String,
    val description: String? = null,
    val group: RoutineGroups,
    val interval: Long = TimeUnit.DAYS.toMillis(1),
    val repeatForever: Boolean = true,
    val cronExpression: String? = null
)
