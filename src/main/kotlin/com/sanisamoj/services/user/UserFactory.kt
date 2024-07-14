package com.sanisamoj.services.user

import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.dataclass.UserResponse
import org.mindrot.jbcrypt.BCrypt

object UserFactory {
    fun userResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id.toString(),
            username = user.username,
            email = user.email,
            phone = user.phone,
            shortLinksId = user.shortLinksId,
            createdAt = user.createdAt,
        )
    }

    // Transforms the user creation request into USER
    fun user(userCreateRequest: UserCreateRequest): User {
        val hashedPassword = BCrypt.hashpw(userCreateRequest.password, BCrypt.gensalt())
        return User(
            username = userCreateRequest.username,
            email = userCreateRequest.email,
            phone = userCreateRequest.phone,
            password = hashedPassword
        )
    }
}