package com.sanisamoj.data.repository

import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository

class DefaultIpRepository: IpRepository {
    override suspend fun getInfoByIp(ip: String): IpInfo {
        TODO("Not yet implemented")
    }
}