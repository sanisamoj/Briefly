package com.sanisamoj.data

import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository

class IpRepositoryTest: IpRepository {
    override suspend fun getInfoByIp(ip: String): IpInfo {
        return IpInfo(
            ip = ip,
            hostname = "bacc54b0.virtua.com.br",
            city = "São Paulo",
            cityIsoCode = "SP",
            country = "Brasil",
            countryIsoCode = "BR",
            continent = "South America",
            latitude = 213210.564564,
            longitude = 213210.564564,
            postal = "01000-000 ",
            timezone = "America/Sao_Paulo"
        )
    }
}