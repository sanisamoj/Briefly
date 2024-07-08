package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.services.email.MailService
import com.sanisamoj.utils.generators.TokenGenerator
import io.ktor.server.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

class UserAuthenticationService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val mailRepository: MailRepository = GlobalContext.getMailRepository()
) {
    suspend fun generateValidationEmailToken(email: String) {
        val user = databaseRepository.getUserByEmail(email)
            ?: throw NotFoundException(Errors.UserNotFound.description)

        if(user.accountStatus == AccountStatus.Active.name) {
            throw NotFoundException(Errors.UnableToComplete.description)
        }

        val tokenInfo = TokenInfo(
            id = user.id.toString(),
            email = user.email,
            sessionId = ObjectId().toString(),
            time = GlobalContext.EMAIL_TOKEN_EXPIRATION
        )

        val token = TokenGenerator.user(tokenInfo)
        CoroutineScope(Dispatchers.IO).launch {
            MailService(mailRepository).sendConfirmationTokenEmail(
                name = user.username,
                token = token,
                to = user.email
            )
        }
    }

    suspend fun login(login: LoginRequest): LoginResponse {
        val user = databaseRepository.getUserByEmail(login.email)
            ?: throw NotFoundException(Errors.InvalidLogin.description)

        verifyUserStatus(user)

        val isPasswordCorrect = BCrypt.checkpw(login.password, user.password)
        if (!isPasswordCorrect) throw Exception(Errors.InvalidLogin.description)

        val userResponse = UserFactory.userResponse(user)
        val tokenInfo = TokenInfo(
            id = userResponse.id.toString(),
            email = userResponse.email,
            sessionId = ObjectId().toString(),
            time = GlobalContext.USER_TOKEN_EXPIRATION
        )
        val token = TokenGenerator.user(tokenInfo)

        return LoginResponse(userResponse, token)
    }

    private fun verifyUserStatus(user: User) {
        if(user.accountStatus == AccountStatus.Inactive.name) { throw Exception(Errors.InactiveAccount.description) }
        if(user.accountStatus == AccountStatus.Blocked.name) { throw Exception(Errors.BlockedAccount.description) }
    }
}