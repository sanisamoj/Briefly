package com.sanisamoj.errors

import com.sanisamoj.data.models.dataclass.Log
import com.sanisamoj.data.models.enums.EventSeverity
import com.sanisamoj.data.models.enums.EventType
import com.sanisamoj.utils.analyzers.dotEnv
import java.io.PrintWriter
import java.io.StringWriter

object LogFactory {

    fun log(
        message: String,
        eventType: EventType = EventType.ERROR,
        severity: EventSeverity = EventSeverity.MEDIUM,
        errorCode: String? = null,
        description: String? = null,
        cause: Throwable? = null,
        additionalData: Map<String, String>? = null
    ): Log {

        return Log(
            serviceName = dotEnv("APP_LOG_NAME"),
            eventType = eventType.name,
            errorCode = errorCode,
            message = message,
            description = description,
            severity = severity.name,
            stackTrace = if(cause != null) getStackTraceAsString(cause) else null,
            additionalData = additionalData
        )
    }


    fun throwableToLog(
        cause: Throwable,
        eventType: EventType = EventType.ERROR,
        severity: EventSeverity = EventSeverity.MEDIUM,
        errorCode: String? = null,
        description: String? = null,
        additionalData: Map<String, String>? = null
    ): Log {
        return Log(
            serviceName = dotEnv("APP_LOG_NAME"),
            eventType = eventType.name,
            errorCode = errorCode,
            message = cause.localizedMessage,
            description = description,
            severity = severity.name,
            stackTrace = getStackTraceAsString(cause),
            additionalData = additionalData
        )
    }

    private fun getStackTraceAsString(cause: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        cause.printStackTrace(pw)
        return sw.toString()
    }

}