package com.sanisamoj.plugins

import com.sanisamoj.routing.linkEntryRouting
import com.sanisamoj.routing.moderatorRouting
import com.sanisamoj.routing.serverRouting
import com.sanisamoj.routing.userRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting() {
    routing {
        userRouting()
        moderatorRouting()
        linkEntryRouting()
        serverRouting()

        staticResources("/", "files") {
            default("index.html")
            preCompressed(CompressedFileType.GZIP)
        }
    }
}
