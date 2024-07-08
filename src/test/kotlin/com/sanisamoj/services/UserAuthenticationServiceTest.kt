package com.sanisamoj.services

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.user.UserAuthenticationService
import com.sanisamoj.services.user.UserFactory
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertFails

class UserAuthenticationServiceTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }

    @Test
    fun generateEmailTokenWithNonExistentUser() = testApplication {
        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        assertFails {
            userAuthenticationService.generateValidationEmailToken("Test")
        }
    }

    @Test
    fun generateEmailTokenWithExistentActiveUser() = testApplication {
        val user = UserFactory.user(TestContext.userCreateRequest)
        val userInDb = databaseRepository.registerUser(user)

        databaseRepository.updateUser(
            userId = userInDb.id.toString(),
            update = OperationField(Fields.AccountStatus, AccountStatus.Active.name)
        )

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        assertFails {
            userAuthenticationService.generateValidationEmailToken(user.email)
        }

        databaseRepository.deleteUser(user.id.toString())
    }
}