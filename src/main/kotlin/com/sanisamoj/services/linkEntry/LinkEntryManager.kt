package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import io.ktor.server.plugins.*

class LinkEntryManager(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
) {

    private suspend fun deleteShortLink(shortLink: String) {
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    private suspend fun updateLinkEntryStatus(shortLink: String, status: Boolean) {
        val update = OperationField(Fields.Active, status)
        databaseRepository.updateLinkByShortLink(shortLink, update)
    }

    suspend fun deleteShortLinkFromUser(userId: String, shortLink: String) {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        if(userId != linkEntry.userId) throw Exception(Errors.AccessProhibited.description)
        deleteShortLink(shortLink)
    }

    suspend fun updateLinkEntryStatusFromUser(userId: String, shortLink: String, status: Boolean) {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        if(userId != linkEntry.userId) throw Exception(Errors.AccessProhibited.description)

        val update = OperationField(Fields.Active, status)
        databaseRepository.updateLinkByShortLink(shortLink, update)
    }
}