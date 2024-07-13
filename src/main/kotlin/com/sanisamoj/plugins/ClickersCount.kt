package com.sanisamoj.plugins

import com.sanisamoj.data.models.dataclass.UserAgentInfo
import com.sanisamoj.utils.generators.parseUserAgent
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.clickersCount() {
    intercept(ApplicationCallPipeline.Monitoring) {
        val ip: String = call.request.origin.remoteHost
        val userAgent: String = call.request.headers["User-Agent"] ?: "Unknown"
        val userAgentInfo: UserAgentInfo = parseUserAgent(userAgent)
    }
}