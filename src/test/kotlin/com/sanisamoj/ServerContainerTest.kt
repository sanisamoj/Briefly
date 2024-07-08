package com.sanisamoj

import com.sanisamoj.data.MailRepositoryTest
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer
import com.sanisamoj.data.models.interfaces.SessionRepository
import com.sanisamoj.data.repository.DefaultDatabaseRepository
import com.sanisamoj.data.repository.DefaultSessionRepository

class ServerContainerTest : ServerContainer {
    override val databaseRepository: DatabaseRepository by lazy { DefaultDatabaseRepository() }
    override val sessionRepository: SessionRepository by lazy { DefaultSessionRepository() }
    override val mailRepository: MailRepository by lazy { MailRepositoryTest() }
}