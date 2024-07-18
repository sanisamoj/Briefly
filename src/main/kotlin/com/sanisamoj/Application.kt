package com.sanisamoj

import com.sanisamoj.config.Config
import com.sanisamoj.config.GlobalContext
import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.database.mongodb.MongoDatabase
import com.sanisamoj.database.redis.Redis
import com.sanisamoj.plugins.*
import com.sanisamoj.utils.analyzers.dotEnv
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    rateLimit()
    statusPage()
    configureHTTP()
    configureSerialization()
    configureSockets(WebSocketManager)
    configureRouting(initialPage = true)
    clickersCount(GlobalContext.getDatabaseRepository())
    startBackgroundTasks()
}

private fun startBackgroundTasks() {
    CoroutineScope(Dispatchers.Default).launch {
        Config.databaseInitialize()
        Config.updateExpiredLinksRoutine()
    }
}
