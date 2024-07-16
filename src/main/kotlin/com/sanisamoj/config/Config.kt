package com.sanisamoj.config

import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.MongoDatabase
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import com.sanisamoj.utils.schedule.ScheduleRoutine
import com.sanisamoj.utils.schedule.models.JobIdentification
import com.sanisamoj.utils.schedule.models.RoutineGroups
import com.sanisamoj.utils.schedule.models.StartRoutineData
import com.sanisamoj.utils.schedule.routines.UpdateExpiredLinksRoutine
import org.quartz.JobKey
import java.time.LocalDateTime

object Config {
    private val jobsIdentificationList: MutableList<JobIdentification> = mutableListOf()
    private const val EVERY_DAY_AT_3PM_CRON: String = "0 0 3 * * ?"
    private val database: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }

    private var expiredLinks: MutableList<LinkEntry> = mutableListOf()

    suspend fun databaseInitialize() {
        MongoDatabase.initialize()
    }

    suspend fun checkAndDeleteExpiredLinks() {
        val currentTime = LocalDateTime.now()
        val threeDaysAgo = currentTime.minusDays(3)

        val linksToDelete = expiredLinks.filter { converterStringToLocalDateTime(it.expiresAt).isBefore(threeDaysAgo) }
        expiredLinks.removeAll(linksToDelete)

        for (link in linksToDelete) {
            database.deleteLinkByShortLink(link.shortLink)
        }
    }

    fun updateExpiredLinksRoutine() {
        val routineName = "inactiveExpiredLinkEntryEveryDay"
        val startRoutineData = StartRoutineData(
            name = routineName,
            group = RoutineGroups.LinkEntryCleanUp,
            cronExpression = EVERY_DAY_AT_3PM_CRON
        )

        val jobKey: JobKey = ScheduleRoutine().startRoutine<UpdateExpiredLinksRoutine>(startRoutineData)

        val jobIdentification = JobIdentification(jobKey, routineName)
        jobsIdentificationList.add(jobIdentification)
    }

    fun addExpiredLinkEntryListInMemory(expiredLinkEntryList: List<LinkEntry>) {
        expiredLinks.addAll(expiredLinkEntryList)
    }
}