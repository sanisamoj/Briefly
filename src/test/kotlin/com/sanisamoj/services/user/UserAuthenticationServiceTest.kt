package com.sanisamoj.services.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.utils.analyzers.dotEnv
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun activateAccountByTokenTest() = testApplication {}

    @Test
    fun loginWithInactiveAccountTest() = testApplication {
        val userRequest = TestContext.userCreateRequest
        val user = UserFactory.user(userRequest)
        databaseRepository.registerUser(user)

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val loginRequest = LoginRequest(userRequest.email, userRequest.password)

        assertFails {
            userAuthenticationService.login(loginRequest)
        }

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun loginWithBlockedAccountTest() = testApplication {
        val userRequest = TestContext.userCreateRequest
        val user = UserFactory.user(userRequest)
        databaseRepository.registerUser(user)

        val accountStatusUpdate = OperationField(Fields.AccountStatus, AccountStatus.Blocked.name)
        databaseRepository.updateUser(user.id.toString(), accountStatusUpdate)

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val loginRequest = LoginRequest(userRequest.email, userRequest.password)
        assertFails {
            userAuthenticationService.login(loginRequest)
        }

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun loginTest() = testApplication {
        val userRequest = TestContext.userCreateRequest
        val user = UserFactory.user(userRequest)
        databaseRepository.registerUser(user)

        val accountStatusUpdate = OperationField(Fields.AccountStatus, AccountStatus.Active.name)
        databaseRepository.updateUser(user.id.toString(), accountStatusUpdate)

        val updatedUser = databaseRepository.getUserById(user.id.toString())

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val loginRequest = LoginRequest(userRequest.email, userRequest.password)
        val loginResponse = userAuthenticationService.login(loginRequest)

        assertEquals(userRequest.email, loginResponse.account.email, "Email data comparison")
        assertEquals(updatedUser.id.toString(), loginResponse.account.id, "ID data comparison")
        assertEquals(updatedUser.accountStatus, AccountStatus.Active.name, "Account Status data comparison")

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun sessionTest() = testApplication {
        val userRequest = TestContext.userCreateRequest
        val user = UserFactory.user(userRequest)
        databaseRepository.registerUser(user)

        val accountStatusUpdate = OperationField(Fields.AccountStatus, AccountStatus.Active.name)
        databaseRepository.updateUser(user.id.toString(), accountStatusUpdate)

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            sessionRepository = TestContext.getSessionRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val userResponse = userAuthenticationService.session(user.id.toString())
        assertEquals(user.email, userResponse.email, "Email data comparison")
        assertEquals(user.id.toString(), userResponse.id, "ID data comparison")

        databaseRepository.deleteUser(user.id.toString())
    }

    @Test
    fun sessionWithRevokedSessionTest() = testApplication {
        val userRequest = TestContext.userCreateRequest
        val user = UserFactory.user(userRequest)
        databaseRepository.registerUser(user)

        val accountStatusUpdate = OperationField(Fields.AccountStatus, AccountStatus.Active.name)
        databaseRepository.updateUser(user.id.toString(), accountStatusUpdate)

        val userAuthenticationService = UserAuthenticationService(
            databaseRepository = TestContext.getDatabaseRepository(),
            sessionRepository = TestContext.getSessionRepository(),
            mailRepository = TestContext.getMailRepository()
        )

        val loginRequest = LoginRequest(userRequest.email, userRequest.password)
        val loginResponse = userAuthenticationService.login(loginRequest)
        val token = loginResponse.token
        val userSecret = dotEnv("USER_SECRET")

        val verifier = JWT.require(Algorithm.HMAC256(userSecret)).build()
        val decodedJWT = verifier.verify(token)
        val sessionId = decodedJWT.getClaim("session").asString()

        val sessionRepository = TestContext.getSessionRepository()
        sessionRepository.revokeSession(user.id.toString(), sessionId)

        val response = client.post("/authentication/session") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)

        databaseRepository.deleteUser(user.id.toString())
    }
}