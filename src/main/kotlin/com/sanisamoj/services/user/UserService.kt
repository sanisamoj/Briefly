package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.ReportingRequest
import com.sanisamoj.data.models.dataclass.TokenInfo
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.email.MailService
import com.sanisamoj.utils.analyzers.dotEnv
import com.sanisamoj.utils.generators.Token
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import org.mindrot.jbcrypt.BCrypt

class UserService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val mailRepository: MailRepository = GlobalContext.getMailRepository()
) {
    suspend fun createUser(userCreateRequest: UserCreateRequest, accountType: AccountType = AccountType.USER): UserResponse {
        verifyUserCreateRequest(userCreateRequest) // Check if there are any empty items
        val userAlreadyExist: Boolean = verifyIfUserAlreadyExists(userCreateRequest)
        if(userAlreadyExist) throw Exception(Errors.UserAlreadyExists.description)

        val user = UserFactory.user(userCreateRequest, accountType)
        val userInDatabase = databaseRepository.registerUser(user)
        val userResponse = UserFactory.userResponse(userInDatabase)
        return userResponse
    }

    suspend fun emitTokenToUpdatePassword(email: String) {
        val user: User = databaseRepository.getUserByEmail(email)
            ?: throw Exception(Errors.UserNotFound.description)

        val tokenInfo = TokenInfo(
            id = user.id.toString(),
            email = user.email,
            sessionId = ObjectId().toString(),
            secret = dotEnv("UPDATE_PASSWORD_SECRET"),
            time = GlobalContext.EMAIL_TOKEN_EXPIRATION
        )
        val token: String = Token.generate(tokenInfo)

        CoroutineScope(Dispatchers.IO).launch {
            MailService(mailRepository).sendUpdatePasswordEmail(user.username, token, user.email)
        }
    }

    suspend fun updatePassword(userId: String, newPassword: String) {
        val hashedPassword: String = BCrypt.hashpw(newPassword, BCrypt.gensalt())
        databaseRepository.updateUser(userId, OperationField(Fields.Password, hashedPassword))
    }

    private suspend fun verifyIfUserAlreadyExists(user: UserCreateRequest): Boolean {
        val userWithEmail = databaseRepository.getUserByEmail(user.email)
        val userWithPhone = databaseRepository.getUserByPhone(user.phone)
        return userWithEmail != null || userWithPhone != null
    }

    private fun verifyUserCreateRequest(userCreateRequest: UserCreateRequest) {
        val validations = mapOf(
            "Name" to userCreateRequest.username,
            "Email" to userCreateRequest.email,
            "Phone" to userCreateRequest.phone,
            "Password" to userCreateRequest.password
        )

        validations.forEach { (_, value) ->
            if (value.isEmpty()) {
                throw IllegalArgumentException(Errors.DataIsMissing.description)
            }
        }
    }

    suspend fun emitRemoveAccount(userId: String, reportingRequest: ReportingRequest) {
        databaseRepository.updateUser(
            userId = userId,
            update = OperationField(Fields.AccountStatus, AccountStatus.Suspended.name)
        )

        CoroutineScope(Dispatchers.IO).launch {
            val user: User = databaseRepository.getUserById(userId)
            MailService(mailRepository).sendAccountRemovalEmail(userId, user.email, reportingRequest.reporting)
        }
    }
}