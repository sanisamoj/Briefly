package com.sanisamoj.config

import com.sanisamoj.api.bot.BotApi
import com.sanisamoj.data.models.interfaces.*
import com.sanisamoj.data.repository.DefaultBotRepository
import com.sanisamoj.data.repository.DefaultDatabaseRepository
import com.sanisamoj.data.repository.DefaultIpRepository
import com.sanisamoj.data.repository.DefaultMailRepository
import com.sanisamoj.data.repository.DefaultSessionRepository

class DefaultServerContainer : ServerContainer {
    override val databaseRepository: DatabaseRepository by lazy { DefaultDatabaseRepository() }
    override val sessionRepository: SessionRepository by lazy { DefaultSessionRepository() }
    override val ipRepository: IpRepository by lazy { DefaultIpRepository() }
    override val mailRepository: MailRepository by lazy { DefaultMailRepository }
    override val botRepository: BotRepository by lazy { DefaultBotRepository(botApiService = BotApi.retrofitBotService) }
}