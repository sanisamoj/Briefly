package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.ErrorResponse
import com.sanisamoj.data.models.dataclass.LinkEntryRequest
import com.sanisamoj.data.models.dataclass.LinkEntryResponse
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.linkEntry.LinkEntryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.linkEntryRouting() {

    route("/link") {

        authenticate("user-jwt") {

            // Responsible for create a LinkEntry
            post {
                val linkEntryRequest: LinkEntryRequest = call.receive<LinkEntryRequest>()
                val principal: JWTPrincipal = call.principal<JWTPrincipal>()!!
                val accountId: String = principal.payload.getClaim("id").asString()
                val updatedLinkEntryRequest: LinkEntryRequest = linkEntryRequest.copy(userId = accountId)

                try {
                    val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(updatedLinkEntryRequest)
                    return@post call.respond(HttpStatusCode.Created, linkEntryResponse)

                } catch (e: Exception) {
                    val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                    return@post call.respond(response.first, message = response.second)
                }
            }
        }
    }
}