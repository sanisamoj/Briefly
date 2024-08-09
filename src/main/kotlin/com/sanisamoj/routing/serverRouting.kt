package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.ReportingRequest
import com.sanisamoj.data.models.dataclass.SystemClicksCountResponse
import com.sanisamoj.data.models.dataclass.VersionResponse
import com.sanisamoj.services.server.ServerService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.serverRouting() {

    rateLimit(RateLimitName("lightweight")) {

        // Responsible for returning system version
        get("/version") {
            val versionResponse: VersionResponse = ServerService().getVersion()
            return@get call.respond(versionResponse)
        }

        // Responsible for returning terms of service
        get("/terms") {
            val redirectLink: String = GlobalContext.TERMS_OF_SERVICE_LINK
            return@get call.respondRedirect(redirectLink, permanent = false)
        }

        // Responsible for reporting
        post("/reporting") {
            val reportingRequest: ReportingRequest = call.receive<ReportingRequest>()
            ServerService().report(reportingRequest)
            call.respond(HttpStatusCode.OK)
        }

        authenticate("moderator-jwt") {

            // Responsible for returning the system's click count
            get("/clicks") {
                val systemClicksCountResponse: SystemClicksCountResponse = ServerService().getClickInSystemCount()
                return@get call.respond(systemClicksCountResponse)
            }

            // Responsible for changing the path of terms
            put("/terms") {
                val link: String = call.parameters["link"].toString()
                GlobalContext.updateTermsLink(link)
                return@put call.respond(HttpStatusCode.OK)
            }

            // Responsible for update mobile version
            put("/version") {
                val min: String? = call.parameters["min"]
                val target: String? = call.parameters["target"]
                val serverService = ServerService()

                // Update both versions if both parameters are present
                if (min != null && target != null) {
                    serverService.updateMinMobileVersion(min)
                    serverService.updateTargetMobileVersion(target)
                } else {
                    // Update only the min version if the target is not present
                    if (min != null) {
                        serverService.updateMinMobileVersion(min)
                    }
                    // Update only the target version if the min is not present
                    if (target != null) {
                        serverService.updateTargetMobileVersion(target)
                    }
                }

                return@put call.respond(serverService.getVersion())
            }
        }

    }
}