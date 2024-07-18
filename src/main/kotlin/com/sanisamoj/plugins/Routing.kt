package com.sanisamoj.plugins

import com.sanisamoj.routing.linkEntryRouting
import com.sanisamoj.routing.moderatorRouting
import com.sanisamoj.routing.userRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureRouting(initialPage: Boolean = false) {
    routing {
        userRouting()
        moderatorRouting()
        linkEntryRouting()

        if(initialPage) {
            staticResources("/", "static") {
                default("index.html")
                preCompressed(CompressedFileType.GZIP)
            }
        }
    }
}
