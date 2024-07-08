package com.sanisamoj.services

import com.sanisamoj.TestContext
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.services.user.UserService
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class UserServiceTest {
    private val databaseRepository: DatabaseRepository = TestContext.getDatabaseRepository()
    private val userCreateRequestTest = UserCreateRequest(
        username = "test",
        email = "test@gmail.com",
        password = "test",
        phone = "test"
    )

    @Test
    fun createUserTest() = testApplication {
        val userService = UserService(databaseRepository = databaseRepository)

        val user = userService.createUser(userCreateRequestTest)
        val userInDb = databaseRepository.getUserByEmail(user.email)

        assertNotNull(userInDb)
        assertEquals(user.username, userInDb.username)
        assertEquals(user.email, userInDb.email)
        assertEquals(user.phone, userInDb.phone)
        assertEquals(user.id, userInDb.id.toString())
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
        val user = userService.createUser(userCreateRequestTest)

        assertFails {
            userService.createUser(userCreateRequestTest)
        }

        databaseRepository.deleteUser(user.id)
    }
}