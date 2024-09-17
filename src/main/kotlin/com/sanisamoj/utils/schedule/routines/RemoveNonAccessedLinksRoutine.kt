package com.sanisamoj.utils.schedule.routines

import com.sanisamoj.config.Config.INACCESSIBLE_TIME_MAX
import com.sanisamoj.config.Config.NOTIFY_EXPIRATION_TIME
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.services.email.MailService
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.LocalDateTime

class RemoveNonAccessedLinksRoutine: Job {
    private val databaseRepository: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }
    private val mailService: MailService by lazy { MailService() }
    private val botRepository: BotRepository by lazy { GlobalContext.getBotRepository() }

    override fun execute(p0: JobExecutionContext?) {
        runBlocking {
            checkAndInactiveNonAccessLinks()
            notifyAboutExpiration()
        }
    }

    private suspend fun checkAndInactiveNonAccessLinks(inaccessibleTime: LocalDateTime = INACCESSIBLE_TIME_MAX) {
        val allLinksNonAccess: List<LinkEntry> = verifyAndReturnAllInactiveNonAccessLinksIn(inaccessibleTime)

        for (link in allLinksNonAccess) {
            databaseRepository.deleteLinkByShortLink(link.shortLink)

            // Send an email and whatsapp message warning that the link has not been accessed for 3 years, so it has been removed from the database
            val user: User? = databaseRepository.getUserByIdOrNull(link.userId)
            if(user != null) {
                databaseRepository.removeLinkEntryIdFromUser(user.id.toString(), link.id.toString())
                mailService.sendLinkDeletedEmail(user.username, link, user.email)

                val messageToSend = MessageToSend(user.phone, GlobalContext.globalWarnings.linkDeletedMail)
                botRepository.sendMessage(messageToSend)
            }
        }
    }

    private suspend fun notifyAboutExpiration(expiration: LocalDateTime = NOTIFY_EXPIRATION_TIME) {
        val allLinksNonAccess: List<LinkEntry> = verifyAndReturnAllInactiveNonAccessLinksIn(expiration)

        allLinksNonAccess.forEach { linkEntry ->
            val user: User = databaseRepository.getUserById(linkEntry.userId)
            val message: String = GlobalContext.globalWarnings.someExpirationLinkTime
            val messageToSend = MessageToSend(user.phone, message)

            botRepository.sendMessage(messageToSend)
        }
    }

    private suspend fun verifyAndReturnAllInactiveNonAccessLinksIn(time: LocalDateTime): List<LinkEntry> {
        val timeAgo: LocalDateTime = time

        val allLinksNonAccess: MutableList<LinkEntry> = mutableListOf()
        val allLinks: List<LinkEntry> = databaseRepository.getAllLinkEntries()

        allLinks.forEach {

            if(it.totalVisits.isEmpty()) {
                if(converterStringToLocalDateTime(it.createdAt).isBefore(timeAgo)) {
                    allLinksNonAccess.add(it)
                }
                return@forEach
            }

            val lastClicker: Clicker = it.totalVisits.last()
            val lastAccess: String = lastClicker.clickedAt
            if(converterStringToLocalDateTime(lastAccess).isBefore(timeAgo)) {
                allLinksNonAccess.add(it)
            }
        }

        return allLinksNonAccess
    }
}