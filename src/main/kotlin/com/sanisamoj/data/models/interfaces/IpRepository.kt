package com.sanisamoj.data.models.interfaces

import com.sanisamoj.data.models.dataclass.IpInfo

interface IpRepository {
    suspend fun getInfoByIp(ip: String): IpInfo
}