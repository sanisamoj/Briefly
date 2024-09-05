package com.sanisamoj.services.server

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.ReportingRequest
import com.sanisamoj.data.models.dataclass.CountResponse
import com.sanisamoj.data.models.dataclass.VersionResponse
import com.sanisamoj.data.models.enums.WebSocketCountItem
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.services.email.MailService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    suspend fun incrementAccess(ip: String, route: String) {
        databaseRepository.applicationClicksInc(ip, route)
    }

    suspend fun getClickInSystemCount(): CountResponse {
        return CountResponse(WebSocketCountItem.SystemClicksCount.name, databaseRepository.getCountApplicationClicks())
    }

    suspend fun getLinkEntryCount(): CountResponse {
        return CountResponse(WebSocketCountItem.LinkEntriesCount.name, databaseRepository.getCountLinkEntry())
    }

    fun updateMinMobileVersion(version: String) {
        setMinMobileVersion(version)
    }

    fun updateTargetMobileVersion(version: String) {
        setTargetMobileVersion(version)
    }

    fun report(reportingRequest: ReportingRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            MailService().sendReportingEmail(reportingRequest)
        }
    }
}