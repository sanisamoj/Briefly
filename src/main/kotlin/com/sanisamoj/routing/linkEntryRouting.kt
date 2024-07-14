package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.errors.errorResponse
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.services.linkEntry.QrCode
import com.sanisamoj.utils.generators.parseUserAgent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.*
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

    // Responsible for redirect user or redirect homepage
    get("/{shortLink}") {
        val shortLink: String? = call.parameters["shortLink"]

        val ip: String = call.request.origin.remoteHost
        val userAgent: String = call.request.headers["User-Agent"] ?: "Unknown"
        val userAgentInfo: UserAgentInfo = parseUserAgent(userAgent)

        // Redirect to homepage
        if(shortLink == null) {
            return@get call.respond(HttpStatusCode.BadRequest)

        } else {
            val redirectInfo = RedirectInfo(ip, shortLink, userAgentInfo)

            try {
                val redirectLink: String = LinkEntryService().redirectLink(redirectInfo)
                return@get call.respondRedirect(redirectLink, permanent = false)

            } catch (e: Throwable) {
                val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                return@get call.respond(response.first, message = response.second)
            }
        }
    }

    // Responsible for return qrcode
    get("/qrcode") {
        val shortLink: String = call.request.queryParameters["code"].toString()

        try {
            val redirectLink: LinkEntryResponse = LinkEntryService().getLinkEntryByShortLink(shortLink)
            val qrCode = QrCode.generate(redirectLink.originalLink, 200, 200)
            call.respondBytes(qrCode, ContentType.Image.PNG)

        } catch (e: Throwable) {
            val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
            return@get call.respond(response.first, message = response.second)
        }

    }
}