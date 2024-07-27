package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.data.models.dataclass.UserResponse
import com.sanisamoj.data.models.dataclass.UsersWithPaginationResponse
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.services.linkEntry.LinkEntryManager
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.services.moderator.ModeratorManagerService
import com.sanisamoj.services.server.ServerService
import com.sanisamoj.services.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.moderatorRouting() {
    route("/moderator") {

        rateLimit(RateLimitName("lightweight")) {

            authenticate("moderator-jwt") {

                // Responsible for deleting a link
                delete("/link") {
                    val shortLink: String = call.parameters["short"].toString()
                    LinkEntryManager().deleteShortLink(shortLink)
                    return@delete call.respond(HttpStatusCode.OK)
                }

                // Responsible for return a shortLink
                get("/link") {
                    val shortLink: String = call.parameters["short"].toString()
                    val linkEntryResponse: LinkEntryResponse = LinkEntryService().getLinkEntryByShortLink(shortLink)
                    return@get call.respond(linkEntryResponse)
                }

                // Responsible for changing the status of the user's account
                put("/block") {
                    val userId: String = call.parameters["id"].toString()
                    ModeratorManagerService().blockUser(userId)
                    return@put call.respond(HttpStatusCode.OK)
                }

                // Responsible for return all user with pagination
                get("/users") {
                    val page: String = call.parameters["page"].toString()
                    val size: String = call.parameters["size"].toString()
                    val usersResponse: UsersWithPaginationResponse =
                        ModeratorManagerService().getAllUsersWithPagination(page.toInt(), size.toInt())
                    return@get call.respond(usersResponse)
                }

                // Responsible for return user by ID
                get("/user") {
                    val userId = call.parameters["id"]
                    val email = call.parameters["email"]
                    val usersResponse: UserResponse = when {
                        userId != null -> ModeratorManagerService().getUserById(userId)
                        email != null -> ModeratorManagerService().getUserByEmail(email)
                        else -> throw IllegalArgumentException("Either 'id' or 'email' must be provided")
                    }
                    call.respond(usersResponse)
                }

                // Responsible for creating moderator account
                post {
                    val user = call.receive<UserCreateRequest>()
                    val userResponse: UserResponse = UserService().createUser(user, AccountType.MODERATOR)
                    return@post call.respond(userResponse)
                }
            }
        }
    }
}
