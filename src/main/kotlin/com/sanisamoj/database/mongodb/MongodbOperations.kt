package com.sanisamoj.database.mongodb

import com.mongodb.client.model.Filters
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId
import java.time.LocalDateTime

class MongodbOperations {

    // Adds an item to the database
    suspend inline fun <reified T : Any> register(collectionInDb: CollectionsInDb, item: T): ObjectId {

        // Returns the database
        val database = MongoDatabase.getDatabase()

        // Returns the collection
        val collection = database.getCollection<T>(collectionInDb.name)

        val id = collection.insertOne(item).insertedId?.asObjectId()?.value
        return id!!

    }

    // Returns an item
    suspend inline fun <reified T : Any> findOne(collectionName: CollectionsInDb, filter: OperationField): T? {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val result: T? = collection.find<T>(Document(filter.field.title, filter.value)).firstOrNull()

        return result
    }

    // Returns all items with paging
    suspend inline fun <reified T : Any> findAllWithPaging(
        collectionName: CollectionsInDb,
        pageSize: Int,
        pageNumber: Int
    ): List<T> {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val skip = (pageNumber - 1) * pageSize

        val result: List<T> = collection.find<T>()
            .skip(skip)
            .limit(pageSize)
            .toList()

        return result
    }

    // Returns count of items without filter
    suspend inline fun <reified T : Any> countDocumentsWithoutFilter(collectionName: CollectionsInDb): Int {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val result: Int = collection.find<T>().count()

        return result
    }

    // Deletes an item from the database
    suspend inline fun <reified T : Any> deleteItem(collectionName: CollectionsInDb, filter: OperationField) {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val result = collection.deleteOne(Document(filter.field.title, filter.value))
        if (result.deletedCount.toInt() == 0) throw Exception("No items were deleted")
        return
    }

    // Drop collections
    suspend inline fun <reified T : Any> dropCollection(collectionName: CollectionsInDb) {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        collection.drop()
    }

    // Updates a value in the item and return
    suspend inline fun <reified T : Any> updateAndReturnItem(
        collectionName: CollectionsInDb,
        filter: OperationField,
        update: OperationField
    ): T? {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)

        return try {
            collection.updateOne(
                Document(filter.field.title, filter.value),
                Document("\$set", Document(update.field.title, update.value))
            )
            val result: T = collection.find<T>(Document(filter.field.title, filter.value)).first()
            result
        } catch (e: Exception) {
            null
        }

    }

    // Push a value in the array of itens
    suspend inline fun <reified T : Any> pushItem(
        collectionName: CollectionsInDb,
        filter: OperationField,
        update: OperationField
    ) {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)

        collection.updateOne(
            Document(filter.field.title, filter.value),
            Document("\$push", Document(update.field.title, update.value))
        )
    }

    // Find items expiring before or on the specified date-time from the database
    suspend inline fun <reified T : Any> findItemsExpiringBeforeOrOn(collectionName: CollectionsInDb, fieldName: String, dateTime: LocalDateTime): List<T> {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)

        // Current date-time in ISO format
        val currentDateTime = dateTime.toString()

        // Create filter to match documents where 'fieldName' is less than or equal to the date-time
        val filter = Filters.lte(fieldName, currentDateTime)

        // Find matching documents
        return collection.find(filter).toList()
    }
}