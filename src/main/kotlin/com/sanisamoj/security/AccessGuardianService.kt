package com.sanisamoj.security

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.BlockedIp
import com.sanisamoj.database.redis.CollectionsInRedis
import com.sanisamoj.database.redis.DataIdentificationRedis
import com.sanisamoj.database.redis.Redis

object AccessGuardianService {
    private val timeToLive: Long by lazy { GlobalContext.BLOCKED_IPS_TIME_TO_LIVE }
    private const val LIMIT_TO_BLOCK: Int = 10

    fun blockIp(ip: String, route: String? = null, timeToLive: Long = this.timeToLive) {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.BlockedIps, ip)
        val blockedIpInCache: BlockedIp? = Redis.getObject<BlockedIp>(identificationRedis)
        lateinit var updatedBlockedIp: BlockedIp

        if(blockedIpInCache != null) {
            val newRateLimitExceeded: Int = blockedIpInCache.rateLimitExceeded + 1
            updatedBlockedIp = blockedIpInCache.copy(rateLimitExceeded = newRateLimitExceeded)
            Redis.setObject(identificationRedis, updatedBlockedIp)

        } else {
            updatedBlockedIp = BlockedIp(ip, route, rateLimitExceeded = 1)
            Redis.setObjectWithTimeToLive(identificationRedis, updatedBlockedIp, time = timeToLive)
        }
    }

    fun isIpBlocked(ip: String, limitToBlock: Int = this.LIMIT_TO_BLOCK): Boolean {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.BlockedIps, ip)
        val blockedIp: BlockedIp? = Redis.getObject<BlockedIp>(identificationRedis)

        return blockedIp != null
    }
}