package com.sanisamoj.services.linkEntry

import com.sanisamoj.TestContext
import com.sanisamoj.TestContext.IP_TEST
import com.sanisamoj.TestContext.LINK_PASSWORD_TEST
import com.sanisamoj.TestContext.PERSONALIZED_CODE_TO_LINK
import com.sanisamoj.TestContext.SHORT_LINK_TEST
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.server.testing.*
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotEquals

class LinkEntryServiceTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun registerLinkEntryTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = SHORT_LINK_TEST,
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        assertEquals(linkEntryRequest.userId, linkEntryResponse.userId)
        assertEquals(linkEntryRequest.active, linkEntryResponse.active)
        assertEquals("https://linktest/", linkEntryResponse.originalLink)

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
            link = SHORT_LINK_TEST,
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
            link = SHORT_LINK_TEST,
            active = true,
            expiresIn = LocalDateTime.now().plusDays(700).toString()
        )

        val expiresInTest: LocalDateTime = LocalDateTime.now().plusDays(365)
        val linkEntryService = LinkEntryService(
            databaseRepository = TestContext.getDatabaseRepository(),
            expiresIn = expiresInTest
        )

        val linkEntryResponseWithExcessiveDateTest: LinkEntryResponse = linkEntryService.register(linkEntryRequest)
        val linkEntryResponseWithEarlyDateTest: LinkEntryResponse = linkEntryService.register(
            linkEntryRequest.copy(expiresIn = "2023-07-08T18:37:02.045716800")
        )

        assertNotEquals(expiresInTest.toString(), linkEntryResponseWithExcessiveDateTest.expiresAt)
        assertEquals(expiresInTest.toString(), linkEntryResponseWithEarlyDateTest.expiresAt)

        userTest.deleteUserTest()
        databaseRepository.deleteLinkByShortLink(
            shortLink = linkEntryResponseWithExcessiveDateTest.shortLink.substringAfterLast("/")
        )
        databaseRepository.deleteLinkByShortLink(
            shortLink = linkEntryResponseWithEarlyDateTest.shortLink.substringAfterLast("/")
        )
    }

    @Test
    fun redirectInactiveLink() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = SHORT_LINK_TEST,
            active = false,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)

        val redirectInfo = RedirectInfo(
            ip = "186.204.44.176",
            shortLink = SHORT_LINK_TEST,
            userAgent = TestContext.userAgentInfoTest,
            referer = "Test"
        )

        assertFails {
            linkEntryService.redirectLink(redirectInfo)
        }

        userTest.deleteUserTest()
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun countClickerTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = SHORT_LINK_TEST,
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")

        val redirectInfo = RedirectInfo(
            ip = IP_TEST,
            shortLink = shortLink,
            userAgent = TestContext.userAgentInfoTest,
            referer = "test"
        )

        linkEntryService.redirectLink(redirectInfo)
        linkEntryService.redirectLink(redirectInfo)
        // Wait for the coroutine scope to finish
        delay(TimeUnit.SECONDS.toMillis(2))
        val linkEntry = databaseRepository.getLinkByShortLink(shortLink)!!

        assertEquals(2, linkEntry.totalVisits.size)
        assertEquals(IP_TEST, linkEntry.totalVisits[0].ip)
        assertEquals(TestContext.userAgentInfoTest.deviceType, linkEntry.totalVisits[0].deviceInfo.deviceType)
        assertEquals(TestContext.userAgentInfoTest.browser, linkEntry.totalVisits[0].deviceInfo.browser)

        userTest.deleteUserTest()
        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun registerPublicLinkEntryAndGetInfoTest() = testApplication {
        val linkEntryRequest = LinkEntryRequest(
            userId = IP_TEST,
            link = SHORT_LINK_TEST,
            active = true,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest, public = true)

        assertEquals(IP_TEST, linkEntryResponse.userId)
        assertEquals(linkEntryRequest.active, linkEntryResponse.active)
        assertEquals("https://linktest/", linkEntryResponse.originalLink)

        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        val midLinkEntryResponse: MidLinkEntryResponse = linkEntryService.getPublicLinkEntryInfoByShortLink(shortLink)

        assertEquals(linkEntryRequest.active, midLinkEntryResponse.active)
        assertEquals("https://linktest/", midLinkEntryResponse.originalLink)

        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun registerPublicLinkEntryWithPasswordTest() = testApplication {
        val linkEntryRequest = LinkEntryRequest(
            userId = IP_TEST,
            link = SHORT_LINK_TEST,
            active = true,
            password = LINK_PASSWORD_TEST,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest, public = true)

        assertEquals(IP_TEST, linkEntryResponse.userId)
        assertEquals(linkEntryRequest.active, linkEntryResponse.active)
        assertEquals("https://linktest/", linkEntryResponse.originalLink)

        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        assertFails {
            linkEntryService.redirectLink(TestContext.redirectInfoTest, null)
        }

        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun registerPublicLinkEntryWithPersonalizedCodeTest() = testApplication {
        val linkEntryRequest = LinkEntryRequest(
            userId = IP_TEST,
            link = SHORT_LINK_TEST,
            active = true,
            password = LINK_PASSWORD_TEST,
            personalizedCode = PERSONALIZED_CODE_TO_LINK,
            expiresIn = LocalDateTime.now().plusDays(5).toString()
        )

        val linkEntryService = LinkEntryService(databaseRepository = TestContext.getDatabaseRepository())
        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest, public = true)

        assertEquals(IP_TEST, linkEntryResponse.userId)
        assertEquals(linkEntryRequest.active, linkEntryResponse.active)
        assertEquals("https://linktest/", linkEntryResponse.originalLink)

        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        assertEquals(PERSONALIZED_CODE_TO_LINK, shortLink)

        databaseRepository.deleteLinkByShortLink(shortLink)
    }
}