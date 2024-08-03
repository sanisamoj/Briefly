package com.sanisamoj.config

import com.sanisamoj.config.GlobalContext.PUBLIC_IMAGES_DIR
import com.sanisamoj.database.mongodb.MongoDatabase
import com.sanisamoj.utils.schedule.ScheduleRoutine
import com.sanisamoj.utils.schedule.models.JobIdentification
import com.sanisamoj.utils.schedule.models.RoutineGroups
import com.sanisamoj.utils.schedule.models.StartRoutineData
import com.sanisamoj.utils.schedule.routines.RemoveNonAccessedLinksRoutine
import com.sanisamoj.utils.schedule.routines.UpdateExpiredLinksRoutine
import org.quartz.JobKey
import java.time.LocalDateTime

object Config {
    private val jobsIdentificationList: MutableList<JobIdentification> = mutableListOf()
    private const val EVERY_DAY_AT_3PM_CRON: String = "0 0 2 * * ?"

    val TWELVE_MONTHS_AGO: LocalDateTime = LocalDateTime.now().minusMonths(12)

    suspend fun databaseInitialize() {
        MongoDatabase.initialize()

        val uploadDir = PUBLIC_IMAGES_DIR
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }
    }

    fun routinesInitialize() {
        updateExpiredLinksRoutine()
        removeInactiveLinksRoutine()
    }

    private fun updateExpiredLinksRoutine() {
        val routineName = "UpdateExpiredLinksRoutine-EveryDay_At3PM"
        val startRoutineData = StartRoutineData(
            name = routineName,
            group = RoutineGroups.LinkEntryCleanUp,
            cronExpression = EVERY_DAY_AT_3PM_CRON
        )

        val jobKey: JobKey = ScheduleRoutine().startRoutine<UpdateExpiredLinksRoutine>(startRoutineData)

        val jobIdentification = JobIdentification(jobKey, routineName)
        jobsIdentificationList.add(jobIdentification)
    }

    private fun removeInactiveLinksRoutine() {
        val routineName = "RemoveInactiveLinksRoutine-EveryDay_At3PM"
        val startRoutineData = StartRoutineData(
            name = routineName,
            group = RoutineGroups.LinkEntryCleanUp,
            cronExpression = EVERY_DAY_AT_3PM_CRON
        )

        val jobKey: JobKey = ScheduleRoutine().startRoutine<RemoveNonAccessedLinksRoutine>(startRoutineData)

        val jobIdentification = JobIdentification(jobKey, routineName)
        jobsIdentificationList.add(jobIdentification)
    }

}