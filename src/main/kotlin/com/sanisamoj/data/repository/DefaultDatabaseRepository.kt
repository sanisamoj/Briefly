package com.sanisamoj.data.repository

import com.sanisamoj.config.GlobalContext.UNKNOWN_USER_ID
import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.ClickerCount
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class DefaultDatabaseRepository: DatabaseRepository {
    override suspend fun applicationClicksInc(ip: String, route: String) {
        val clickerCount = ClickerCount(ip, route)
        MongodbOperations().register(
            collectionInDb = CollectionsInDb.ClickerCount,
            item = clickerCount
        )

        Redis.incrementItemCount(RedisKeys.ClickersCount.name)
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
        ) ?: throw NotFoundException()
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
        ) ?: throw NotFoundException()
    }

    override suspend fun deleteUser(userId: String) {
        MongodbOperations().deleteItem<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Id, ObjectId(userId)),
        )
    }

    override suspend fun registerLink(link: LinkEntry): LinkEntry {
        val mongodbOperations = MongodbOperations()
        val linkId =  mongodbOperations.register(
            collectionInDb = CollectionsInDb.LinkEntry,
            item = link
        ).toString()

        if(link.userId !== UNKNOWN_USER_ID) {
            mongodbOperations.pushItem<LinkEntry>(
                collectionName = CollectionsInDb.Users,
                filter = OperationField(Fields.Id, ObjectId(link.userId)),
                update = OperationField(Fields.ShortLinksId, linkId)
            )
        }

        return getLinkById(linkId)
    }

    override suspend fun getLinkById(id: String): LinkEntry {
        return MongodbOperations().findOne<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.Id, ObjectId(id))
        ) ?: throw NotFoundException()
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
}