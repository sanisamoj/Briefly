package com.sanisamoj.plugins

import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.security.AccessGuardianService
import io.ktor.server.application.*
import io.ktor.server.plugins.*

fun Application.accessGuardian() {
    intercept(
        ApplicationCallPipeline.Call
    ) {
        val ip: String = call.request.origin.remoteHost
        if(AccessGuardianService.isIpBlocked(ip)) { throw Exception(Errors.AccessProhibited.description) }
    }
}
