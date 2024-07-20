package com.sanisamoj.services.linkEntry

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.LinkEntryRequest
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class LinkEntryManagerTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun deleteLinkEntryFromUserTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        val linkEntryManger = LinkEntryManager(databaseRepository)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        assertFails { linkEntryManger.deleteShortLinkFromUser("otherUser", shortLink) }

        linkEntryManger.deleteShortLinkFromUser(user.id.toString(), shortLink)
        val linkEntry: LinkEntry? = databaseRepository.getLinkByShortLink(shortLink)
        assertNull(linkEntry)

        userTest.deleteUserTest()
    }

    @Test
    fun deleteLinkEntryTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        val linkEntryManger = LinkEntryManager(databaseRepository)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        linkEntryManger.deleteShortLink(shortLink)

        val shortLinkExist: LinkEntry? = databaseRepository.getLinkByShortLink(shortLink)
        assertNull(shortLinkExist)

        val updatedUser: User = databaseRepository.getUserById(user.id.toString())
        assertEquals(0, updatedUser.shortLinksId.size)

        userTest.deleteUserTest()
    }

    @Test
    fun updateActiveStatusFromLinkEntryTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        val linkEntryManger = LinkEntryManager(databaseRepository)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        linkEntryManger.updateLinkEntryStatusFromUser(user.id.toString(), shortLink, false)

        assertFails {
            linkEntryManger.updateLinkEntryStatusFromUser(
                userId = "otherUser",
                shortLink = shortLink,
                status = false
            )
        }

        linkEntryManger.updateLinkEntryStatusFromUser(
            userId = user.id.toString(),
            shortLink = shortLink,
            status = false
        )

        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)!!
        assertEquals(false, linkEntry.active)

        userTest.deleteUserTest()
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun updateActiveStatusFromExpiredLinkEntryTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().toString()
        )

        val linkEntryService = LinkEntryService(
            databaseRepository = TestContext.getDatabaseRepository(),
            expiresIn = LocalDateTime.now().minusYears(2)
        )

        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        val linkEntryManger = LinkEntryManager(databaseRepository)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")

        assertFails {
            linkEntryManger.updateLinkEntryStatusFromUser(
                userId = user.id.toString(),
                shortLink = shortLink,
                status = false
            )
        }


        val linkEntry: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)!!
        assertEquals(true, linkEntry.active)

        userTest.deleteUserTest()
        databaseRepository.deleteLinkByShortLink(shortLink)
    }
}