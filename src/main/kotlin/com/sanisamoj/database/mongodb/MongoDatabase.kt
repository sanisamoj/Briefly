package com.sanisamoj.database.mongodb

import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.sanisamoj.utils.analyzers.dotEnv
import org.bson.BsonInt64
import org.bson.Document

object MongoDatabase {
    private var database: MongoDatabase? = null
    private lateinit var client : MongoClient

    // Variável responsável por armazenar a url da conexão com o banco de dados
    private val connectionString: String = dotEnv("MONGODB_SERVER_URL")

    // Variável responsável por armazenar o nome do banco de dados no MONGODB
    private val nameDatabase : String = dotEnv("NAME_DATABASE")

    // Inicia a conexão com o banco de dados
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
        }
    }

    // Inicia o banco de dados
    suspend fun initialize() { if (database == null) init() }

    // Retorna a instância do banco de dados
    suspend fun getDatabase(): MongoDatabase {
        if (database == null) init()
        return database!!
    }
}