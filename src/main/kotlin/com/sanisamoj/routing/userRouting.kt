package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.UserCreateRequest
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.user.UserService
import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRouting() {
    route("/user") {

        rateLimit(RateLimitName("register")) {
            // Route responsible for creating a user
            post {
                val user = call.receive<UserCreateRequest>()

                try {
                    val userResponse = UserService().createUser(user)
                    return@post call.respond(userResponse)

                } catch (e: Exception) {
                    val response = errorResponse(e.message!!)
                    return@post call.respond(response.first, message = response.second)
                }
            }
        }
    }
}