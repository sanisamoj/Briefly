package com.sanisamoj.plugins

import com.sanisamoj.routing.linkEntryRouting
import com.sanisamoj.routing.moderatorRouting
import com.sanisamoj.routing.serverRouting
import com.sanisamoj.routing.userRouting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.routing.*
import io.ktor.http.content.*

fun Application.configureRouting() {
    install(CachingHeaders) {
        options { call, content ->
            when (content.contentType?.withoutParameters()) {
                ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (60 * 60)))
                else -> null
            }
        }
    }

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
