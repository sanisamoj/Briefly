package com.sanisamoj.database.mongodb

import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.sanisamoj.utils.analyzers.dotEnv
import kotlinx.coroutines.delay
import org.bson.BsonInt64
import org.bson.Document
import java.util.concurrent.TimeUnit

object MongoDatabase {
    private var database: MongoDatabase? = null
    private lateinit var client : MongoClient
    private val connectionString: String = dotEnv("MONGODB_SERVER_URL")
    private val nameDatabase : String = dotEnv("NAME_DATABASE")

    private suspend fun init() {
        client = MongoClient.create(connectionString)
        val db: MongoDatabase = client.getDatabase(nameDatabase)

        try {
            val command = Document("ping", BsonInt64(1))
            db.runCommand(command)
            println("You successfully connected to MongoDB!")
            database = db
        } catch (me: MongoException) {
            System.err.println(me)
            println("A new attempt will be made to reconnect to mongodb in 30s.")
            delay(TimeUnit.SECONDS.toMillis(30))
            init()
        }
    }

    suspend fun initialize() { if (database == null) init() }

    suspend fun getDatabase(): MongoDatabase {
        if (database == null) init()
        return database!!
    }
}