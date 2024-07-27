package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.LocalDateTime

class UpdateExpiredLinksRoutine : Job {
    private val database: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }

    companion object {
        val expiredLinks: MutableList<LinkEntry> = mutableListOf()
    }

    override fun execute(p0: JobExecutionContext?) {
        runBlocking {
            val expiredLinks: List<LinkEntry> = runBlocking { database.filterActiveAndExpiredLinks(LocalDateTime.now()) }

            launch {
                expiredLinks.forEach {
                    database.updateLinkByShortLink(
                        shortLink = it.shortLink,
                        update = OperationField(Fields.Active, false)
                    )
                }
            }

            launch {
                expiredLinks.forEach {
                    val updateExpiredLink = it.copy(active = false)
                    UpdateExpiredLinksRoutine.expiredLinks.add(updateExpiredLink)
                }
            }
        }
    }
}