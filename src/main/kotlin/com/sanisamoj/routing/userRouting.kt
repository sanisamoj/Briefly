package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext.ACTIVATED_ACCOUNT_LINK_ROUTE
import com.sanisamoj.config.GlobalContext.EXPIRED_TOKEN_EMAIL_LINK_ROUTE
import com.sanisamoj.config.GlobalContext.MAX_HEADERS_SIZE
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.AccountType
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.linkEntry.LinkEntryManager
import com.sanisamoj.services.media.MediaService
import com.sanisamoj.services.user.UserAuthenticationService
import com.sanisamoj.services.user.UserManagerService
import com.sanisamoj.services.user.UserService
import com.sanisamoj.utils.converters.BytesConverter
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/user") {

        rateLimit(RateLimitName("register")) {

            // Route responsible for creating a user
            post {
                val user: UserCreateRequest = call.receive<UserCreateRequest>()
                val userResponse: UserResponse = UserService().createUser(user)
                return@post call.respond(userResponse)
            }
        }

        rateLimit(RateLimitName("lightweight")) {

            authenticate("user-jwt") {

                // Responsible for deleting a user's LinkEntry
                delete("/link") {
                    val shortLink = call.parameters["short"].toString()
                    val principal: JWTPrincipal = call.principal<JWTPrincipal>()!!
                    val userId: String = principal.payload.getClaim("id").asString()
                    LinkEntryManager().deleteShortLinkFromUser(userId, shortLink)
                    return@delete call.respond(HttpStatusCode.OK)
                }

                // Responsible for updating a link
                put("/link") {
                    val shortLink: String = call.parameters["short"].toString()
                    val active: Boolean = call.parameters["active"].toString() == "true"
                    val principal: JWTPrincipal = call.principal<JWTPrincipal>()!!
                    val userId: String = principal.payload.getClaim("id").asString()
                    LinkEntryManager().updateLinkEntryStatusFromUser(userId, shortLink, active)
                    return@put call.respond(HttpStatusCode.OK)
                }
            }
        }

        rateLimit(RateLimitName("validation")) {

            authenticate("user-jwt") {

                // Responsible for updating user profile
                put("/profile") {
                    val putUserProfile: PutUserProfile = call.receive()
                    val principal: JWTPrincipal = call.principal()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    val userManagerService = UserManagerService()

                    when {
                        putUserProfile.name != null -> {
                            val userResponse = userManagerService.updateName(userId, putUserProfile.name)
                            return@put call.respond(HttpStatusCode.OK, userResponse)
                        }
                        putUserProfile.phone != null -> {
                            userManagerService.updatePhone(userId, putUserProfile.phone)
                            return@put call.respond(HttpStatusCode.OK)
                        }
                        else -> {
                            return@put call.respond(HttpStatusCode.BadRequest, "No valid data to update")
                        }
                    }
                }

                // Confirm and update phone
                post("/phone") {
                    val updatePhoneWithValidationCode: UpdatePhoneWithValidationCode = call.receive<UpdatePhoneWithValidationCode>()
                    val principal: JWTPrincipal = call.principal()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    UserManagerService().validateValidationCodeToUpdatePhone(
                        userId = userId,
                        newPhone = updatePhoneWithValidationCode.phone,
                        validationCode = updatePhoneWithValidationCode.validationCode
                    )

                    return@post call.respond(HttpStatusCode.OK)
                }

                // Responsible for emit update password actions
                put("/password") {
                    val principal: JWTPrincipal = call.principal()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    UserManagerService().updatePassword(userId)
                    return@put call.respond(HttpStatusCode.OK)
                }

                // Responsible for confirm validation code and update password
                post("password") {
                    val putUserProfile: PutUserProfile = call.receive()
                    val principal: JWTPrincipal = call.principal()!!
                    val userId: String = principal.payload.getClaim("id").asString()

                    UserManagerService().validateValidationCodeToUpdatePassword(userId, putUserProfile.password!!, putUserProfile.validationCode!!)
                    return@post call.respond(HttpStatusCode.OK)
                }

                //  Responsible for account removal
                delete {
                    val reportingRequest: ReportingRequest = call.receive<ReportingRequest>()
                    val principal: JWTPrincipal = call.principal()!!
                    val userId: String = principal.payload.getClaim("id").asString()
                    UserService().emitRemoveAccount(userId, reportingRequest)
                    return@delete call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

    route("/moderator") {
        rateLimit(RateLimitName("register")) {

            // Route responsible for creating a user
            post {
                val user = call.receive<UserCreateRequest>()
                val userResponse: UserResponse = UserService().createUser(user, AccountType.MODERATOR)
                return@post call.respond(userResponse)
            }
        }
    }

    route("/authentication") {

        rateLimit(RateLimitName("validation")) {

            // Responsible for generate email token
            post("/generate") {
                val identification = call.request.queryParameters["identifier"].toString()
                UserAuthenticationService().generateValidationEmailToken(identification)
                return@post call.respond(HttpStatusCode.OK)
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
            val token: String = call.parameters["token"].toString()
            val email: String = call.parameters["email"].toString()

            try {
                UserAuthenticationService().activateAccountByToken(token)
                return@get call.respondRedirect(ACTIVATED_ACCOUNT_LINK_ROUTE, permanent = false)

            } catch (_: Throwable) {
                return@get call.respondRedirect("$EXPIRED_TOKEN_EMAIL_LINK_ROUTE$email", permanent = false)
            }
        }

        rateLimit(RateLimitName("lightweight")) {

            authenticate("user-jwt", "moderator-jwt") {

                // Responsible for session
                post("/session") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val accountId = principal.payload.getClaim("id").asString()
                    val userResponse: UserResponse = UserAuthenticationService().session(accountId)
                    return@post call.respond(userResponse)
                }

                // Responsible for sign out
                delete("/session") {
                    val principal = call.principal<JWTPrincipal>()!!
                    val accountId = principal.payload.getClaim("id").asString()
                    val sessionId = principal.payload.getClaim("session").asString()
                    UserAuthenticationService().signOut(accountId, sessionId)
                    return@delete call.respond(HttpStatusCode.OK)
                }
            }
        }
    }

    route("/media") {

        // Responsible for returning an image
        get("/{name?}") {
            val mediaName: String = call.parameters["name"].toString()
            val file = MediaService().getImage(mediaName)
            println(file)
            if (file.exists()) return@get call.respondFile(file)
            else return@get call.respond(HttpStatusCode.NotFound)
        }

        authenticate("user-jwt") {

            // Responsible for saving an image to the server
            post {
                val principal = call.principal<JWTPrincipal>()!!
                val accountId = principal.payload.getClaim("id").asString()
                val multipartData = call.receiveMultipart()
                val requestSize = call.request.headers[HttpHeaders.ContentLength]
                val requestSizeInMb = BytesConverter(requestSize!!.toLong()).getInMegabyte()

                if (requestSizeInMb > MAX_HEADERS_SIZE) throw Exception("totalImageUploadSizeExceeded")
                val response = UserManagerService().updateImageProfile(accountId, multipartData)
                return@post call.respond(response)
            }

            // Responsible for deleting an image
            delete {
                val principal = call.principal<JWTPrincipal>()!!
                val accountId = principal.payload.getClaim("id").asString()

                UserManagerService().deleteImageProfile(accountId)
                return@delete call.respond(HttpStatusCode.OK)
            }

        }
    }
}