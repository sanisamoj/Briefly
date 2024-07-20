package com.sanisamoj.routines

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.LinkEntryRequest
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.eraseAllDataToTests
import com.sanisamoj.utils.schedule.routines.UpdateExpiredLinksRoutine
import io.ktor.server.testing.*
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateExpiredLinksRoutineTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    init {
        eraseAllDataToTests()
    }

    @Test
    fun updateExpiredLinksRoutineTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val linkEntryRequest = LinkEntryRequest(
            userId = user.id.toString(),
            link = "linkTest",
            active = true,
            expiresIn = LocalDateTime.now().minusDays(10).toString()
        )

        val expiresInTest: LocalDateTime = LocalDateTime.now().minusYears(1)
        val linkEntryService = LinkEntryService(
            databaseRepository = TestContext.getDatabaseRepository(),
            expiresIn = expiresInTest
        )

        val linkEntryResponse: LinkEntryResponse = linkEntryService.register(linkEntryRequest)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        UpdateExpiredLinksRoutine().execute(null)

        val linkEntryInDb: LinkEntry = databaseRepository.getLinkByShortLink(shortLink)!!
        assertEquals(false, linkEntryInDb.active)

        databaseRepository.deleteLinkByShortLink(shortLink)
        userTest.deleteUserTest()
    }
}