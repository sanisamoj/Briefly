package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.config.Config
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.LocalDateTime

class UpdateExpiredLinksRoutine: Job {
    override fun execute(p0: JobExecutionContext?) {
        runBlocking {
            Config.checkAndDeleteExpiredLinks()

            val database: DatabaseRepository = GlobalContext.getDatabaseRepository()
            val expiredLinks: List<LinkEntry> = runBlocking { database.filterExpiredLinks(LocalDateTime.now()) }
            Config.addExpiredLinkEntryListInMemory(expiredLinks)

            expiredLinks.forEach {
                database.updateLinkByShortLink(
                    shortLink = it.shortLink,
                    update = OperationField(Fields.Active, false)
                )
            }
        }
    }
}