package com.sanisamoj.data.repository

import com.sanisamoj.config.GlobalContext.MAX_UPLOAD_PROFILE_IMAGE
import com.sanisamoj.config.GlobalContext.MIME_TYPE_ALLOWED
import com.sanisamoj.config.GlobalContext.PUBLIC_IMAGES_DIR
import com.sanisamoj.config.WebSocketManager
import com.sanisamoj.data.models.dataclass.Clicker
import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.database.mongodb.CollectionsInDb
import com.sanisamoj.data.models.enums.Fields
import com.sanisamoj.database.mongodb.MongodbOperations
import com.sanisamoj.database.mongodb.OperationField
import com.sanisamoj.database.redis.Redis
import com.sanisamoj.database.redis.RedisKeys
import com.sanisamoj.utils.generators.CharactersGenerator
import io.ktor.http.content.*
import io.ktor.server.plugins.*
import org.bson.types.ObjectId
import java.io.File
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

    override suspend fun getCountLinkEntry(): Int {
        return MongodbOperations().countDocumentsWithoutFilter<LinkEntry>(CollectionsInDb.LinkEntry)
    }

    override suspend fun registerUser(user: User): User {
        val userId: String = MongodbOperations().register(
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

    override suspend fun getUserByIdOrNull(id: String): User? {
        return MongodbOperations().findOne<User>(
            collectionName = CollectionsInDb.Users,
            filter = OperationField(Fields.Id, ObjectId(id))
        )
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
        val userInDb: User = getUserById(userId)
        val shortLinkIdList: MutableList<String> = userInDb.shortLinksId.toMutableList()
        val index: Int = shortLinkIdList.indexOf(linkEntryId)
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
        val linkId : String =  mongodbOperations.register(
            collectionInDb = CollectionsInDb.LinkEntry,
            item = link
        ).toString()

        if(webSocketSession.isAnyModeratorConnected()){
            val count: Int = getCountLinkEntry()
            webSocketSession.notifyAboutLinkEntryCount(count)
        }

        try {
            getUserById(link.userId)
            val shortLink: LinkEntry = getLinkById(link.id.toString())
            mongodbOperations.pushItem<LinkEntry>(
                collectionName = CollectionsInDb.Users,
                filter = OperationField(Fields.Id, ObjectId(link.userId)),
                update = OperationField(Fields.ShortLinksId, linkId)
            )

            return shortLink

        } catch (_: Throwable) {
            val shortLink: LinkEntry = getLinkById(link.id.toString())
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

    override suspend fun getAllLinkEntries(): List<LinkEntry> {
        return MongodbOperations().findAll(CollectionsInDb.LinkEntry)
    }

    override suspend fun getAllLinkEntriesFromTheUserWithPagination(
        userId: String,
        page: Int,
        size: Int
    ): List<LinkEntry> {
        return MongodbOperations().findAllWithPagingAndFilter(
            collectionName = CollectionsInDb.LinkEntry,
            pageNumber = page,
            pageSize = size,
            filter = OperationField(Fields.UserId, userId)
        )
    }

    override suspend fun countAllLinkEntriesFromTheUser(userId: String): Int {
        val filter = OperationField(Fields.UserId, userId)
        return MongodbOperations().countDocumentsWithFilter<LinkEntry>(CollectionsInDb.LinkEntry, filter)
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
            dateFieldName = Fields.ExpiresAt.title,
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
            dateFieldName = Fields.ExpiresAt.title,
            dateTime = currentTime
        )

        return expiredLinkEntryList
    }

    override suspend fun filterActiveAndExpiredLinks(dateTime: LocalDateTime): List<LinkEntry> {
        val mongodbOperations = MongodbOperations()
        val currentTime = LocalDateTime.now()

        val expiredLinkEntryList: List<LinkEntry> = mongodbOperations.findItemsWithFilterExpiringBeforeOrOn<LinkEntry>(
            collectionName = CollectionsInDb.LinkEntry,
            dateFieldName = Fields.ExpiresAt.title,
            dateTime = currentTime,
            filter = OperationField(Fields.Active, true)
        )

        return expiredLinkEntryList
    }

    override suspend fun saveMedia(multipartData: MultiPartData): List<String> {
        val pathToPublicImages = PUBLIC_IMAGES_DIR
        val imageNameList: List<String> = saveAndReturnListNames(multipartData, pathToPublicImages, MAX_UPLOAD_PROFILE_IMAGE)
        val imageSavedList: MutableList<String> = mutableListOf()
        imageNameList.forEach { name ->
            imageSavedList.add(name)
        }
        return imageSavedList
    }

    private suspend fun saveAndReturnListNames(
        multipartData: MultiPartData,
        path: File,
        maxImagesAllowed: Int
    ): List<String> {

        val imageNameList: MutableList<String> = mutableListOf()
        val imagePathOfSavedImages: MutableList<File> = mutableListOf()

        var imageCount = 0

        multipartData.forEachPart { part ->
            when (part) {

                is PartData.FileItem -> {
                    if (imageCount > maxImagesAllowed) {
                        imagePathOfSavedImages.forEach {
                            deleteMedia(it)
                        }
                        throw Exception(Errors.TheLimitMaxImageAllowed.description)
                    }

                    val mimeType: String = getType(part.originalFileName!!)
                    if (!MIME_TYPE_ALLOWED.contains(mimeType)) {
                        imagePathOfSavedImages.forEach {
                            deleteMedia(it)
                        }
                        throw Exception(Errors.UnsupportedMediaType.description)
                    }

                    val fileBytes: ByteArray = part.streamProvider().readBytes()
                    val filename = "${CharactersGenerator.generateWithNoSymbols()}-${part.originalFileName}"
                    File(path, filename).writeBytes(fileBytes)
                    imageNameList.add(filename)
                    imagePathOfSavedImages.add(File(path, filename))
                    imageCount++
                }

                else -> {}
            }

            part.dispose()
        }

        return imageNameList
    }

    private fun getType(filename: String): String {
        val extension = filename.substringAfterLast('.', "")
        return extension
    }

    override fun getMedia(name: String): File {
        val file = File("$PUBLIC_IMAGES_DIR", name)
        if(!file.exists()) throw Error(Errors.MediaNotExist.description)
        else return file
    }

    override fun deleteMedia(file: File) {
        getMedia(file.name)
        if(file.exists()) file.delete()
    }
}