package com.sanisamoj.services.moderator

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UsersWithPaginationResponse
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.utils.UserTest
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ModeratorManagerServiceTest {
    private val databaseRepository: DatabaseRepository by lazy { TestContext.getDatabaseRepository() }
    private val sessionRepository: SessionRepository by lazy { TestContext.getSessionRepository() }

    @Test
    fun blockUserById() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val moderatorManagerService = ModeratorManagerService(
            databaseRepository = databaseRepository,
            sessionRepository = sessionRepository
        )

        moderatorManagerService.blockUser(user.id.toString())
        val userInDb: User = databaseRepository.getUserById(user.id.toString())
        assertEquals(AccountStatus.Blocked.name, userInDb.accountStatus)

        databaseRepository.deleteUser(user.id.toString())
        assertFails { moderatorManagerService.blockUser("NotFoundUserId") }
    }

    @Test
    fun returnAllUserWithPagination() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val moderatorManagerService = ModeratorManagerService(
            databaseRepository = databaseRepository,
            sessionRepository = sessionRepository
        )

        val usersResponse: UsersWithPaginationResponse = moderatorManagerService.getAllUsersWithPagination(1, 10)
        assertEquals(1, usersResponse.users.size)
        assertEquals(1, usersResponse.paginationResponse.totalPages)
        assertEquals(0, usersResponse.paginationResponse.remainingPage)

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun returnUser() = testApplication {
        val userTest = UserTest()
        val user: User = userTest.createUserTest(accountStatus = AccountStatus.Active)

        val moderatorManagerService = ModeratorManagerService(
            databaseRepository = databaseRepository,
            sessionRepository = sessionRepository
        )

        val userByEmail = moderatorManagerService.getUserByEmail(user.email)
        val userById = moderatorManagerService.getUserById(user.id.toString())
        
        assertEquals(user.email, userById.email)
        assertEquals(user.id.toString(), userByEmail.id)

        databaseRepository.deleteUser(user.id.toString())
    }
}