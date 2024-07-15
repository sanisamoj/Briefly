package com.sanisamoj.data

import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.OperationField
import io.ktor.server.plugins.*
import org.bson.types.ObjectId
import java.time.LocalDateTime

object TestDatabaseRepository: DatabaseRepository {
    private var testUsers: MutableList<User> = mutableListOf(
        User(
            id = ObjectId(),
            username = "testUser",
            email = "test@test.com",
            password = "test",
            phone = "11111111111"
        )
    )

    private var testLinks: MutableList<LinkEntry> = mutableListOf(
        LinkEntry(
            userId = testUsers.first().id.toString(),
            shortLink = "abcd1234",
            originalLink = "https://example.com",
            expiresAt = LocalDateTime.now().plusYears(1).toString()
        )
    )

    override suspend fun applicationClicksInc(ip: String, route: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getCountApplicationClicks(): Int {
        TODO("Not yet implemented")
    }

    override suspend fun registerUser(user: User): User {
        testUsers.add(user)
        return user
    }

    override suspend fun getUserById(id: String): User {
        val user = testUsers.firstOrNull { it.id.toString() == id }
        return user ?: throw NotFoundException()
    }

    override suspend fun getUserByEmail(email: String): User {
        val user = testUsers.firstOrNull { it.email == email }
        return user ?: throw NotFoundException()
    }

    override suspend fun getUserByPhone(phone: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(userId: String, update: OperationField): User {
        val userIndex = testUsers.indexOfFirst { it.id.toString() == userId }

        if (userIndex == -1) {
            throw NotFoundException("User not found with ID $userId")
        }

        val currentUser = testUsers[userIndex]

        val updatedUser: User = when(update.field) {
            Fields.Email -> currentUser.copy(email = update.value as String)
            Fields.Name -> currentUser.copy(username = update.value as String)
            Fields.Phone -> currentUser.copy(phone = update.value as String)
            Fields.Status -> currentUser.copy(accountStatus = update.value as String)
            Fields.Password -> currentUser.copy(password = update.value as String)
            Fields.AccountStatus -> currentUser.copy(email = update.value as String)
            else -> throw IllegalArgumentException("Invalid field name: ${update.field}")
        }

        testUsers[userIndex] = updatedUser
        return updatedUser
    }

    override suspend fun deleteUser(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun registerLink(link: LinkEntry): LinkEntry {
        testLinks.add(link)
        return link
    }

    override suspend fun getLinkById(id: String): LinkEntry {
        val link = testLinks.firstOrNull { it.id.toString() == id }
        return link ?: throw NotFoundException("Link not found with ID $id")
    }

    override suspend fun getLinkByShortLink(shortLink: String): LinkEntry? {
        return testLinks.firstOrNull { it.shortLink == shortLink }
    }

    override suspend fun updateLinkByShortLink(shortLink: String, update: OperationField): LinkEntry {
        TODO("Not yet implemented")
    }

    override suspend fun addClickerInShortLink(shortLink: String, clicker: Clicker) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLinkByShortLink(shortLink: String) {
        TODO("Not yet implemented")
    }
}