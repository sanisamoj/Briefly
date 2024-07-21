package com.sanisamoj.config

import com.sanisamoj.data.models.interfaces.*
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object GlobalContext {
    const val VERSION: String = "0.13.1"
    private var mobileMinVersion: String = "0.1.0"
    private var mobileTargetVersion: String = "1.0.0"
    private val serverContainer: ServerContainer = DefaultServerContainer()

    val USER_TOKEN_EXPIRATION: Long = TimeUnit.DAYS.toMillis(15)
    val EMAIL_TOKEN_EXPIRATION: Long = TimeUnit.MINUTES.toMillis(5)
    val MODERATOR_TOKEN_EXPIRATION: Long = TimeUnit.DAYS.toMillis(1)

    val BLOCKED_IPS_TIME_TO_LIVE: Long = TimeUnit.HOURS.toMillis(1)

    var LINK_ENTRY_EXPIRES_IN: LocalDateTime = LocalDateTime.now().plusDays(365)
        private set

    const val UNKNOWN_USER_ID = "Unknown"
    const val MAX_SHORT_LINK_BY_ACCOUNT: Int = 25

    fun getMobileMinVersion(): String { return mobileMinVersion }
    fun getMobileTargetVersion(): String { return mobileTargetVersion }

    // Validate so that a value lower than the current version is not assigned
    fun setMinMobileVersion(version: String) { mobileMinVersion = version }
    fun setTargetMobileVersion(version: String) { mobileTargetVersion = version }

    fun getDatabaseRepository(): DatabaseRepository { return serverContainer.databaseRepository }
    fun getSessionRepository(): SessionRepository { return serverContainer.sessionRepository }
    fun getIpRepository(): IpRepository { return serverContainer.ipRepository }
    fun getMailRepository(): MailRepository { return serverContainer.mailRepository }
}