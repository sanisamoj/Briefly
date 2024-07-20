package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import io.ktor.server.plugins.*
import java.time.LocalDateTime

class LinkEntryManager(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
) {

    suspend fun deleteShortLink(shortLink: String) {
        removeShortLinkIdFromUserByShortLink(shortLink)
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    suspend fun deleteShortLinkFromUser(userId: String, shortLink: String) {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        if(userId != linkEntry.userId) throw Exception(Errors.AccessProhibited.description)
        removeShortLinkIdFromUser(linkEntry.id.toString())
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    private suspend fun removeShortLinkIdFromUser(shortLinkId: String) {
        val linkEntry: LinkEntry = databaseRepository.getLinkById(shortLinkId)
        val userId: String = linkEntry.userId
        databaseRepository.removeLinkEntryIdFromUser(userId, shortLinkId)
    }

    private suspend fun removeShortLinkIdFromUserByShortLink(shortLink: String) {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw NotFoundException(Errors.UserNotFound.description)

        val userId: String = linkEntry.userId
        val linkEntryId: String = linkEntry.id.toString()
        databaseRepository.removeLinkEntryIdFromUser(userId, linkEntryId)
    }

    suspend fun updateLinkEntryStatusFromUser(userId: String, shortLink: String, status: Boolean) {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        if(userId != linkEntry.userId) throw Exception(Errors.AccessProhibited.description)

        val expiresAt: LocalDateTime = converterStringToLocalDateTime(linkEntry.expiresAt)
        val currentTime: LocalDateTime = LocalDateTime.now()
        if(expiresAt.isBefore(currentTime)) throw Exception(Errors.ExpiredLink.description)

        val update = OperationField(Fields.Active, status)
        databaseRepository.updateLinkByShortLink(shortLink, update)
    }
}