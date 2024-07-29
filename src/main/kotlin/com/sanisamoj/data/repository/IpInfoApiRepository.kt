package com.sanisamoj.data.repository

import com.sanisamoj.api.ipinfo.IpInfoApi
import com.sanisamoj.api.ipinfo.IpInfoApiService
import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository
import com.sanisamoj.utils.analyzers.dotEnv

class IpInfoApiRepository(private val ipInfoApi: IpInfoApi = IpInfoApi) : IpRepository {
    private val token: String = dotEnv("IP_INFO_TOKEN")

    override suspend fun getInfoByIp(ip: String): IpInfo {
        val service: IpInfoApiService = ipInfoApi.retrofitIpService

        return try {
            val ipInfo: IpInfo = service.getIpInfo(ip, token)
            ipInfo

        } catch (e: Throwable) {
            IpInfo(ip)
        }
    }
}