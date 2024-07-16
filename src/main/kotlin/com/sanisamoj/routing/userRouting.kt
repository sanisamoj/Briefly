package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.pages.confirmationPage
import com.sanisamoj.data.pages.tokenExpiredPage
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.linkEntry.LinkEntryManager
import com.sanisamoj.services.user.UserAuthenticationService
import com.sanisamoj.services.user.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

fun Route.userRouting() {
    route("/user") {

        rateLimit(RateLimitName("register")) {

            // Route responsible for creating a user
            post {
                val user = call.receive<UserCreateRequest>()

                try {
                    val userResponse: UserResponse = UserService().createUser(user)
                    return@post call.respond(userResponse)

                } catch (e: Exception) {
                    val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                    return@post call.respond(response.first, message = response.second)
                }
            }
        }

        rateLimit(RateLimitName("lightweight")) {

            authenticate("user-jwt") {

                // Responsible for deleting a user's LinkEntry
                delete("/link") {
                    val shortLink = call.parameters["short"].toString()
                    val principal: JWTPrincipal = call.principal<JWTPrincipal>()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    try {
                        LinkEntryManager().deleteShortLinkFromUser(userId, shortLink)
                        return@delete call.respond(HttpStatusCode.OK)

                    } catch (e: Exception) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@delete call.respond(response.first, message = response.second)
                    }
                }

                put("/link") {
                    val shortLink: String = call.parameters["short"].toString()
                    val active: Boolean = call.parameters["active"].toString() == "true"
                    val principal: JWTPrincipal = call.principal<JWTPrincipal>()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    try {
                        LinkEntryManager().updateLinkEntryStatusFromUser(userId, shortLink, active)
                        return@put call.respond(HttpStatusCode.OK)

                    } catch (e: Exception) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@put call.respond(response.first, message = response.second)
                    }
                }

            }
        }
    }

    route("/authentication") {

        rateLimit(RateLimitName("validation")) {

            // Responsible for generate email token
            post("/generate") {
                val identification = call.request.queryParameters["identifier"].toString()

                try {
                    UserAuthenticationService().generateValidationEmailToken(identification)
                    return@post call.respond(HttpStatusCode.OK)

                } catch (e: Exception) {
                    val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                    return@post call.respond(response.first, message = response.second)
                }
            }
        }

        rateLimit(RateLimitName("login")) {

            // Responsible for login
            post("/login") {
                val loginRequest = call.receive<LoginRequest>()

                try {
                    val userResponse: LoginResponse = UserAuthenticationService().login(loginRequest)
                    return@post call.respond(userResponse)

                } catch (e: Exception) {
                    val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                    return@post call.respond(response.first, message = response.second)
                }
            }
        }

        // Responsible for activate account by token email
        get("/activate") {
            val token = call.parameters["token"].toString()

            try {
                UserAuthenticationService().activateAccountByToken(token)
                call.respondText(buildString {
                    appendHTML().html { confirmationPage() }
                }, ContentType.Text.Html)

            } catch (e: Throwable) {
                call.respondText(buildString {
                    appendHTML().html { tokenExpiredPage() }
                }, ContentType.Text.Html)
            }
        }

        rateLimit(RateLimitName("lightweight")) {

            authenticate("user-jwt") {

                // Responsible for session
                post("/session") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val accountId = principal.payload.getClaim("id").asString()

                    try {
                        val userResponse: UserResponse = UserAuthenticationService().session(accountId)
                        return@post call.respond(userResponse)

                    } catch (e: Exception) {
                        val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                        return@post call.respond(response.first, message = response.second)
                    }
                }

                // Responsible for sign out
                delete("/session") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val accountId = principal.payload.getClaim("id").asString()
                    val sessionId = principal.payload.getClaim("session").asString()

                    try {
                        UserAuthenticationService().signOut(accountId, sessionId)
                        return@delete call.respond(HttpStatusCode.OK)
                    } catch (e: Exception) {
                        val response = errorResponse(e.message!!)
                        return@delete call.respond(response.first, message = response.second)
                    }
                }
            }
        }
    }
}