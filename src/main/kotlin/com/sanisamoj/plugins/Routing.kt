package com.sanisamoj.plugins

import com.sanisamoj.routing.linkEntryRouting
import com.sanisamoj.routing.moderatorRouting
import com.sanisamoj.routing.userRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        userRouting()
        moderatorRouting()
        linkEntryRouting()

        staticResources("/", "static") {
            default("index.html")
            preCompressed(CompressedFileType.GZIP)
        }
    }
}
