package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.*
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

                val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(updatedLinkEntryRequest)
                return@post call.respond(HttpStatusCode.Created, linkEntryResponse)
            }

            // Responsible for return linkEntry
            get {
                val shortLink: String = call.request.queryParameters["short"].toString()
                val principal = call.principal<JWTPrincipal>()!!
                val accountId = principal.payload.getClaim("id").asString()

                val linkEntryResponse = LinkEntryService().getLinkEntryByShortLinkWithUserId(accountId, shortLink)
                return@get call.respond(linkEntryResponse)
            }
        }
    }

    rateLimit(RateLimitName("lightweight")) {

        // Responsible for redirect user or redirect homepage
        get("/{shortLink?}") {
            val shortLink: String? = call.parameters["shortLink"]

            val ip: String = call.request.origin.remoteHost
            val userAgent: String = call.request.headers["User-Agent"] ?: "Unknown"
            val userAgentInfo: UserAgentInfo = parseUserAgent(userAgent)

            // Redirect to homepage
            if(shortLink == null || shortLink == "favicon.ico") {
                return@get call.respond(HttpStatusCode.OK)

            } else {
                val redirectInfo = RedirectInfo(ip, shortLink, userAgentInfo)
                val redirectLink: String = LinkEntryService().redirectLink(redirectInfo)
                return@get call.respondRedirect(redirectLink, permanent = false)
            }
        }

        // Responsible for returning information from a link
        get("/info") {
            val shortLink = call.request.queryParameters["short"].toString()
            val linkEntryResponse: MidLinkEntryResponse = LinkEntryService().getPublicLinkEntryInfoByShortLink(shortLink)
            return@get call.respond(linkEntryResponse)
        }

        // Responsible for return qrcode
        get("/qrcode") {
            val shortLink: String = call.request.queryParameters["short"].toString()
            val redirectLink: LinkEntryResponse = LinkEntryService().getLinkEntryByShortLink(shortLink)
            val qrCode = QrCode.generate(redirectLink.originalLink, 200, 200)
            call.respondBytes(qrCode, ContentType.Image.PNG)
        }
    }

    rateLimit(RateLimitName("publicLinkEntry")) {

        // Responsible for generating a public shortened link
        post("/generate") {
            val originalLink = call.request.queryParameters["link"].toString()
            val ip: String = call.request.origin.remoteHost

            val linkEntryRequest = LinkEntryRequest(userId = ip, link = originalLink)
            val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(linkEntryRequest, public = true)

            val midLinkEntryResponse = MidLinkEntryResponse(
                active = linkEntryResponse.active,
                shortLink = linkEntryResponse.shortLink,
                qrCodeLink = linkEntryResponse.qrCodeLink,
                originalLink = linkEntryResponse.originalLink,
                expiresAt = linkEntryResponse.expiresAt
            )
            return@post call.respond(midLinkEntryResponse)
        }
    }
}