package com.sanisamoj.database.redis

import com.sanisamoj.data.models.enums.CollectionsInRedis

data class DataIdentificationRedis(
    val collection: CollectionsInRedis,
    val key: String
)