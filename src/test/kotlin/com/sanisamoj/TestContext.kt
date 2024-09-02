package com.sanisamoj

import com.sanisamoj.data.models.dataclass.RedirectInfo
import com.sanisamoj.data.models.dataclass.UserAgentInfo
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer
import com.sanisamoj.data.models.interfaces.SessionRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.utils.io.streams.*
import java.io.File

object TestContext {
    private val testContainer: ServerContainer = ServerContainerTest()
    const val IP_TEST: String = "186.204.44.176"
    const val SHORT_LINK_TEST = "linkTest"
    const val PERSONALIZED_CODE_TO_LINK = "personalized"
    const val LINK_PASSWORD_TEST = "123456"
    const val IMAGE_TEST_NAME = "image.jpg"
    const val NOT_ALLOWED_IMAGE_TEST_NAME = "image2.raw"
    const val UPDATED_NAME_TEST = "Updated name Test"

    val userCreateRequest = UserCreateRequest(
        username = "test",
        email = "test@gmail.com",
        password = "test",
        phone = "test"
    )

    val userAgentInfoTest = UserAgentInfo(
        general = "Mozilla/5.0",
        deviceType = "Desktop",
        operatingSystem = "Windows",
        subOperatingSystem = "Windows",
        operatingSystemDetails = listOf("64-bit", "10"),
        browserEngine = "Gecko",
        browserEngineDetails = listOf("rv:89.0"),
        webKit = "Not applicable",
        browser = "Firefox"
    )

    val redirectInfoTest: RedirectInfo = RedirectInfo(
        ip = IP_TEST,
        shortLink = SHORT_LINK_TEST,
        userAgent = userAgentInfoTest,
        referer = "www.test.com.br"
    )

    fun getDatabaseRepository(): DatabaseRepository {
        return testContainer.databaseRepository
    }

    fun getSessionRepository(): SessionRepository {
        return testContainer.sessionRepository
    }

    fun getMailRepository(): MailRepository {
        return testContainer.mailRepository
    }

    fun getBotRepository(): BotRepository {
        return testContainer.botRepository
    }

    fun createMultiPartData(file: File, imageName: String): MultiPartData {
        return object : MultiPartData {
            private val items = listOf(
                PartData.FormItem("no", {}, headersOf(HttpHeaders.ContentDisposition, "form-data; name=\"private\"")),
                PartData.FileItem({ file.inputStream().asInput() }, {}, headersOf(
                    HttpHeaders.ContentType to listOf(ContentType.Image.JPEG.toString()),
                    HttpHeaders.ContentDisposition to listOf("form-data; name=\"image\"; filename=\"$imageName\"")
                ))
            )
            private var index = 0

            override suspend fun readPart(): PartData? {
                return if (index < items.size) items[index++] else null
            }

            fun dispose() {
                items.forEach { it.dispose() }
            }
        }
    }
}