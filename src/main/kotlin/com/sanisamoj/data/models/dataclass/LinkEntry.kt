package com.sanisamoj.data.models.dataclass

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class LinkEntry(
    @BsonId
    val id: ObjectId = ObjectId(),
    val userId: String,
    val active: Boolean = true,
    val public: Boolean = false,
    val shortLink: String,
    val originalLink: String,
    val totalVisits: List<Clicker> = emptyList(),
    val expiresAt: String = LocalDateTime.now().withYear(1).toString()
)
