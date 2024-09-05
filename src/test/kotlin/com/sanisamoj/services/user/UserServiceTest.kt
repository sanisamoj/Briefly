package com.sanisamoj.services.user

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.ReportingRequest
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.enums.ReportType
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class UserServiceTest {
    private val databaseRepository: DatabaseRepository = TestContext.getDatabaseRepository()
    private val mailRepository: MailRepository = TestContext.getMailRepository()
    private val userCreateRequestTest = UserCreateRequest(
        username = "test",
        email = "test@gmail.com",
        password = "test",
        phone = "test"
    )

    init {
        eraseAllDataToTests()
    }

    @Test
    fun createUserTest() = testApplication {
        val userService = UserService(databaseRepository = databaseRepository)

        val user: UserResponse = userService.createUser(userCreateRequestTest)
        val userInDb: User? = databaseRepository.getUserByEmail(user.email)

        assertNotNull(userInDb)
        assertEquals(user.username, userInDb.username)
        assertEquals(user.email, userInDb.email)
        assertEquals(user.phone, userInDb.phone)
        assertEquals(user.id, userInDb.id.toString())
        assertEquals(AccountStatus.Inactive.name, userInDb.accountStatus)
        assertEquals(AccountType.USER.name, userInDb.type)
        assertEquals(user.createdAt, userInDb.createdAt)

        databaseRepository.deleteUser(user.id)
    }

    @Test
    fun createModeratorTest() = testApplication {
        val userService = UserService(databaseRepository = databaseRepository)

        val user: UserResponse = userService.createUser(userCreateRequestTest, AccountType.MODERATOR)
        val userInDb: User? = databaseRepository.getUserByEmail(user.email)

        assertNotNull(userInDb)
        assertEquals(user.username, userInDb.username)
        assertEquals(user.email, userInDb.email)
        assertEquals(user.phone, userInDb.phone)
        assertEquals(user.id, userInDb.id.toString())
        assertEquals(AccountStatus.Inactive.name, userInDb.accountStatus)
        assertEquals(AccountType.MODERATOR.name, userInDb.type)
        assertEquals(user.createdAt, userInDb.createdAt)

        databaseRepository.deleteUser(user.id)
    }

    @Test
    fun createUserWithDataMissingTest() = testApplication {
        val userService = UserService(databaseRepository = databaseRepository)

        assertFails {
            userService.createUser(userCreateRequestTest.copy(username = ""))
            userService.createUser(userCreateRequestTest.copy(email = ""))
            userService.createUser(userCreateRequestTest.copy(password = ""))
            userService.createUser(userCreateRequestTest.copy(phone = ""))
        }
    }

    @Test
    fun createAlreadyExistsUser() = testApplication {
        val userService = UserService(databaseRepository = databaseRepository)
        val user: UserResponse = userService.createUser(userCreateRequestTest)

        assertFails {
            userService.createUser(userCreateRequestTest)
        }

        databaseRepository.deleteUser(user.id)
    }

    @Test
    fun emitRemoveAccountTest() = testApplication {
        runBlocking {
            val userTest = UserTest()
            val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

            val userService = UserService(
                databaseRepository = databaseRepository,
                mailRepository = mailRepository
            )

            val reportingRequest = ReportingRequest(reportType = ReportType.REMOVAL.name, reporting = "Motivo")
            userService.emitRemoveAccount(user.id.toString(), reportingRequest)

            val updatedUser: User = databaseRepository.getUserById(user.id.toString())
            assertEquals(AccountStatus.Suspended.name, updatedUser.accountStatus)
            userTest.deleteUserTest()
        }
    }

}