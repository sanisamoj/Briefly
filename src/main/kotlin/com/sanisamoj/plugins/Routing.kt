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
import java.io.File

fun Application.configureRouting() {
    routing {
        install(CachingHeaders) {
            options { call, content ->
                when (content.contentType?.withoutParameters()) {
                    ContentType.Text.Html -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = (60 * 60)))
                    else -> null
                }
            }
        }

        userRouting()
        moderatorRouting()
        linkEntryRouting()
        serverRouting()

        staticFiles("/", File("files"))
    }
}
