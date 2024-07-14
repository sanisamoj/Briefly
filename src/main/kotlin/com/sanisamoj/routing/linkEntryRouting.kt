package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext.UNKNOWN_USER_ID
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
import io.ktor.server.plugins.ratelimit.*
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

            // Responsible for return linkEntry
            get {
                val shortLink: String = call.request.queryParameters["short"].toString()

                try {
                    val linkEntryResponse = LinkEntryService().getLinkEntryByShortLink(shortLink)
                    return@get call.respond(linkEntryResponse)

                } catch (e: Exception) {
                    val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                    return@get call.respond(response.first, message = response.second)
                }
            }
        }
    }

    rateLimit(RateLimitName("lightweight")) {

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

        // Responsible for generating a public shortened link
        post("/generate") {
            val originalLink = call.request.queryParameters["link"].toString()

            try {
                val linkEntryRequest = LinkEntryRequest(userId = UNKNOWN_USER_ID, link = originalLink)
                val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(linkEntryRequest)

                val midLinkEntryResponse = MidLinkEntryResponse(
                    active = linkEntryResponse.active,
                    shortLink = linkEntryResponse.shortLink,
                    qrCodeLink = linkEntryResponse.qrCodeLink,
                    originalLink = linkEntryResponse.originalLink,
                    expiresAt = linkEntryResponse.expiresAt
                )
                return@post call.respond(midLinkEntryResponse)

            } catch (e: Exception) {
                val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                return@post call.respond(response.first, message = response.second)
            }
        }

        // Responsible for returning information from a link
        get("/info") {
            val shortLink = call.request.queryParameters["link"].toString()

            try {
                val linkEntryResponse: MidLinkEntryResponse = LinkEntryService().getPublicLinkEntryByShortLink(shortLink)
                return@get call.respond(linkEntryResponse)

            } catch (e: Exception) {
                val response: Pair<HttpStatusCode, ErrorResponse> = errorResponse(e.message!!)
                return@get call.respond(response.first, message = response.second)
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
}