package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.MAX_SHORT_LINK_BY_ACCOUNT
import com.sanisamoj.config.GlobalContext.UNKNOWN_USER_ID
import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.IpRepository
import com.sanisamoj.services.user.UserService
import com.sanisamoj.utils.analyzers.hasEmptyStringProperties
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import com.sanisamoj.utils.generators.CharactersGenerator
import io.ktor.server.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.time.LocalDateTime

class LinkEntryService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val ipRepository: IpRepository = GlobalContext.getIpRepository(),
    private val webSocketManager: WebSocketManager = WebSocketManager,
    private val expiresIn: LocalDateTime = GlobalContext.LINK_ENTRY_EXPIRES_IN,
    private val maxShortLinksAllowed: Int = MAX_SHORT_LINK_BY_ACCOUNT
) {
    suspend fun register(linkEntryRequest: LinkEntryRequest): LinkEntryResponse {
        hasEmptyStringProperties(linkEntryRequest)
        val shortLink: String = generateShortLink()

        var validatedExpiresIn: LocalDateTime = converterStringToLocalDateTime(linkEntryRequest.expiresIn)
        val currentTime: LocalDateTime = LocalDateTime.now()

        if(validatedExpiresIn.isAfter(expiresIn) || validatedExpiresIn.isBefore(currentTime)) {
            validatedExpiresIn = expiresIn
        }

        if(linkEntryRequest.userId != UNKNOWN_USER_ID) {
            val userResponse: UserResponse = UserService().getUserById(linkEntryRequest.userId)
            if(userResponse.linkEntryList.size >= maxShortLinksAllowed)
                throw Exception(Errors.MaximumShortLinksExceeded.description)
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

        if(!link.active) throw Exception(Errors.LinkIsNotActive.description)

        if(link.userId != UNKNOWN_USER_ID) {
            CoroutineScope(Dispatchers.IO).launch {
                addClickerInLinkEntry(redirectInfo)
                webSocketManager.notifyAboutShortLink(link.userId, link.shortLink)
            }
        }

        return link.originalLink
    }

    private suspend fun addClickerInLinkEntry(redirectInfo: RedirectInfo)  {
        val clicker = buildClicker(redirectInfo.ip, redirectInfo.userAgent)
        databaseRepository.addClickerInShortLink(redirectInfo.shortLink, clicker)
    }

    private suspend fun buildClicker(ip: String, userAgent: UserAgentInfo): Clicker {
        val ipInfo: IpInfo = ipRepository.getInfoByIp(ip)
        val deviceInfo: DeviceInfo = defineDeviceInfo(userAgent)
        val region = Region(ipInfo.city, ipInfo.region, ipInfo.country, ipInfo.postal)

        return Clicker(
            ip = ip,
            region = region,
            deviceInfo = deviceInfo
        )
    }

    private fun defineDeviceInfo(userAgent: UserAgentInfo): DeviceInfo {
        val deviceType = userAgent.deviceType
        val subOperatingSystem = userAgent.subOperatingSystem
        val operatingSystem = "${userAgent.operatingSystem} $subOperatingSystem"
        val browser = userAgent.browser
        return DeviceInfo(deviceType, operatingSystem, browser)
    }

    suspend fun getLinkEntryByShortLinkWithUserId(userId: String, shortLink: String): LinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw Exception(Errors.ShortLinkNotFound.description)

        if(userId != linkEntry.userId) throw Exception(Errors.AccessProhibited.description)

        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }

    suspend fun getLinkEntryByShortLink(shortLink: String): LinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw Exception(Errors.ShortLinkNotFound.description)

        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }

    suspend fun getPublicLinkEntryByShortLink(shortLink: String): MidLinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw Exception(Errors.ShortLinkNotFound.description)

        if(linkEntry.userId != UNKNOWN_USER_ID) throw Exception(Errors.AccessProhibited.description)

        return LinkEntryFactory.midLinkEntryResponse(linkEntry)
    }

    suspend fun getLinkEntryByShortLinkById(shortLinkId: String): LinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkById(shortLinkId)
        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }
}