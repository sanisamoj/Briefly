package com.sanisamoj.utils.tests

import com.sanisamoj.database.mongodb.CollectionsInDb
import com.sanisamoj.database.mongodb.MongodbOperations

suspend inline fun <reified T : Any> eraseAllDataInMongodb(collectionsInDb: CollectionsInDb) {
    MongodbOperations().dropCollection<T>(collectionsInDb)
}