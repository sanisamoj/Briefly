package com.sanisamoj

import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer

object TestContext {
    private val testContainer: ServerContainer = ServerContainerTest()

    val userCreateRequest = UserCreateRequest(
        username = "test",
        email = "test@gmail.com",
        password = "test",
        phone = "test"
    )

    fun getDatabaseRepository(): DatabaseRepository {
        return testContainer.databaseRepository
    }

    fun getMailRepository(): MailRepository {
        return testContainer.mailRepository
    }
}