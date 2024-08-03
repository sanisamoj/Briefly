package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository

class UserService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
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
}