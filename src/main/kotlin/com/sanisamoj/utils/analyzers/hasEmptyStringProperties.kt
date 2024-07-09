package com.sanisamoj.utils.analyzers

import com.sanisamoj.data.models.enums.Errors
import kotlin.reflect.full.memberProperties

fun <T : Any> hasEmptyStringProperties(instance: T) {
    val anyStringPropertiesAreEmpty: Boolean = instance::class.memberProperties
        .map { it.call(instance) }
        .filterIsInstance<String>()
        .any { it.isEmpty() }

    if (anyStringPropertiesAreEmpty) {
        throw IllegalArgumentException(Errors.DataIsMissing.description)
    }
}