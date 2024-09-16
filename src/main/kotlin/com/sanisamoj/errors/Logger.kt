package com.sanisamoj.errors

import com.sanisamoj.api.log.LogApi
import com.sanisamoj.api.log.LogApiService
import com.sanisamoj.data.models.dataclass.ApplicationServiceLoginRequest
import com.sanisamoj.data.models.dataclass.Log
import com.sanisamoj.utils.analyzers.dotEnv
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

object Logger {
    private val repository: LogApiService by lazy { LogApi.retrofitLogService }
    private val applicationName: String = dotEnv("APP_LOG_NAME")
    private val password: String = dotEnv("APP_LOG_PASSWORD")
    private lateinit var token: String

    suspend fun updateToken() {
        try {
            token = repository.applicationLogin(ApplicationServiceLoginRequest(applicationName, password)).token
            println("Log token updated!")
        } catch (_: Throwable) {
            delay(TimeUnit.SECONDS.toMillis(60))
            updateToken()
        }
    }

    suspend fun register(log: Log) {
        try {
            repository.registerLog(log, "Bearer $token")
        } catch (e: Throwable) {
            println(e)
        }
    }
}