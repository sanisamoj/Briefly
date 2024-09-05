package com.sanisamoj.services.user

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.GlobalContext.MODERATOR_TOKEN_EXPIRATION
import com.sanisamoj.config.GlobalContext.USER_TOKEN_EXPIRATION
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.email.MailService
import com.sanisamoj.utils.analyzers.dotEnv
import com.sanisamoj.utils.generators.Token
import io.ktor.server.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

class UserAuthenticationService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val sessionRepository: SessionRepository = GlobalContext.getSessionRepository(),
    private val mailRepository: MailRepository = GlobalContext.getMailRepository()
) {
    suspend fun generateValidationEmailToken(email: String) {
        val user: User = databaseRepository.getUserByEmail(email)
            ?: throw NotFoundException(Errors.UserNotFound.description)

        if(user.accountStatus == AccountStatus.Active.name) {
            throw NotFoundException(Errors.UnableToComplete.description)
        }

        val userAccountType: Boolean = user.type == AccountType.USER.name
        val tokenInfo = TokenInfo(
            id = user.id.toString(),
            email = user.email,
            sessionId = ObjectId().toString(),
            secret = dotEnv("USER_SECRET"),
            time = GlobalContext.EMAIL_TOKEN_EXPIRATION
        )

        val token: String = Token.generate(tokenInfo)
        CoroutineScope(Dispatchers.IO).launch {
            MailService(mailRepository).sendConfirmationTokenEmail(
                name = user.username,
                token = token,
                to = if(userAccountType) user.email else dotEnv("SUPERADMIN_EMAIL")
            )
        }
    }

    suspend fun activateAccountByToken(token: String) {
        val secret: String = dotEnv("USER_SECRET")
        val verifier = JWT.require(Algorithm.HMAC256(secret)).build()
        val decodedJWT = verifier.verify(token)
        val accountId = decodedJWT.getClaim("id").asString()
        val operation = OperationField(Fields.AccountStatus, AccountStatus.Active.name)
        databaseRepository.updateUser(accountId, operation)

        CoroutineScope(Dispatchers.IO).launch {
            val user: User = databaseRepository.getUserById(accountId)
            MailService(mailRepository).sendAccountActivationMail(user.username, user.email)
        }
    }

    suspend fun login(login: LoginRequest): LoginResponse {
        val user: User = databaseRepository.getUserByEmail(login.email)
            ?: throw NotFoundException(Errors.InvalidLogin.description)

        verifyUserStatus(user)

        val isPasswordCorrect: Boolean = BCrypt.checkpw(login.password, user.password)
        if (!isPasswordCorrect) throw Exception(Errors.InvalidLogin.description)

        val userResponse: UserResponse = UserFactory.userResponse(user)
        val sessionId: String = ObjectId().toString()

        val userAccountType: Boolean = user.type == AccountType.USER.name
        val time: Long = if(userAccountType) USER_TOKEN_EXPIRATION else MODERATOR_TOKEN_EXPIRATION
        val secret: String = if(userAccountType) {
            dotEnv("USER_SECRET")
        } else {
            dotEnv("MODERATOR_SECRET")
        }

        val tokenInfo = TokenInfo(
            id = userResponse.id,
            email = userResponse.email,
            sessionId = sessionId,
            secret = secret,
            time = time
        )
        val token: String = Token.generate(tokenInfo)

        addSessionEntry(userResponse.id, sessionId)

        return LoginResponse(userResponse, token)
    }

    suspend fun session(accountId: String): UserResponse {
        val user: User = databaseRepository.getUserById(accountId)
        verifyUserStatus(user)

        return UserFactory.userResponse(user)
    }

    suspend fun signOut(accountId: String, sessionId: String) {
        sessionRepository.revokeSession(accountId, sessionId)
    }

    private fun verifyUserStatus(user: User) {
        if(user.accountStatus == AccountStatus.Inactive.name) { throw Exception(Errors.InactiveAccount.description) }
        if(user.accountStatus == AccountStatus.Blocked.name) { throw Exception(Errors.BlockedAccount.description) }
        if(user.accountStatus == AccountStatus.Suspended.name) { throw Exception(Errors.SuspendedAccount.description) }
    }

    private suspend fun addSessionEntry(accountId: String, sessionId: String) {
        val sessionEntry = SessionEntry(sessionId)
        sessionRepository.setSessionEntry(accountId, sessionEntry)
    }
}