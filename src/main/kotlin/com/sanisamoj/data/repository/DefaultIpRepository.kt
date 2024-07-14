package com.sanisamoj.data.repository

import com.sanisamoj.api.IpInfoApi
import com.sanisamoj.api.IpInfoApiService
import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository
import com.sanisamoj.utils.analyzers.dotEnv
import java.time.LocalDateTime

class DefaultIpRepository(private val ipInfoApi: IpInfoApi = IpInfoApi) : IpRepository {
    private val token: String = dotEnv("IP_INFO_TOKEN")

    override suspend fun getInfoByIp(ip: String): IpInfo {
        val service: IpInfoApiService = ipInfoApi.retrofitIpService

        return try {
            val ipInfo: IpInfo = service.getIpInfo(ip, token)
            ipInfo

        } catch (e: Throwable) {
            IpInfo(
                ip = ip,
                hostname = "unknown",
                city = "unknown",
                region = "unknown",
                country = "unknown",
                loc = "unknown",
                org = "unknown",
                postal = "unknown",
                timezone = LocalDateTime.now().toString()
            )
        }
    }
}