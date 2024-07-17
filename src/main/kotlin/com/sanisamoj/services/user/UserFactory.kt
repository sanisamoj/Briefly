package com.sanisamoj.services.user

import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.services.linkEntry.LinkEntryFactory
import com.sanisamoj.services.linkEntry.LinkEntryService
import kotlinx.coroutines.runBlocking
import org.mindrot.jbcrypt.BCrypt

object UserFactory {
    fun userResponse(user: User): UserResponse {
        val linkEntryResponseList: MutableList<LinkEntryResponse> = mutableListOf()
        val linkEntryService = LinkEntryService()

        runBlocking {
            user.shortLinksId.forEach {
                val linkEntryResponse = linkEntryService.getLinkEntryByShortLinkById(it)
                linkEntryResponseList.add(linkEntryResponse)
            }
        }

        return UserResponse(
            id = user.id.toString(),
            username = user.username,
            email = user.email,
            phone = user.phone,
            linkEntryList = linkEntryResponseList,
            createdAt = user.createdAt,
        )
    }

    // Transforms the user creation request into USER
    fun user(userCreateRequest: UserCreateRequest, accountType: AccountType = AccountType.USER): User {
        val hashedPassword = BCrypt.hashpw(userCreateRequest.password, BCrypt.gensalt())
        return User(
            username = userCreateRequest.username,
            email = userCreateRequest.email,
            phone = userCreateRequest.phone,
            password = hashedPassword,
            type = accountType.name
        )
    }
}