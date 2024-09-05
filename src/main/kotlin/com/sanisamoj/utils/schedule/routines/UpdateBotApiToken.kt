package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.interfaces.BotRepository
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

class UpdateBotApiToken: Job {
    private val botRepository: BotRepository by lazy { GlobalContext.getBotRepository() }

    override fun execute(p0: JobExecutionContext?) {
        runBlocking { botRepository.updateToken() }
    }
}