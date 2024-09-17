package com.sanisamoj.data.repository

import com.sanisamoj.api.bot.BotApiService
import com.sanisamoj.data.models.dataclass.LoginRequest
import com.sanisamoj.data.models.dataclass.MessageToSend
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.enums.EventSeverity
import com.sanisamoj.data.models.enums.EventType
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
    private lateinit var token: String
    private val botId: String by lazy { dotEnv("BOT_ID") }

    override suspend fun updateToken() {
        try {
            val loginRequest = LoginRequest(email, password)
            token = botApiService.login(loginRequest).token

            Logger.register(
                log = LogFactory.log(
                    message = Errors.BotTokenNotUpdated.description,
                    eventType = EventType.INFO,
                    severity = EventSeverity.LOW,
                    additionalData = mapOf("at" to "${LocalDateTime.now()}")
                )
            )
        } catch (_: Throwable) {
            Logger.register(
                log = LogFactory.log(
                    message = "Bot token not updated! Retry in 1 minute!",
                    eventType = EventType.ERROR,
                    severity = EventSeverity.MEDIUM,
                    additionalData = mapOf("at" to "${LocalDateTime.now()}")
                )
            )
            delay(TimeUnit.SECONDS.toMillis(60))
            updateToken()
        }
    }

    override suspend fun sendMessage(messageToSend: MessageToSend) {
        try {
            botApiService.sendMessage(botId, messageToSend, "Bearer $token")
        } catch (cause: Throwable) {
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