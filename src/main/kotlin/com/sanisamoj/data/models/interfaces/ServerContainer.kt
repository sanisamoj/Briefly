package com.sanisamoj.data.models.interfaces

interface ServerContainer {
    val databaseRepository: DatabaseRepository
    val sessionRepository: SessionRepository
    val ipRepository: IpRepository
    val mailRepository: MailRepository
    val botRepository: BotRepository
}