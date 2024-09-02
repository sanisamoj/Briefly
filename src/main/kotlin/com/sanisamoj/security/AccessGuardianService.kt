package com.sanisamoj.security

import com.sanisamoj.config.GlobalContext
import com.sanisamoj.data.models.dataclass.BlockedIp
import com.sanisamoj.data.models.interfaces.DatabaseRepository
import com.sanisamoj.data.models.enums.CollectionsInRedis
import com.sanisamoj.database.redis.DataIdentificationRedis
import com.sanisamoj.database.redis.Redis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AccessGuardianService {
    private val databaseRepository: DatabaseRepository by lazy { GlobalContext.getDatabaseRepository() }
    private val timeToLive: Long by lazy { GlobalContext.BLOCKED_IPS_TIME_TO_LIVE }
    private const val LIMIT_TO_BLOCK: Int = 7

    fun markIpAsViolator(ip: String, route: String? = null) {
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

    fun isIpBlocked(ip: String): Boolean {
        return hasIpViolated(ip)
    }

    private fun hasIpViolated(ip: String): Boolean {
        val identificationRedis = DataIdentificationRedis(CollectionsInRedis.BlockedIps, ip)
        val blockedIp: BlockedIp = Redis.getObject<BlockedIp>(identificationRedis) ?: return false
        if(blockedIp.rateLimitExceeded >= LIMIT_TO_BLOCK) {
            CoroutineScope(Dispatchers.IO).launch {
                databaseRepository.removeAllLinksEntriesFromUnknownUser(ip)
            }
            return true
        } else {
            return false
        }
    }
}