package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.TokenInfo
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
}