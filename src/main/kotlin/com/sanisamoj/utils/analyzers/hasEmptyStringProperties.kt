package com.sanisamoj.utils.analyzers

import com.sanisamoj.data.models.enums.Errors
import kotlin.reflect.full.memberProperties

fun <T : Any> hasEmptyStringProperties(instance: T, propertiesToIgnore: List<String> = emptyList()) {
    val anyStringPropertiesAreEmpty: Boolean = instance::class.memberProperties
        .filter { it.name !in propertiesToIgnore }
        .map { it.call(instance) }
        .filterIsInstance<String>()
        .any { it.isEmpty() }

    if (anyStringPropertiesAreEmpty) {
        throw IllegalArgumentException(Errors.DataIsMissing.description)
    }
}