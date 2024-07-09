package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.database.mongodb.OperationField

interface DatabaseRepository {
    suspend fun registerUser(user: User): User
    suspend fun getUserById(id: String): User
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByPhone(phone: String): User?
    suspend fun updateUser(userId: String, update: OperationField): User
    suspend fun deleteUser(userId: String)

    suspend fun registerLink(link: LinkEntry): LinkEntry
    suspend fun getLinkById(id: String): LinkEntry
    suspend fun getLinkByShortLink(shortLink: String): LinkEntry?
}