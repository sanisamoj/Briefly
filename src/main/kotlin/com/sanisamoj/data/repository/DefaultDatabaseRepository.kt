package com.sanisamoj.data.repository

import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.CollectionsInDb
import com.sanisamoj.database.mongodb.Fields
import com.sanisamoj.database.mongodb.MongodbOperations
import com.sanisamoj.database.mongodb.OperationField
import io.ktor.server.plugins.*
import org.bson.types.ObjectId

class DefaultDatabaseRepository: DatabaseRepository {
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
        val linkId =  MongodbOperations().register(
            collectionInDb = CollectionsInDb.LinkEntry,
            item = link
        ).toString()

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

    override suspend fun deleteLinkByShortLink(shortLink: String) {
        MongodbOperations().deleteItem<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            filter = OperationField(Fields.ShortLink, shortLink)
        )
    }
}