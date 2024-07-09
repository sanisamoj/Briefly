package com.sanisamoj.utils.generators

import com.sanisamoj.data.models.dataclass.UserAgentInfo

fun parseUserAgent(userAgent: String): UserAgentInfo {
    try {
        // Remove whitespace, semicolons and commas
        val cleanedAgent: String = userAgent.replace("[\\s;,]+".toRegex(), " ")
            .replace("mobile", "", ignoreCase = true).trim()

        // Detect device type
        val deviceType: String = if (userAgent.contains("mobile", ignoreCase = true)) "mobile" else "desktop"

        // Remove brackets
        val agentWithoutBrackets: String = cleanedAgent.replace("[\\(\\)]".toRegex(), "")

        // Split into parts
        val parts: List<String> = agentWithoutBrackets.split(" ")

        // General info
        val generalInfo: String = parts[0]

        // Operating System
        val osInfo: String = parts.subList(1, 4).joinToString(" ")
        val osInfoSplit = osInfo.split(" ")
        val operatingSystem = osInfoSplit[0]
        val subOperatingSystem = "${osInfoSplit[1]} ${osInfoSplit[2]}"

        // Operating System Details
        val osDetails: List<String> = parts.subList(4, 6).map { it.trim() }

        // Browser Engine
        val browserEngineInfo: String = parts[6]

        // Browser Engine Details
        val browserEngineDetails: List<String> = parts.subList(7, parts.size - 1).map { it.trim() }

        // Browser
        val webKit: String = parts[parts.size - 1]
        val browser: String = parts[parts.size - 2]

        return UserAgentInfo(
            general = generalInfo,
            deviceType = deviceType,
            operatingSystem = operatingSystem,
            subOperatingSystem = subOperatingSystem,
            operatingSystemDetails = osDetails,
            browserEngine = browserEngineInfo,
            browserEngineDetails = browserEngineDetails,
            webKit = webKit,
            browser = browser
        )
    } catch (e: Throwable) {
        return UserAgentInfo(
            general = userAgent,
            deviceType = "unknown",
            operatingSystem = "unknown",
            subOperatingSystem = "unknown",
            operatingSystemDetails = emptyList(),
            browserEngine = "unknown",
            browserEngineDetails = emptyList(),
            webKit = "unknown",
            browser = "unknown"
        )
    }
}