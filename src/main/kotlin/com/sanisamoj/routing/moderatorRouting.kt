package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.linkEntry.LinkEntryManager
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.services.moderator.ModeratorManagerService
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

                    try {
                        LinkEntryManager().deleteShortLink(shortLink)
                        return@delete call.respond(HttpStatusCode.OK)

                    } catch (e: Throwable) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@delete call.respond(response.first, message = response.second)
                    }
                }

                // Responsible for return a shortLink
                get("link") {
                    val shortLink: String = call.parameters["short"].toString()

                    try {
                        val linkEntryResponse: LinkEntryResponse = LinkEntryService().getLinkEntryByShortLink(shortLink)
                        return@get call.respond(linkEntryResponse)

                    } catch (e: Throwable) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@get call.respond(response.first, message = response.second)
                    }
                }

                // Responsible for changing the status of the user's account
                put("/block") {
                    val userId: String = call.parameters["id"].toString()

                    try {
                        ModeratorManagerService().blockUser(userId)
                        return@put call.respond(HttpStatusCode.OK)

                    } catch (e: Throwable) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@put call.respond(response.first, message = response.second)
                    }
                }

                // Responsible for return all user with pagination
                get("/users") {
                    val page: String = call.parameters["page"].toString()
                    val size: String = call.parameters["size"].toString()

                    try {
                        val usersResponse: UsersWithPaginationResponse =
                            ModeratorManagerService().getAllUsersWithPagination(page.toInt(), size.toInt())
                        return@get call.respond(usersResponse)

                    } catch (e: Throwable) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@get call.respond(response.first, message = response.second)
                    }
                }

                // Responsible for return user by ID
                get("/user") {
                    val userId = call.parameters["id"]
                    val email = call.parameters["email"]

                    try {
                        val usersResponse: UserResponse = when {
                            userId != null -> ModeratorManagerService().getUserById(userId)
                            email != null -> ModeratorManagerService().getUserByEmail(email)
                            else -> throw IllegalArgumentException("Either 'id' or 'email' must be provided")
                        }
                        call.respond(usersResponse)

                    } catch (e: Throwable) {
                        val (status, errorResponse) = errorResponse(e.message ?: "An unknown error occurred")
                        call.respond(status, errorResponse)
                    }
                }

                // Responsible for creating moderator account
                post {
                    val user = call.receive<UserCreateRequest>()

                    try {
                        val userResponse: UserResponse = UserService().createUser(user, AccountType.MODERATOR)
                        return@post call.respond(userResponse)

                    } catch (e: Exception) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@post call.respond(response.first, message = response.second)
                    }
                }
            }
        }
    }
}
