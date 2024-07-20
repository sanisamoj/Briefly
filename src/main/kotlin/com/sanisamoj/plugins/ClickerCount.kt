package com.sanisamoj.plugins

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*

fun Application.clickerCount(
    databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository()
) {
    intercept(ApplicationCallPipeline.Monitoring) {
        val ip: String = call.request.origin.remoteHost
        val route: String = call.request.uri
        databaseRepository.applicationClicksInc(ip, route)
    }
}
