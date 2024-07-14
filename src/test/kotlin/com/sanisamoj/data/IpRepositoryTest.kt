package com.sanisamoj.data

import com.sanisamoj.data.models.dataclass.IpInfo
import com.sanisamoj.data.models.interfaces.IpRepository

class IpRepositoryTest: IpRepository {
    override suspend fun getInfoByIp(ip: String): IpInfo {
        return IpInfo(
            ip = ip,
            hostname = "bacc54b0.virtua.com.br",
            city = "São Paulo",
            region = "São Paulo",
            country = "BR",
            loc = "-23.5475,-46.6361",
            org = "AS28573 Claro NXT Telecomunicacoes Ltda",
            postal = "01000-000 ",
            timezone = "America/Sao_Paulo"
        )
    }
}