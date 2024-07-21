package com.sanisamoj

import com.sanisamoj.data.models.dataclass.UserAgentInfo
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer
import com.sanisamoj.data.models.interfaces.SessionRepository

object TestContext {
    private val testContainer: ServerContainer = ServerContainerTest()
    const val IP_TEST: String = "186.204.44.176"

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
        subOperatingSystem = "Windows 10",
        operatingSystemDetails = listOf("64-bit"),
        browserEngine = "Gecko",
        browserEngineDetails = listOf("rv:89.0"),
        webKit = "Not applicable",
        browser = "Firefox"
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
}