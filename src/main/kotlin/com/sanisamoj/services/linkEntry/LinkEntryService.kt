package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.analyzers.dotEnv
import com.sanisamoj.utils.analyzers.hasEmptyStringProperties
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import com.sanisamoj.utils.generators.CharactersGenerator
import io.ktor.server.plugins.*
import org.bson.types.ObjectId
import java.time.LocalDateTime

class LinkEntryService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val expiresIn: LocalDateTime = LocalDateTime.now().plusDays(365)
) {
    suspend fun register(linkEntryRequest: LinkEntryRequest): LinkEntryResponse {
        hasEmptyStringProperties(linkEntryRequest)
        val shortLink: String = generateShortLink()

        var validatedExpiresIn: LocalDateTime = converterStringToLocalDateTime(linkEntryRequest.expiresIn)
        val currentTime: LocalDateTime = LocalDateTime.now()

        if(validatedExpiresIn.isAfter(expiresIn) || validatedExpiresIn.isBefore(currentTime)) {
            validatedExpiresIn = expiresIn
        }

        val linkEntry = LinkEntry(
            id = ObjectId(),
            userId = linkEntryRequest.userId,
            active = linkEntryRequest.active,
            shortLink = shortLink,
            originalLink = linkEntryRequest.link,
            expiresAt = validatedExpiresIn.toString()
        )

        databaseRepository.registerLink(linkEntry)
        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }

    private suspend fun generateShortLink(): String {
        var shortLink: String
        var shortLinkAlreadyExists: Boolean

        do {
            shortLink = CharactersGenerator.generateWithNoSymbols()
            shortLinkAlreadyExists = databaseRepository.getLinkByShortLink(shortLink) != null
        } while (shortLinkAlreadyExists)

        return shortLink
    }

    suspend fun redirectLink(redirectInfo: RedirectInfo): String {
        val link: LinkEntry = databaseRepository.getLinkByShortLink(redirectInfo.shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        val deviceType = redirectInfo.userAgent.deviceType
        val subOperatingSystem = redirectInfo.userAgent.subOperatingSystem
        val operatingSystem = "${redirectInfo.userAgent.operatingSystem} $subOperatingSystem"
        val browser = redirectInfo.userAgent.browser
        val deviceInfo = DeviceInfo(deviceType, operatingSystem, browser)


        return link.originalLink
    }

    private suspend fun getLinkEntryByShortLink(shortLink: String): LinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw Exception(Errors.ShortLinkNotFound.description)

        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }
}