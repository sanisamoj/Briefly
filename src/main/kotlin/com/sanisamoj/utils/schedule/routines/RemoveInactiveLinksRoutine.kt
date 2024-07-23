package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import com.sanisamoj.utils.schedule.routines.UpdateExpiredLinksRoutine.Companion.expiredLinks
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.LocalDateTime

class RemoveInactiveLinksRoutine: Job {
    private val databaseRepository: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }

    override fun execute(p0: JobExecutionContext?) {
        runBlocking { checkAndDeleteExpiredLinks() }
    }

    private suspend fun checkAndDeleteExpiredLinks() {
        val currentTime = LocalDateTime.now()
        val threeDaysAgo = currentTime.minusDays(3)

        val linksToDelete = expiredLinks.filter { converterStringToLocalDateTime(it.expiresAt).isBefore(threeDaysAgo) }
        expiredLinks.removeAll(linksToDelete)

        for (link in linksToDelete) {
            databaseRepository.deleteLinkByShortLink(link.shortLink)

            try {
                val user: User = databaseRepository.getUserById(link.userId)
                databaseRepository.removeLinkEntryIdFromUser(user.id.toString(), link.id.toString())
            } catch (_: Throwable) {
                // Intentionally ignored
            }
        }
    }
}