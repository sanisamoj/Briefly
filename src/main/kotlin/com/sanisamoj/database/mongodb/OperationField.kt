package com.sanisamoj.database.mongodb

import com.sanisamoj.data.models.enums.Fields

data class OperationField(
    val field: Fields,
    val value: Any
)