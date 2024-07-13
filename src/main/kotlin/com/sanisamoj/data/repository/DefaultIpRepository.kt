package com.sanisamoj.data.repository

import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository

class DefaultIpRepository : IpRepository {
    override suspend fun getInfoByIp(ip: String): IpInfo {
        return IpInfo(
            ip = "0.0.0.0",
            hostname = "unknown",
            city = "unknown",
            region = "unknown",
            country = "unknown",
            loc = "0,0",
            org = "unknown",
            postal = "00000",
            timezone = "UTC"
        )
    }
}