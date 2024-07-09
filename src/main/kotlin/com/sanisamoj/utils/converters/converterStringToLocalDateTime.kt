package com.sanisamoj.utils.converters

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun converterStringToLocalDateTime(localDateTimeInString: String): LocalDateTime {

    // Formatter to convert String to LocalDateTime
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    // Convert the string expiresIn to LocalDateTime
    val localDateTime: LocalDateTime = LocalDateTime.parse(localDateTimeInString, formatter)

    return localDateTime
}