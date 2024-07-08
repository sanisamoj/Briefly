package com.sanisamoj.plugins

import com.sanisamoj.data.models.dataclass.ErrorResponse
import com.sanisamoj.data.models.enums.Errors
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.statusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            // Remove "cause" in production
            val response = HttpStatusCode.InternalServerError to ErrorResponse(Errors.InternalServerError.description, "$cause")
            call.respond(response.first, response.second)
        }

        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers["Retry-After"]
            val errorResponse = ErrorResponse(Errors.TooManyRequests.description, "Wait for $retryAfter seconds.")
            call.respond(status, errorResponse)
        }
    }
}