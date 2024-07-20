package com.sanisamoj.database.redis

import com.sanisamoj.data.models.enums.Errors
import com.sanisamoj.utils.analyzers.dotEnv
import com.sanisamoj.utils.converters.ObjectConverter
import redis.clients.jedis.JedisPool
import redis.clients.jedis.exceptions.JedisConnectionException

object Redis {
    private val redisHost = dotEnv("REDIS_SERVER_URL")
    private val redisPort = dotEnv("REDIS_SERVER_PORT").toInt()
    val jedisPool: JedisPool = JedisPool(redisHost, redisPort)

    fun set(identification: DataIdentificationRedis, value: String) {
        val key = identification.key
        val collection = identification.collection.name

        try {
            jedisPool.resource.use { jedis -> jedis.set("$key:${collection}", value) }

        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun setWithTimeToLive(identification: DataIdentificationRedis, value: String, time: Long) {
        val key = identification.key
        val collection = identification.collection.name

        try {
            jedisPool.resource.use { jedis -> jedis.setex("$key:${collection}", time, value) }

        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun get(identification: DataIdentificationRedis): String? {
        val key = identification.key
        val collection = identification.collection.name

        return try {
            jedisPool.resource.use { jedis -> jedis["$key:${collection}"] }
        } catch (e: JedisConnectionException) {
            throw Exception(Errors.RedisNotResponding.description)
        }
    }

    fun setObject(identification: DataIdentificationRedis, value: Any) {
        val key = identification.key
        val collection = identification.collection.name
        val valueInString = ObjectConverter().objectToString<Any>(value)

        try {
            jedisPool.resource.use { jedis -> jedis.set("$key:${collection}", valueInString) }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun setObjectWithTimeToLive(identification: DataIdentificationRedis, value: Any, time: Long) {
        val key = identification.key
        val collection = identification.collection.name
        val valueInString = ObjectConverter().objectToString<Any>(value)

        try {
            jedisPool.resource.use { jedis -> jedis.setex("$key:${collection}", time, valueInString) }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    inline fun <reified T> getObject(identification: DataIdentificationRedis): T? {
        val key = identification.key
        val collection = identification.collection.name

        return try {
            val valueInString = jedisPool.resource.use { jedis -> jedis["$key:${collection}"] } ?: return null
            val stringInObject = ObjectConverter().stringToObject<T>(valueInString)
            stringInObject

        } catch (e: JedisConnectionException) {
            throw Exception(Errors.RedisNotResponding.description)
        }
    }

    fun incrementItemCount(key: String) {
        try {
            jedisPool.resource.use { jedis ->
                jedis.incr(key)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun getItemCount(key: String): Int {
        return try {
            Redis.jedisPool.resource.use { jedis ->
                jedis.get(key)?.toInt() ?: 0
            }
        } catch (e: JedisConnectionException) {
            e.printStackTrace()
            throw Exception("Failed to connect to Redis")
        }
    }

    fun flushAll() {
        try {
            jedisPool.resource.use { jedis -> jedis.flushAll() }
            println("All Redis data has been deleted.")

        } catch (e: Throwable) {
            println(e.message)
            throw Exception(Errors.RedisNotResponding.description)
        }
    }
}