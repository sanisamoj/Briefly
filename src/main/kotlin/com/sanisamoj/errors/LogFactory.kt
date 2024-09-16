package com.sanisamoj.errors

import com.sanisamoj.data.models.dataclass.Log
import com.sanisamoj.data.models.enums.EventSeverity
import com.sanisamoj.data.models.enums.EventType
import com.sanisamoj.utils.analyzers.dotEnv
import java.io.PrintWriter
import java.io.StringWriter

object LogFactory {

    fun throwableToLog(
        cause: Throwable,
        eventType: EventType = EventType.ERROR,
        severity: EventSeverity = EventSeverity.MEDIUM,
        description: String? = null
    ): Log {
        return Log(
            serviceName = dotEnv("APP_LOG_NAME"),
            eventType = eventType.name,
            errorCode = "500",
            message = cause.message ?: "No messages",
            description = description,
            severity = severity.name,
            stackTrace = getStackTraceAsString(cause),
            additionalData = null
        )
    }

    private fun getStackTraceAsString(cause: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        cause.printStackTrace(pw)
        return sw.toString()
    }

}