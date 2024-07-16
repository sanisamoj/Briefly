package com.sanisamoj.utils.schedule.models

import org.quartz.JobKey

data class JobIdentification(
    val jobKey : JobKey,
    val description: String? = null
)