package com.sanisamoj.config

import com.sanisamoj.config.GlobalContext.PUBLIC_IMAGES_DIR
import com.sanisamoj.database.mongodb.MongoDatabase
import com.sanisamoj.errors.Logger
import com.sanisamoj.utils.schedule.ScheduleRoutine
import com.sanisamoj.utils.schedule.models.JobIdentification
import com.sanisamoj.utils.schedule.models.RoutineGroups
import com.sanisamoj.utils.schedule.models.StartRoutineData
import com.sanisamoj.utils.schedule.routines.RemoveNonAccessedLinksRoutine
import com.sanisamoj.utils.schedule.routines.UpdateBotApiToken
import com.sanisamoj.utils.schedule.routines.UpdateExpiredLinksRoutine
import com.sanisamoj.utils.schedule.routines.UpdateLogApiToken
import org.quartz.JobKey
import java.io.File
import java.time.LocalDateTime

object Config {
    private val jobsIdentificationList: MutableList<JobIdentification> = mutableListOf()
    private const val EVERY_DAY_AT_3PM_CRON: String = "0 0 2 * * ?"
    private const val EVERY_TWO_YEARS_CRON: String = "0 0 0 1 1 ? */2"
    private const val EVERY_YEAR_CRON: String = "0 0 0 1 1 ? *"

    val INACCESSIBLE_TIME_MAX: LocalDateTime = LocalDateTime.now().minusMonths(36)
    val NOTIFY_EXPIRATION_TIME: LocalDateTime = LocalDateTime.now().minusDays(7)

    suspend fun initialize() {
        MongoDatabase.initialize()

        // Create the folder for the images
        val uploadDir: File = PUBLIC_IMAGES_DIR
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        try {
            // Update bot api token
            GlobalContext.getBotRepository().updateToken()
            // Update log api token
            Logger.updateToken()
        } catch (_: Throwable) {}
    }

    fun routinesInitialize() {
        updateExpiredLinksRoutine()
        removeInactiveLinksRoutine()
        updateBotApiToken()
        updateLogApiToken()
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

    private fun updateBotApiToken() {
        val routineName = "UpdateBotApiToken-EveryTwoYears_At00"
        val startRoutineData = StartRoutineData(
            name = routineName,
            group = RoutineGroups.TokenUpdate,
            cronExpression = EVERY_TWO_YEARS_CRON
        )

        val jobKey: JobKey = ScheduleRoutine().startRoutine<UpdateBotApiToken>(startRoutineData)
        val jobIdentification = JobIdentification(jobKey, routineName)
        jobsIdentificationList.add(jobIdentification)
    }

    private fun updateLogApiToken() {
        val routineName = "UpdateBotApiToken-EveryOneYears_At00"
        val startRoutineData = StartRoutineData(
            name = routineName,
            group = RoutineGroups.TokenUpdate,
            cronExpression = EVERY_YEAR_CRON
        )

        val jobKey: JobKey = ScheduleRoutine().startRoutine<UpdateLogApiToken>(startRoutineData)
        val jobIdentification = JobIdentification(jobKey, routineName)
        jobsIdentificationList.add(jobIdentification)
    }

}