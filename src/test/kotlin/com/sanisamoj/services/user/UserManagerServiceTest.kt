package com.sanisamoj.services.user

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.utils.UserTest
import io.ktor.server.testing.testApplication
import org.junit.Test
import kotlin.test.assertEquals

class UserManagerServiceTest {
    private val databaseRepository: DatabaseRepository = TestContext.getDatabaseRepository()

    @Test
    fun updateNameUser() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val userManagerService = UserManagerService(databaseRepository = databaseRepository)
        userManagerService.updateName(user.id.toString(), TestContext.UPDATED_NAME_TEST)

        val updatedUser: User = databaseRepository.getUserById(user.id.toString())
        assertEquals(TestContext.UPDATED_NAME_TEST, updatedUser.username)
        userTest.deleteUserTest()
    }
}