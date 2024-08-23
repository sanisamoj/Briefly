package com.sanisamoj.services.user

import com.sanisamoj.config.GlobalContext.MEDIA_ROUTE
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.services.linkEntry.LinkEntryService
import kotlinx.coroutines.runBlocking
import org.mindrot.jbcrypt.BCrypt

object UserFactory {
    fun userResponse(user: User): UserResponse {
        val linkEntryResponseList: MutableList<LinkEntryFromLoginResponse> = mutableListOf()
        val linkEntryService = LinkEntryService()

        runBlocking {
            user.shortLinksId.forEach {
                try {
                    val linkEntryResponse: LinkEntryResponse = linkEntryService.getLinkEntryByShortLinkById(it)
                    val linkEntryFromLoginResponse = LinkEntryFromLoginResponse(
                        active = linkEntryResponse.active,
                        shortLink = linkEntryResponse.shortLink,
                        originalLink = linkEntryResponse.originalLink,
                        expiresAt = linkEntryResponse.expiresAt
                    )
                    linkEntryResponseList.add(linkEntryFromLoginResponse)
                } catch (_: Throwable) {
                    // Intentionally ignored
                }
            }
        }

        return UserResponse(
            id = user.id.toString(),
            username = user.username,
            profileImageUrl = if(user.imageProfile == "") "" else MEDIA_ROUTE + user.imageProfile,
            email = user.email,
            phone = user.phone,
            linkEntryList = linkEntryResponseList,
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