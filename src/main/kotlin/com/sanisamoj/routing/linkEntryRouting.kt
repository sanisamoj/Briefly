package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext.INACTIVE_LINK_PAGE_ROUTE
import com.sanisamoj.config.GlobalContext.NOT_FOUND_PAGE_ROUTE
import com.sanisamoj.config.GlobalContext.PROTECTED_LINK_ROUTE
import com.sanisamoj.config.GlobalContext.SELF_URL
import com.sanisamoj.config.GlobalContext.UNKNOWN
import com.sanisamoj.data.models.dataclass.*
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.services.linkEntry.LinkEntryFactory
import com.sanisamoj.services.linkEntry.LinkEntryService
import com.sanisamoj.services.linkEntry.QrCode
import com.sanisamoj.services.server.ServerService
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

            // Responsible for return a unique linkEntry from the user or list with pagination
            get {
                // Retrieve query parameters from the request
                val shortLink: String? = call.request.queryParameters["short"]
                val page: String? = call.request.queryParameters["page"]
                val size: String? = call.request.queryParameters["size"]

                val principal = call.principal<JWTPrincipal>() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val accountId = principal.payload.getClaim("id").asString()

                // Check if pagination parameters are provided
                if (page != null && size != null) {
                    val pageNumber = page.toIntOrNull()
                    val pageSize = size.toIntOrNull()

                    if (pageNumber != null && pageSize != null) {
                        val linkEntryResponse = LinkEntryService().getAllLinkEntryFromTheUserWithPagination(accountId, pageNumber, pageSize)
                        call.respond(linkEntryResponse)
                    } else {
                        throw Error(Errors.InvalidPageOrSizeParameters.description)
                    }
                } else if (shortLink != null) {
                    val linkEntryResponse = LinkEntryService().getLinkEntryByShortLinkWithUserId(accountId, shortLink)
                    call.respond(linkEntryResponse)

                } else {
                    throw Error(Errors.ShortLinkOrPaginationRequired .description)
                }
            }
        }
    }

    rateLimit(RateLimitName("lightweight")) {

        // Responsible for redirect user or redirect homepage
        get("/{shortLink?}") {
            val shortLink: String? = call.parameters["shortLink"]
            val ip: String = call.request.headers["X-Forwarded-For"]?.split(",")?.firstOrNull()?.trim().toString()
            val userAgent: String = call.request.headers["User-Agent"] ?: UNKNOWN
            val referer: String? = call.request.headers["Referer"]
            val userAgentInfo: UserAgentInfo = parseUserAgent(userAgent)

            // Redirect to homepage
            try {
                val redirectInfo = RedirectInfo(ip, shortLink.toString(), userAgentInfo, referer.toString())
                val redirectLink: String = LinkEntryService().redirectLink(redirectInfo)
                ServerService().incrementAccess(ip, "/${shortLink}")
                return@get call.respondRedirect(redirectLink, permanent = false)

            } catch (e: Throwable) {
                ServerService().incrementAccess(ip, "/")
                val redirectionLink = when(e.message) {
                    Errors.LinkIsNotActive.description -> INACTIVE_LINK_PAGE_ROUTE
                    Errors.ProtectedLink.description -> PROTECTED_LINK_ROUTE + shortLink.toString()
                    else -> NOT_FOUND_PAGE_ROUTE
                }

                return@get call.respondRedirect(redirectionLink, permanent = false)
            }
        }

        // Responsible for redirect user or redirect homepage
        post("/protected") {
            val protectedLinkEntryPass: ProtectedLinkEntryPass = call.receive<ProtectedLinkEntryPass>()
            val ip: String = call.request.origin.remoteHost
            val userAgent: String = call.request.headers["User-Agent"] ?: UNKNOWN
            val referer: String? = call.request.headers["Referer"]
            val userAgentInfo: UserAgentInfo = parseUserAgent(userAgent)

            // Redirect to homepage
            val redirectInfo = RedirectInfo(ip, protectedLinkEntryPass.shortLink, userAgentInfo, referer.toString())
            val redirectLink: String = LinkEntryService().redirectLink(redirectInfo, protectedLinkEntryPass)
            val privateLinkPassResponse = PrivateLinkPassResponse(redirectLink)
            return@post call.respond(privateLinkPassResponse)
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
            val httpShortLink = "${SELF_URL}/${redirectLink.shortLink.substringAfterLast("/")}"
            val qrCode = QrCode.generate(httpShortLink, 200, 200)
            call.respondBytes(qrCode, ContentType.Image.PNG)
        }
    }

    rateLimit(RateLimitName("publicLinkEntry")) {

        // Responsible for generating a public shortened link
        post("/generate") {
            val linkEntryRequestFromUser: LinkEntryRequest = call.receive<LinkEntryRequest>()
            val ip: String = call.request.origin.remoteHost

            val linkEntryRequest: LinkEntryRequest = linkEntryRequestFromUser.copy(userId = ip, active = true)
            val linkEntryResponse: LinkEntryResponse = LinkEntryService().register(linkEntryRequest, public = true)
            return@post call.respond(LinkEntryFactory.linkEntryResponseToMidLinkEntryResponse(linkEntryResponse))
        }
    }
}