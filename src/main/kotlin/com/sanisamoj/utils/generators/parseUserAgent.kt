package com.sanisamoj.utils.generators

import com.sanisamoj.data.models.dataclass.UserAgentInfo

fun parseUserAgent(userAgent: String): UserAgentInfo {
    try {
        // Detect device type
        val deviceType: String = if (userAgent.contains("mobile", ignoreCase = true)) "mobile" else "desktop"

        // Remove brackets and split into parts
        val parts: List<String> = userAgent.replace("[\\(\\)]".toRegex(), "").split("[\\s;,]+".toRegex())

        // General info
        val generalInfo: String = parts[0]

        // Operating System
        val osInfoIndex = parts.indexOfFirst { it.contains("Windows") || it.contains("Linux") || it.contains("Mac") || it.contains("Android") }
        val operatingSystem = parts[osInfoIndex]
        val subOperatingSystem = if (osInfoIndex + 1 < parts.size) parts[osInfoIndex + 1] else ""

        // Operating System Details
        val osDetails: List<String> = mutableListOf<String>().apply {
            var index = osInfoIndex + 1
            while (index < parts.size && parts[index].contains(Regex("^[A-Za-z0-9.-]+$"))) {
                add(parts[index])
                index++
            }
        }

        // Browser Engine and Details
        val browserEngineIndex = parts.indexOfFirst { it.contains("WebKit") || it.contains("Gecko") || it.contains("Trident") || it.contains("Blink") }
        val browserEngine = parts[browserEngineIndex]
        val browserEngineDetails: List<String> = mutableListOf<String>().apply {
            var index = browserEngineIndex + 1
            while (index < parts.size && parts[index].contains(Regex("^[A-Za-z0-9./:-]+$"))) {
                add(parts[index])
                index++
            }
        }

        // Browser
        val browser = when {
            userAgent.contains("OPR", ignoreCase = true) || userAgent.contains("Opera", ignoreCase = true) -> "Opera"
            userAgent.contains("Vivaldi", ignoreCase = true) -> "Vivaldi"
            userAgent.contains("Edg", ignoreCase = true) -> "Edge"
            userAgent.contains("Chrome", ignoreCase = true) && !userAgent.contains("Edg") -> "Chrome"
            userAgent.contains("Firefox", ignoreCase = true) || userAgent.contains("FxiOS", ignoreCase = true) -> "Firefox"
            userAgent.contains("Safari", ignoreCase = true) && !userAgent.contains("Chrome") && !userAgent.contains("OPR") -> "Safari"
            userAgent.contains("Trident", ignoreCase = true) || userAgent.contains("MSIE", ignoreCase = true) -> "Internet Explorer"
            userAgent.contains("SamsungBrowser", ignoreCase = true) -> "Samsung Internet"
            userAgent.contains("DuckDuckGo", ignoreCase = true) -> "DuckDuckGo"
            userAgent.contains("TorBrowser", ignoreCase = true) -> "Tor"
            else -> "unknown"
        }

        // WebKit
        val webKit = parts.find { it.contains("Edg/") || it.contains("Chrome/") || it.contains("Safari/") } ?: "unknown"

        return UserAgentInfo(
            general = generalInfo,
            deviceType = deviceType,
            operatingSystem = operatingSystem,
            subOperatingSystem = subOperatingSystem,
            operatingSystemDetails = osDetails,
            browserEngine = browserEngine,
            browserEngineDetails = browserEngineDetails,
            webKit = webKit,
            browser = browser
        )
    } catch (_: Throwable) {
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
