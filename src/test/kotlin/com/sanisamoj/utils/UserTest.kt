package com.sanisamoj.utils

import com.sanisamoj.TestContext
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.user.UserAuthenticationService
import com.sanisamoj.services.user.UserService

class UserTest(
    private val databaseRepository: DatabaseRepository = TestContext.getDatabaseRepository(),
    private val mailRepository: MailRepository = TestContext.getMailRepository(),
    private val userCreateTestRequest: UserCreateRequest = TestContext.userCreateRequest
) {
    private var user: User? = null
    private var token: String? = null

    suspend fun createUserTest(
        accountStatus: AccountStatus = AccountStatus.Inactive,
        accountType: AccountType = AccountType.USER
    ): User {
        val userService = UserService(databaseRepository = databaseRepository)
        val userResponse: UserResponse = userService.createUser(userCreateTestRequest, accountType)

        val accountStatusUpdate = OperationField(Fields.AccountStatus, accountStatus.name)
        val userInDb: User = databaseRepository.updateUser(userResponse.id, accountStatusUpdate)
        user = userInDb
        return userInDb
    }

    suspend fun token(): String {

        if(token == null) {
            if(user == null) createUserTest(accountStatus = AccountStatus.Active)

            val userAuthenticationService = UserAuthenticationService(
                databaseRepository = databaseRepository,
                mailRepository = mailRepository
            )

            val loginRequest = LoginRequest(userCreateTestRequest.email, userCreateTestRequest.password)
            val loginResponse: LoginResponse = userAuthenticationService.login(loginRequest)
            token = loginResponse.token
        }

        return token!!
    }

    suspend fun deleteUserTest() {
        databaseRepository.deleteUser(user?.id.toString())
    }
}