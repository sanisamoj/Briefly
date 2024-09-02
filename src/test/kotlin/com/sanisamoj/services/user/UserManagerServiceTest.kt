package com.sanisamoj.services.user

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import com.sanisamoj.utils.eraseAllDataToTests
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class UserManagerServiceTest {
    private val databaseRepository: DatabaseRepository = TestContext.getDatabaseRepository()
    private val botRepository: BotRepository = TestContext.getBotRepository()

    init {
        eraseAllDataToTests()
    }

    @Test
    fun updateNameUserTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val userManagerService = UserManagerService(databaseRepository = databaseRepository)
        userManagerService.updateName(user.id.toString(), TestContext.UPDATED_NAME_TEST)

        val updatedUser: User = databaseRepository.getUserById(user.id.toString())
        assertEquals(TestContext.UPDATED_NAME_TEST, updatedUser.username)
        userTest.deleteUserTest()
    }

    @Test
    fun updatePhoneTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)
        val userId: String = user.id.toString()
        val userManagerService = UserManagerService(databaseRepository = databaseRepository, botRepository = botRepository)
        userManagerService.updatePhone(userId, "1111111111111")

        val updatedUser: User = databaseRepository.getUserById(userId)
        assertFails { userManagerService.validateValidationCodeToUpdatePhone(userId, "1111111111111", 123456) }

        val updatedUserResponse: UserResponse = userManagerService.validateValidationCodeToUpdatePhone(userId, "1111111111111", updatedUser.validationCode)
        assertEquals("1111111111111", updatedUserResponse.phone)
        userTest.deleteUserTest()
    }

    @Test
    fun updatePasswordTest() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)
        val userId: String = user.id.toString()
        val userManagerService = UserManagerService(databaseRepository = databaseRepository, botRepository = botRepository)
        userManagerService.updatePassword(userId)

        val updatedUser: User = databaseRepository.getUserById(userId)
        assertFails { userManagerService.validateValidationCodeToUpdatePassword(userId, "1111111111111", 123456) }

        userManagerService.validateValidationCodeToUpdatePassword(userId, "1111111111111", updatedUser.validationCode)
        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val loginRequest = LoginRequest(user.email, "1111111111111")
        val loginResponse = userAuthenticationService.login(loginRequest)

        assertEquals(user.email, loginResponse.account.email, "Email data comparison")
        assertEquals(updatedUser.id.toString(), loginResponse.account.id, "ID data comparison")
        assertEquals(updatedUser.accountStatus, AccountStatus.Active.name, "Account Status data comparison")

        userTest.deleteUserTest()
    }
}