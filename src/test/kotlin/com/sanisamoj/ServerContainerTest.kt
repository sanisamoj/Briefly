package com.sanisamoj

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.MailRepositoryTest
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer

class ServerContainerTest : ServerContainer {
    override val databaseRepository: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }
    override val mailRepository: MailRepository by lazy { MailRepositoryTest() }
}