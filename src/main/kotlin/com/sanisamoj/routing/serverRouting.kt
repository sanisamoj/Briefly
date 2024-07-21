package com.sanisamoj.routing

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.SystemClicksCountResponse
import com.sanisamoj.data.models.dataclass.VersionResponse
import com.sanisamoj.services.server.ServerService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.serverRouting() {
    get("/version") {
        val versionResponse: VersionResponse = ServerService().getVersion()
        return@get call.respond(versionResponse)
    }

    get("/terms") {
        val redirectLink: String = GlobalContext.TERMS_OF_SERVICE_LINK
        return@get call.respondRedirect(redirectLink, permanent = false)
    }

    authenticate("moderator-jwt") {
        get("/clicks") {
            val systemClicksCountResponse: SystemClicksCountResponse = ServerService().getClickInSystemCount()
            return@get call.respond(systemClicksCountResponse)
        }
    }
}