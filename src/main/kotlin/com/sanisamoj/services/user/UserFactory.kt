package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext.MEDIA_ROUTE
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountType
import org.mindrot.jbcrypt.BCrypt

object UserFactory {
    fun userResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id.toString(),
            username = user.username,
            profileImageUrl = if(user.imageProfile == "") "" else MEDIA_ROUTE + user.imageProfile,
            email = user.email,
            phone = user.phone,
            createdAt = user.createdAt,
        )
    }

    // Transforms the user creation request into USER
    fun user(userCreateRequest: UserCreateRequest, accountType: AccountType = AccountType.USER): User {
        val hashedPassword: String = BCrypt.hashpw(userCreateRequest.password, BCrypt.gensalt())
        return User(
            username = userCreateRequest.username,
            email = userCreateRequest.email,
            phone = userCreateRequest.phone,
            password = hashedPassword,
            type = accountType.name
        )
    }
}