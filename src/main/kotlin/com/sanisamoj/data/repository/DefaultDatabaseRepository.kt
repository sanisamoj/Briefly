package com.sanisamoj.data.repository

import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.CollectionsInDb
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.MongodbOperations
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.database.redis.Redis
import com.sanisamoj.database.redis.RedisKeys
import io.ktor.server.plugins.*
import org.bson.types.ObjectId
import java.time.LocalDateTime

class DefaultDatabaseRepository(
    private val webSocketSession: WebSocketManager = WebSocketManager
): DatabaseRepository {
    override suspend fun applicationClicksInc(ip: String, route: String) {
        Redis.incrementItemCount(RedisKeys.ClickersCount.name)

        if(webSocketSession.isAnyModeratorConnected()){
            val count: Int = getCountApplicationClicks()
            webSocketSession.notifyAboutClickCount(count)
        }
    }

    override suspend fun getCountApplicationClicks(): Int {
        return Redis.getItemCount(RedisKeys.ClickersCount.name)
    }

    override suspend fun registerUser(user: User): User {
        val userId = MongodbOperations().register(
            collectionInDb = CollectionsInDb.Users,
            item = user
        ).toString()

        return getUserById(userId)
    }

    override suspend fun getUserById(id: String): User {
        return MongodbOperations().findOne<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Id, ObjectId(id))
        ) ?: throw NotFoundException(Errors.UserNotFound.description)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return MongodbOperations().findOne<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Email, email)
        )
    }

    override suspend fun getUserByPhone(phone: String): User? {
        return MongodbOperations().findOne<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Phone, phone)
        )
    }

    override suspend fun updateUser(userId: String, update: OperationField): User {
        return MongodbOperations().updateAndReturnItem<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Id, ObjectId(userId)),
            update = update
        ) ?: throw NotFoundException(Errors.UserNotFound.description)
    }

    override suspend fun deleteUser(userId: String) {
        MongodbOperations().deleteItem<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Id, ObjectId(userId)),
        )
    }

    override suspend fun usersCount(): Int {
        return MongodbOperations().countDocumentsWithoutFilter<User>(CollectionsInDb.Users)
    }

    override suspend fun getAllUserWithPagination(page: Int, size: Int): List<User> {
        return MongodbOperations().findAllWithPaging(
            collectionName = CollectionsInDb.Users,
            pageNumber = page,
            pageSize = size
        )
    }

    override suspend fun removeLinkEntryIdFromUser(userId: String, linkEntryId: String) {
        val userInDb = getUserById(userId)
        val shortLinkIdList = userInDb.shortLinksId.toMutableList()
        val index = shortLinkIdList.indexOf(linkEntryId)
        if(index != -1) {
            shortLinkIdList.removeAt(index)
            val update = OperationField(Fields.ShortLinksId, shortLinkIdList)
            updateUser(userId, update)
        }
    }

    override suspend fun removeAllLinksEntriesFromUnknownUser(ip: String) {
        val mongodbOperations = MongodbOperations()
        val linkEntryFromUnknownUser: List<LinkEntry> = mongodbOperations.findAllByFilter<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.UserId, ip)
        )

        if(linkEntryFromUnknownUser.isNotEmpty()) {
            mongodbOperations.deleteAllItems<LinkEntry>(
                collectionName = CollectionsInDb.LinkEntry,
                filter = OperationField(Fields.UserId, ip)
            )
        }
    }

    override suspend fun registerLink(link: LinkEntry): LinkEntry {
        val mongodbOperations = MongodbOperations()
        val linkId =  mongodbOperations.register(
            collectionInDb = CollectionsInDb.LinkEntry,
            item = link
        ).toString()

        try {
            getUserById(link.userId)
            val shortLink = getLinkById(link.id.toString())
            mongodbOperations.pushItem<LinkEntry>(
                collectionName = CollectionsInDb.Users,
                filter = OperationField(Fields.Id, ObjectId(link.userId)),
                update = OperationField(Fields.ShortLinksId, linkId)
            )

            return shortLink

        } catch (e: Throwable) {
            val shortLink = getLinkById(link.id.toString())
            return shortLink
        }

    }

    override suspend fun getLinkById(id: String): LinkEntry {
        return MongodbOperations().findOne<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.Id, ObjectId(id))
        ) ?: throw NotFoundException(Errors.ShortLinkNotFound.description)
    }

    override suspend fun getLinkByShortLink(shortLink: String): LinkEntry? {
        return MongodbOperations().findOne<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.ShortLink, shortLink)
        )
    }

    override suspend fun updateLinkByShortLink(shortLink: String, update: OperationField): LinkEntry {
        return MongodbOperations().updateAndReturnItem<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.ShortLink, shortLink),
            update = update
        ) ?: throw NotFoundException(Errors.ShortLinkNotFound.description)
    }

    override suspend fun addClickerInShortLink(shortLink: String, clicker: Clicker) {
        MongodbOperations().pushItem<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.ShortLink, shortLink),
            update = OperationField(Fields.TotalVisits, clicker)
        )
    }

    override suspend fun deleteLinkByShortLink(shortLink: String) {
        MongodbOperations().deleteItem<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.ShortLink, shortLink)
        )
    }

    override suspend fun deleteExpiredLinks(dateTime: LocalDateTime) {
        val mongodbOperations = MongodbOperations()
        val currentTime = LocalDateTime.now()

        val expiredLinkEntryList: List<LinkEntry> = mongodbOperations.findItemsExpiringBeforeOrOn<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            fieldName = Fields.ExpiresAt.title,
            dateTime = currentTime
        )

        expiredLinkEntryList.forEach {
            mongodbOperations.deleteItem<LinkEntry>(
                collectionName = CollectionsInDb.LinkEntry,
                filter = OperationField(Fields.Id, it.id)
            )
        }
    }

    override suspend fun filterExpiredLinks(dateTime: LocalDateTime): List<LinkEntry> {
        val mongodbOperations = MongodbOperations()
        val currentTime = LocalDateTime.now()

        val expiredLinkEntryList: List<LinkEntry> = mongodbOperations.findItemsExpiringBeforeOrOn<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            fieldName = Fields.ExpiresAt.title,
            dateTime = currentTime
        )

        return expiredLinkEntryList
    }
}