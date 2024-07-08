package com.sanisamoj.config

import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.interfaces.MailRepository
import com.sanisamoj.data.models.interfaces.ServerContainer
import com.sanisamoj.data.repository.DefaultDatabaseRepository
import com.sanisamoj.data.repository.DefaultMailRepository

class DefaultServerContainer : ServerContainer {
    override val databaseRepository: DatabaseRepository by lazy { DefaultDatabaseRepository() }
    override val mailRepository: MailRepository by lazy { DefaultMailRepository }
}