package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.database.mongodb.OperationField
import io.ktor.http.content.*
import java.io.File
import java.time.LocalDateTime

interface DatabaseRepository {
    suspend fun applicationClicksInc(ip: String, route: String)
    suspend fun getCountApplicationClicks(): Int
    suspend fun getCountLinkEntry(): Int
    suspend fun registerUser(user: User): User
    suspend fun getUserById(id: String): User
    suspend fun getUserByIdOrNull(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUserByPhone(phone: String): User?
    suspend fun updateUser(userId: String, update: OperationField): User
    suspend fun deleteUser(userId: String)
    suspend fun usersCount(): Int
    suspend fun getAllUserWithPagination(page: Int, size: Int): List<User>
    suspend fun removeLinkEntryIdFromUser(userId: String, linkEntryId: String)
    suspend fun removeAllLinksEntriesFromUnknownUser(ip: String)

    suspend fun registerLink(link: LinkEntry): LinkEntry
    suspend fun getLinkById(id: String): LinkEntry
    suspend fun getLinkByShortLink(shortLink: String): LinkEntry?
    suspend fun getAllLinkEntries(): List<LinkEntry>
    suspend fun getAllLinkEntriesFromTheUserWithPagination(userId: String, page: Int, size: Int): List<LinkEntry>
    suspend fun countAllLinkEntriesFromTheUser(userId: String): Int
    suspend fun updateLinkByShortLink(shortLink: String, update: OperationField): LinkEntry
    suspend fun addClickerInShortLink(shortLink: String, clicker: Clicker)
    suspend fun deleteLinkByShortLink(shortLink: String)
    suspend fun deleteExpiredLinks(dateTime: LocalDateTime)
    suspend fun filterExpiredLinks(dateTime: LocalDateTime): List<LinkEntry>
    suspend fun filterActiveAndExpiredLinks(dateTime: LocalDateTime): List<LinkEntry>

    suspend fun saveMedia(multipartData: MultiPartData): List<String>
    fun getMedia(name: String): File
    fun deleteMedia(file: File)
}