package com.sanisamoj.data.repository

import com.sanisamoj.api.bot.BotApiService
import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.enums.EventSeverity
import com.sanisamoj.data.models.enums.EventType
import com.sanisamoj.data.models.enums.Infos
import com.sanisamoj.data.models.interfaces.BotRepository
import com.sanisamoj.errors.LogFactory
import com.sanisamoj.errors.Logger
import com.sanisamoj.utils.analyzers.dotEnv
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class DefaultBotRepository(
    private val botApiService: BotApiService
) : BotRepository {
    private val email: String by lazy { dotEnv("BOT_LOGIN_EMAIL") }
    private val password: String by lazy { dotEnv("BOT_LOGIN_PASSWORD") }
    private var token: String = ""
    private val botId: String by lazy { dotEnv("BOT_ID") }
    private val maxRetries = 3

    override suspend fun updateToken() {
        var attempts = 0
        while (attempts < maxRetries) {
            try {
                val loginRequest = LoginRequest(email, password)
                token = botApiService.login(loginRequest).token
                Logger.register(
                    log = LogFactory.log(
                        message = Infos.BotTokenUpdated.description,
                        eventType = EventType.INFO,
                        severity = EventSeverity.LOW,
                        additionalData = mapOf("at" to "${LocalDateTime.now()}")
                    )
                )
                println(Infos.BotTokenUpdated.description)
                return
            } catch (_: Throwable) {
                attempts++
                println("${Errors.BotTokenNotUpdated.description} Retry in 30 seconds! Attempt $attempts/${maxRetries}")
                Logger.register(
                    log = LogFactory.log(
                        message = "${Errors.BotTokenNotUpdated.description} Retry in 1 minute! Attempt $attempts/$maxRetries",
                        eventType = EventType.ERROR,
                        severity = EventSeverity.MEDIUM,
                        additionalData = mapOf("at" to "${LocalDateTime.now()}")
                    )
                )
                if (attempts >= maxRetries) {
                    Logger.register(
                        log = LogFactory.log(
                            message = Errors.MaxRetriesReached.description,
                            eventType = EventType.ERROR,
                            severity = EventSeverity.HIGH,
                            additionalData = mapOf("at" to "${LocalDateTime.now()}")
                        )
                    )
                    break
                }
                delay(TimeUnit.SECONDS.toMillis(60))
            }
        }
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        try {
            println(botId)
            botApiService.sendMessage(botId, messageToSend, "Bearer $token")
        } catch (cause: Throwable) {
            println(cause)
            Logger.register(
                LogFactory.throwableToLog(
                    cause = cause,
                    severity = EventSeverity.HIGH,
                    description = Errors.BotUnableToSendMessage.description
                )
            )
        }
    }
}
