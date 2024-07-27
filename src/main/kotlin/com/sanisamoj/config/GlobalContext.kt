package com.sanisamoj.config

import com.sanisamoj.data.models.interfaces.*
import com.sanisamoj.utils.analyzers.dotEnv
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object GlobalContext {
    const val VERSION: String = "0.15.0"
    private var mobileMinVersion: String = "0.1.0"
    private var mobileTargetVersion: String = "1.0.0"
    private val serverContainer: ServerContainer = DefaultServerContainer()

    val TERMS_OF_SERVICE_LINK: String = dotEnv("TERMS_OF_SERVICE_LINK")

    val USER_TOKEN_EXPIRATION: Long = TimeUnit.DAYS.toMillis(15)
    val EMAIL_TOKEN_EXPIRATION: Long = TimeUnit.MINUTES.toMillis(5)
    val MODERATOR_TOKEN_EXPIRATION: Long = TimeUnit.DAYS.toMillis(1)

    val BLOCKED_IPS_TIME_TO_LIVE: Long = TimeUnit.HOURS.toMillis(1)
    const val NO_EXPIRATION_TIME = "No expiration"

    var LINK_ENTRY_EXPIRES_IN: LocalDateTime = LocalDateTime.now().plusDays(365)
        private set

    fun getMobileMinVersion(): String { return mobileMinVersion }
    fun getMobileTargetVersion(): String { return mobileTargetVersion }

    fun setMinMobileVersion(version: String) { mobileMinVersion = version }
    fun setTargetMobileVersion(version: String) { mobileTargetVersion = version }

    fun getDatabaseRepository(): DatabaseRepository { return serverContainer.databaseRepository }
    fun getSessionRepository(): SessionRepository { return serverContainer.sessionRepository }
    fun getIpRepository(): IpRepository { return serverContainer.ipRepository }
    fun getMailRepository(): MailRepository { return serverContainer.mailRepository }
}