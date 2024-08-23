package com.sanisamoj.services.linkEntry

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.NO_EXPIRATION_TIME
import com.sanisamoj.config.GlobalContext.PERSONALIZED_CODE_MAX_LENGTH
import com.sanisamoj.config.GlobalContext.PERSONALIZED_CODE_MIN_LENGTH
import com.sanisamoj.config.GlobalContext.UNKNOWN
import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.IpRepository
import com.sanisamoj.utils.analyzers.hasEmptyStringProperties
import com.sanisamoj.utils.converters.converterStringToLocalDateTime
import com.sanisamoj.utils.generators.CharactersGenerator
import com.sanisamoj.utils.generators.completeAndBuildUrl
import io.ktor.server.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt
import java.time.LocalDateTime

class LinkEntryService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val ipRepository: IpRepository = GlobalContext.getIpRepository(),
    private val webSocketManager: WebSocketManager = WebSocketManager,
    private val expiresIn: LocalDateTime = GlobalContext.LINK_ENTRY_EXPIRES_IN
) {
    suspend fun register(linkEntryRequest: LinkEntryRequest, public: Boolean = false): LinkEntryResponse {
        hasEmptyStringProperties(
            instance = linkEntryRequest,
            propertiesToIgnore = listOf("expiresIn", "personalizedCode")
        )

        val shortLink: String = if(linkEntryRequest.personalizedCode != null) {
            checkPersonalizedShortLink(linkEntryRequest.personalizedCode)
            linkEntryRequest.personalizedCode
        } else {
            generateShortLink()
        }

        val originalLink: String = completeAndBuildUrl(linkEntryRequest.link)
            ?: throw Error(Errors.InvalidLink.description)

        val hashedPassword: String? = if(linkEntryRequest.password != null) {
            BCrypt.hashpw(linkEntryRequest.password, BCrypt.gensalt())
        } else null

        val linkEntry = LinkEntry(
            id = ObjectId(),
            userId = linkEntryRequest.userId,
            active = linkEntryRequest.active,
            shortLink = shortLink,
            password = hashedPassword,
            public = public,
            originalLink = originalLink,
            expiresAt = getCorrectExpirationValue(linkEntryRequest.expiresIn)
        )

        databaseRepository.registerLink(linkEntry)
        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }

    // Checks the expiration date by assigning the correct value
    private fun getCorrectExpirationValue(expires: String?): String {
        return if(expires == "" || expires == null) {
            NO_EXPIRATION_TIME
        } else {
            var validatedExpiresIn: LocalDateTime = converterStringToLocalDateTime(expires)
            val currentTime: LocalDateTime = LocalDateTime.now()
            if(validatedExpiresIn.isBefore(currentTime)) {
                validatedExpiresIn = expiresIn
            }
            validatedExpiresIn.toString()
        }
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

    private suspend fun checkPersonalizedShortLink(shortLink: String) {
        val linkEntry: LinkEntry? = databaseRepository.getLinkByShortLink(shortLink)
        if(shortLink.length >= PERSONALIZED_CODE_MAX_LENGTH || shortLink.length < PERSONALIZED_CODE_MIN_LENGTH)
            throw Error(Errors.LengthExceeded.description)
        if(linkEntry != null) throw Error(Errors.PersonalizedShortLinkAlreadyExist.description)
    }

    suspend fun redirectLink(redirectInfo: RedirectInfo, protected: ProtectedLinkEntryPass? = null): String {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(redirectInfo.shortLink)
            ?: throw NotFoundException(Errors.ShortLinkNotFound.description)

        if(!linkEntry.active) throw Exception(Errors.LinkIsNotActive.description)

        when(protected) {
            null -> {
                if(linkEntry.password != null) throw Exception(Errors.ProtectedLink.description)
            }

            else -> {
                val isPasswordCorrect: Boolean = BCrypt.checkpw(protected.password, linkEntry.password)
                if(!isPasswordCorrect) throw Exception(Errors.InvalidPassword.description)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            addClickerInLinkEntry(redirectInfo, linkEntry)
        }

        return linkEntry.originalLink
    }

    private suspend fun addClickerInLinkEntry(redirectInfo: RedirectInfo, linkEntry: LinkEntry)  {
        val clicker: Clicker = buildClicker(redirectInfo.ip, redirectInfo)
        val clickerResponse: ClickerResponse = LinkEntryFactory.clickerResponse(clicker)
        databaseRepository.addClickerInShortLink(redirectInfo.shortLink, clicker)

        if(!linkEntry.public) {
            val user: User? = databaseRepository.getUserByIdOrNull(linkEntry.userId)
            if(user != null) webSocketManager.notifyAboutShortLink(user.id.toString(), linkEntry.shortLink, clickerResponse)
        }
    }

    private suspend fun buildClicker(ip: String, redirectInfo: RedirectInfo): Clicker {
        val ipInfo: IpInfo = ipRepository.getInfoByIp(ip)
        val deviceInfo: DeviceInfo = defineDeviceInfo(redirectInfo.userAgent)
        val region = Region(
            city = ipInfo.city ?: UNKNOWN,
            cityIsoCode = ipInfo.cityIsoCode ?: UNKNOWN,
            country = ipInfo.country ?: UNKNOWN,
            countryIsoCode = ipInfo.countryIsoCode ?: UNKNOWN,
            continent = ipInfo.continent ?: UNKNOWN,
            latitude = ipInfo.latitude,
            longitude = ipInfo.longitude,
            postal = ipInfo.postal ?: UNKNOWN,
            timezone = ipInfo.timezone ?: UNKNOWN
        )

        return Clicker(
            ip = ip,
            region = region,
            deviceInfo = deviceInfo,
            referer = redirectInfo.referer
        )
    }

    private fun defineDeviceInfo(userAgent: UserAgentInfo): DeviceInfo {
        val deviceType: String = userAgent.deviceType
        val browser: String = userAgent.browser

        val operatingSystem: String = try {
            if(deviceType == "desktop") {
                "${userAgent.operatingSystem} ${userAgent.operatingSystemDetails[1]}"
            } else {
                "${userAgent.operatingSystemDetails[0]} ${userAgent.operatingSystemDetails[1]}"
            }
        } catch (_: Throwable) { UNKNOWN }

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

    suspend fun getPublicLinkEntryInfoByShortLink(shortLink: String): MidLinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)
            ?: throw Exception(Errors.ShortLinkNotFound.description)

        if(!linkEntry.public) throw Error(Errors.TheLinkHasOwner.description)

        return LinkEntryFactory.midLinkEntryResponse(linkEntry)
    }

    suspend fun getLinkEntryByShortLinkById(shortLinkId: String): LinkEntryResponse {
        val linkEntry: LinkEntry = databaseRepository.getLinkById(shortLinkId)
        return LinkEntryFactory.linkEntryResponse(linkEntry)
    }
}