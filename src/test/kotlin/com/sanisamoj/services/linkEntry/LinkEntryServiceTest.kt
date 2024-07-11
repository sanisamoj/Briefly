package com.sanisamoj.services.linkEntry

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.LinkEntryRequest
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import io.ktor.server.testing.*
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class LinkEntryServiceTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    @Test
    fun registerLinkEntryTest() = testApplication {
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

        assertEquals(linkEntryRequest.userId, linkEntryResponse.userId)
        assertEquals(linkEntryRequest.active, linkEntryResponse.active)
        assertEquals(linkEntryRequest.link, linkEntryResponse.originalLink)

        userTest.deleteUserTest()
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun registerLinkWithEmptyEntryTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())

        assertFails {
            linkEntryService.register(linkEntryRequest.copy(userId = ""))
            linkEntryService.register(linkEntryRequest.copy(link = ""))
            linkEntryService.register(linkEntryRequest.copy(expiresIn = ""))
        }

        userTest.deleteUserTest()
    }

    @Test
    fun registerLinkEntryWithWrongExpirationTime() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().plusDays(700).toString()
        )

        val expiresInTest: LocalDateTime = LocalDateTime.now().plusDays(365)
        val linkEntryService = LinkEntryService(
            databaseRepository = TestContext.getDatabaseRepository(),
            expiresIn = expiresInTest
        )

        val linkEntryResponseWithExcessiveDateTest: LinkEntryResponse = linkEntryService.register(linkEntryRequest)
        val linkEntryResponseWithWithEarlyDateTest: LinkEntryResponse = linkEntryService.register(
            linkEntryRequest.copy(expiresIn = "2023-07-08T18:37:02.045716800")
        )

        assertEquals(expiresInTest.toString(), linkEntryResponseWithExcessiveDateTest.expiresAt)
        assertEquals(expiresInTest.toString(), linkEntryResponseWithWithEarlyDateTest.expiresAt)

        userTest.deleteUserTest()
        databaseRepository.deleteLinkByShortLink(
            shortLink = linkEntryResponseWithExcessiveDateTest.shortLink.substringAfterLast("/")
        )
        databaseRepository.deleteLinkByShortLink(
            shortLink = linkEntryResponseWithWithEarlyDateTest.shortLink.substringAfterLast("/")
        )
    }
}