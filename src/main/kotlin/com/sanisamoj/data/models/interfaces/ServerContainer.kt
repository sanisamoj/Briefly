package com.sanisamoj.data.models.interfaces

interface ServerContainer {
    val databaseRepository: DatabaseRepository
    val mailRepository: MailRepository
}