package com.sanisamoj.config

import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer
import com.sanisamoj.data.models.interfaces.SessionRepository
import java.util.concurrent.TimeUnit

object GlobalContext {
    val version: String = "1.0.0"
    private var mobileMinVersion: String = "1.0.0"
    private var mobileTargetVersion: String = "1.0.0"
    private val serverContainer: ServerContainer = DefaultServerContainer()

    val USER_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(15)
    val EMAIL_TOKEN_EXPIRATION = TimeUnit.MINUTES.toMillis(5)
    val MODERATOR_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(1)

    fun getMobileMinVersion(): String { return mobileMinVersion }
    fun getMobileTargetVersion(): String { return mobileTargetVersion }

    // Validate so that a value lower than the current version is not assigned
    fun setMobileMinVersion(mobileMinVersion: String) { TODO() }
    fun setMobileTargetVersion(mobileTargetVersion: String) { TODO() }

    fun getDatabaseRepository(): DatabaseRepository { return serverContainer.databaseRepository }
    fun getSessionRepository(): SessionRepository { return serverContainer.sessionRepository }
    fun getMailRepository(): MailRepository { return serverContainer.mailRepository }
}