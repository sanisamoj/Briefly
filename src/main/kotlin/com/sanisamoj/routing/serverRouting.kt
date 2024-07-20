package com.sanisamoj.routing

import com.sanisamoj.data.models.dataclass.VersionResponse
import com.sanisamoj.services.server.ServerService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.serverRouting() {
    get("/version") {
        val versionResponse: VersionResponse = ServerService().getVersion()
        return@get call.respond(versionResponse)
    }
}