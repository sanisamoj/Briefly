package com.sanisamoj.data.models.dataclass

data class UserAgentInfo(
    val general: String,
    val deviceType: String,
    val operatingSystem: String,
    val subOperatingSystem: String,
    val operatingSystemDetails: List<String>,
    val browserEngine: String,
    val browserEngineDetails: List<String>,
    val webKit: String,
    val browser: String
)