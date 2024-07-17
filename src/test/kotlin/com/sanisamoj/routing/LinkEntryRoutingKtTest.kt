package com.sanisamoj.routing

import com.sanisamoj.TestContext
import com.sanisamoj.config.GlobalContext.UNKNOWN_USER_ID
import com.sanisamoj.data.models.dataclass.LinkEntryRequest
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.module
import com.sanisamoj.plugins.configureSerialization
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.utils.UserTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.testing.*
import junit.framework.Assert.assertEquals
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertNotEquals

class LinkEntryRoutingKtTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    @Test
    fun getQRCodeFromLinkEntry() = testApplication {
        val linkEntryRequest = LinkEntryRequest(userId = UNKNOWN_USER_ID, link = "linkTest")
        val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(linkEntryRequest)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")

        client.get("/qrcode?short=$shortLink").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(ContentType.Image.PNG, contentType())
        }

        databaseRepository.deleteLinkByShortLink(shortLink)
    }

    @Test
    fun testDeleteModeratorLink() = testApplication {
        val linkEntryRequest = LinkEntryRequest(userId = UNKNOWN_USER_ID, link = "linkTest")
        val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(linkEntryRequest)
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")

        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active, AccountType.MODERATOR)
        val token: String = userTest.token()

        client.delete("/moderator/link?short=$shortLink") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        val nonExistentLink = "nonExistentLink"
        client.delete("/moderator/link?short=$nonExistentLink") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }.apply {
            assertNotEquals(HttpStatusCode.OK, status)
        }

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun testPutModeratorUserAccountStatus() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active, AccountType.MODERATOR)
        val token: String = userTest.token()

        client.put("/moderator/user?id=${user.id}&status=Blocked") {
            headers { append(HttpHeaders.Authorization, "Bearer $token") }
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
        }

        val userUpdated = databaseRepository.getUserById(user.id.toString())
        assertEquals(userUpdated.accountStatus, AccountStatus.Blocked.name)

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun testPostLink() = testApplication {
        application { install(ContentNegotiation) {
            json()
        } }
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active, AccountType.MODERATOR)
        val token: String = userTest.token()
        val expiresIn: String = LocalDateTime.now().toString()

        val response = client.post("/link") {
            headers {
                contentType(ContentType.Application.Json)
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(LinkEntryRequest(
                userId = user.id.toString(),
                link = "linkTest",
                expiresIn = expiresIn
            ))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)

            val linkEntryResponse = body<LinkEntryResponse>()
            assertEquals(linkEntryResponse.userId, user.id.toString())
            assertEquals(linkEntryResponse.expiresAt, expiresIn)
        }

        val linkEntryResponse = response.body<LinkEntryResponse>()
        val shortLink = linkEntryResponse.shortLink.substringAfterLast("/")
        databaseRepository.deleteLinkByShortLink(shortLink)

    }

    @Test
    fun testGetUserLinkEntry() = testApplication {
        application {
            module()
        }
        client.get("/link").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testPostGeneratePublicShortLink() = testApplication {
        application {
            module()
        }
        client.post("/generate").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetPublicShortLinkInfo() = testApplication {
        application {
            module()
        }
        client.get("/info").apply {
            TODO("Please write your test here")
        }
    }

    @Test
    fun testGetQrcode() = testApplication {
        application {
            module()
        }
        client.get("/qrcode").apply {
            TODO("Please write your test here")
        }
    }
}