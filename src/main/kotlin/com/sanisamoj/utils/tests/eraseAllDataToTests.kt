package com.sanisamoj.utils.tests

import com.sanisamoj.data.models.dataclass.LinkEntry
import com.sanisamoj.data.models.dataclass.User
import com.sanisamoj.database.mongodb.CollectionsInDb
import com.sanisamoj.database.redis.Redis
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun eraseAllDataToTests() {
    runBlocking {
        launch { eraseAllDataInMongodb<User>(CollectionsInDb.Users) }
        launch { eraseAllDataInMongodb<LinkEntry>(CollectionsInDb.LinkEntry) }
        launch { Redis.flushAll() }
    }
}