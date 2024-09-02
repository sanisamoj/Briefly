package com.sanisamoj.services.moderator

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.Sessions
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.dataclass.UsersWithPaginationResponse
import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.services.user.UserFactory
import com.sanisamoj.utils.pagination.PaginationResponse
import com.sanisamoj.utils.pagination.paginationMethod
import io.ktor.server.plugins.*

class ModeratorManagerService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val sessionRepository: SessionRepository = GlobalContext.getSessionRepository()
) {
    suspend fun blockUser(userId: String) {
        val update = OperationField(Fields.AccountStatus, AccountStatus.Blocked.name)
        databaseRepository.updateUser(userId, update)

        val sessions: Sessions = sessionRepository.getSession(userId)
        sessions.liveSessions.forEach { sessionRepository.revokeSession(userId, it.sessionId) }
    }

    suspend fun getAllUsersWithPagination(page: Int, size: Int): UsersWithPaginationResponse {
        val usersCount: Int = databaseRepository.usersCount()
        val usersList: List<User> = databaseRepository.getAllUserWithPagination(page, size)

        val usersResponseList: MutableList<UserResponse> = mutableListOf()
        usersList.forEach {
            usersResponseList.add(UserFactory.userResponse(it))
        }

        val paginationResponse: PaginationResponse = paginationMethod(usersCount.toDouble(), size, page)
        return UsersWithPaginationResponse(usersResponseList, paginationResponse)
    }

    suspend fun getUserById(userId: String) : UserResponse {
        val user = databaseRepository.getUserById(userId)
        return UserFactory.userResponse(user)
    }

    suspend fun getUserByEmail(email: String): UserResponse {
        val user: User = databaseRepository.getUserByEmail(email)
            ?: throw NotFoundException(Errors.UserNotFound.description)

        return UserFactory.userResponse(user)
    }

    suspend fun deleteAccount(userId: String) {
        databaseRepository.deleteUser(userId)
    }
}