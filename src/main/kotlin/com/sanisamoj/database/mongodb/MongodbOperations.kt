package com.sanisamoj.database.mongodb

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId

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

    // Returns all items
    suspend inline fun <reified T : Any> findAll(collectionName: CollectionsInDb): List<T> {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val result: List<T> = collection.find<T>().toList()

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

    // Returns all items by filter with paging
    suspend inline fun <reified T : Any> findAllByFilterWithPagingBySorting(
        collectionName: CollectionsInDb,
        filter: OperationField,
        sortingFilter: OperationField,
        additionalFilters: List<OperationField> = emptyList(),
        pageSize: Int,
        pageNumber: Int
    ): List<T> {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val skip = (pageNumber - 1) * pageSize

        val combinedFilters = mutableListOf((Document(filter.field.title, filter.value)))
        for (additionalFilter in additionalFilters) {
            combinedFilters.add(Document(additionalFilter.field.title, additionalFilter.value))
        }
        val finalFilter = Document("\$and", combinedFilters)

        val result: List<T> = collection.find<T>(finalFilter)
            .skip(skip)
            .limit(pageSize)
            .sort(Document(sortingFilter.field.title, sortingFilter.value))
            .toList()

        return result
    }

    // Returns all items by filter with paging
    suspend inline fun <reified T : Any> findAllWithPagingBySorting(
        collectionName: CollectionsInDb,
        sortingFilter: OperationField,
        pageSize: Int,
        pageNumber: Int
    ): List<T> {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        val skip = (pageNumber - 1) * pageSize

        val result: List<T> = collection.find<T>()
            .skip(skip)
            .limit(pageSize)
            .sort(Document(sortingFilter.field.title, sortingFilter.value))
            .toList()

        return result
    }

    // Returns count of items
    suspend inline fun <reified T : Any> countDocuments(
        collectionName: CollectionsInDb,
        filter: OperationField,
        additionalFilters: List<OperationField> = emptyList()
    ): Int {
        val database = MongoDatabase.getDatabase()

        val combinedFilters = mutableListOf((Document(filter.field.title, filter.value)))
        for (additionalFilter in additionalFilters) {
            combinedFilters.add(Document(additionalFilter.field.title, additionalFilter.value))
        }
        val finalFilter = Document("\$and", combinedFilters)

        val collection = database.getCollection<T>(collectionName.name)
        val result: Int = collection.find<T>(finalFilter).count()

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

    // Deletes all items from the database
    suspend inline fun <reified T : Any> deleteAllItems(collectionName: CollectionsInDb, filter: OperationField) {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)
        collection.deleteMany(Document(filter.field.title, filter.value))
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

    // Updates a value in the item
    suspend inline fun <reified T : Any> updateItem(
        collectionName: CollectionsInDb,
        filter: OperationField,
        update: OperationField
    ) {
        val database = MongoDatabase.getDatabase()
        val collection = database.getCollection<T>(collectionName.name)

        collection.updateOne(
            Document(filter.field.title, filter.value),
            Document("\$set", Document(update.field.title, update.value))
        )
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

}