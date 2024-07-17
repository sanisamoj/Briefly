package com.sanisamoj.data.models.dataclass

import com.sanisamoj.data.models.enums.AccountStatus
import com.sanisamoj.data.models.enums.AccountType
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class User(
    @BsonId
    val id: ObjectId = ObjectId(),
    val username: String,
    val email: String,
    val password: String,
    val phone: String,
    val type: String = AccountType.USER.name,
    val accountStatus: String = AccountStatus.Inactive.name,
    val shortLinksId: List<String> = emptyList(),
    val createdAt: String = LocalDateTime.now().toString(),
)
