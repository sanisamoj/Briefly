package com.sanisamoj

import com.sanisamoj.data.MailRepositoryTest
import com.sanisamoj.data.models.interfaces.*
import com.sanisamoj.data.repository.DefaultDatabaseRepository
import com.sanisamoj.data.repository.DefaultIpRepository
import com.sanisamoj.data.repository.DefaultSessionRepository

class ServerContainerTest : ServerContainer {
    override val databaseRepository: DatabaseRepository by lazy { DefaultDatabaseRepository() }
    override val sessionRepository: SessionRepository by lazy { DefaultSessionRepository() }
    override val ipRepository: IpRepository by lazy { DefaultIpRepository() }
    override val mailRepository: MailRepository by lazy { MailRepositoryTest() }
}