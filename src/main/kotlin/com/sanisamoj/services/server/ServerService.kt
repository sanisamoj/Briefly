package com.sanisamoj.services.server

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.VersionResponse

class ServerService {

    fun getVersion(): VersionResponse {
        return VersionResponse(
            serverVersion = GlobalContext.version,
            mobileMinVersion = GlobalContext.getMobileMinVersion(),
            mobileTargetVersion = GlobalContext.getMobileTargetVersion()
        )
    }
}