package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.errors.Logger
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

class UpdateLogApiToken: Job {
    override fun execute(p0: JobExecutionContext?) {
        runBlocking { Logger.updateToken() }
    }
}