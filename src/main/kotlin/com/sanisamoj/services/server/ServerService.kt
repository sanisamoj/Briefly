package com.sanisamoj.services.server

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.VersionResponse
import com.sanisamoj.data.models.interfaces.DatabaseRepository

class ServerService(
    private val databaseRepository: DatabaseRepository = GlobalContext.getDatabaseRepository(),
    private val setMinMobileVersion: (String) -> Unit = { GlobalContext.setMinMobileVersion(it) },
    private val setTargetMobileVersion: (String) -> Unit = { GlobalContext.setTargetMobileVersion(it) }
) {

    fun getVersion(): VersionResponse {
        return VersionResponse(
            serverVersion = GlobalContext.VERSION,
            mobileMinVersion = GlobalContext.getMobileMinVersion(),
            mobileTargetVersion = GlobalContext.getMobileTargetVersion()
        )
    }

    suspend fun getClickInSystemCount(): Int {
        return databaseRepository.getCountApplicationClicks()
    }

    fun updateMinMobileVersion(version: String) {
        setMinMobileVersion(version)
    }

    fun updateTargetMobileVersion(version: String) {
        setTargetMobileVersion(version)
    }
}