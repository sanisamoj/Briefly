package com.sanisamoj.security

import com.sanisamoj.data.models.dataclass.BlockedIp
import com.sanisamoj.database.redis.CollectionsInRedis
import com.sanisamoj.database.redis.DataIdentificationRedis
import com.sanisamoj.database.redis.Redis

object AccessGuardianService {

    fun blockIp(ip: String, route: String? = null) {
        val blockedIp = BlockedIp(ip, route)
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.BlockedIps, ip)
        Redis.setObject(identificationRedis, blockedIp)
    }

    fun isIpBlocked(ip: String): Boolean {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.BlockedIps, ip)
        val blockedIp: BlockedIp? = Redis.getObject<BlockedIp>(identificationRedis)

        return blockedIp != null
    }
}