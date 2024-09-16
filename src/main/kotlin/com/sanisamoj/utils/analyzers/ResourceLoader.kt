package com.sanisamoj.utils.analyzers

import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.nio.file.Files

object ResourceLoader {

    /**
     * Loads a resource as an InputStream.
     *
     * @param resourcePath Path to the resource within the resources folder.
     * @return InputStream of the resource.
     * @throws IllegalArgumentException if the resource is not found.
     */
    fun loadResourceAsStream(resourcePath: String): InputStream {
        return this::class.java.getResourceAsStream(resourcePath)
            ?: throw IllegalArgumentException("Recurso não encontrado: $resourcePath")
    }

    /**
     * Loads a resource as a File.
     *
     * @param resourcePath Path to the resource within the resources folder.
     * @return File pointing to the resource.
     * @throws IllegalArgumentException if the resource is not found.
     */
    fun loadResourceAsFile(resourcePath: String): File {
        val resourceUrl = this::class.java.getResource(resourcePath)
            ?: throw IllegalArgumentException("Recurso não encontrado: $resourcePath")
        return File(resourceUrl.toURI())
    }

    /**
     * Loads a file from the external file system as an InputStream.
     *
     * @param filePath Path to the external file.
     * @return InputStream of the file.
     * @throws IllegalArgumentException if the file is not found.
     */
    fun loadExternalFileAsStream(filePath: String): InputStream {
        val currentProjectDir = System.getProperty("user.dir")
        val externalFile = File(currentProjectDir, filePath)
        if (externalFile.exists()) {
            return Files.newInputStream(externalFile.toPath())
        }
        throw IllegalArgumentException("Arquivo externo não encontrado: $filePath")
    }

    /**
     * Loads a file from the external file system as a File.
     *
     * @param filePath Path to the external file.
     * @return File pointing to the external file.
     * @throws IllegalArgumentException if the file is not found.
     */
    fun loadExternalFileAsFile(filePath: String): File {
        val currentProjectDir = System.getProperty("user.dir")
        val externalFile = File(currentProjectDir, filePath)
        if (externalFile.exists()) {
            return externalFile
        }
        throw IllegalArgumentException("Arquivo externo não encontrado: $filePath")
    }

    /**
     * Converts the content of a JSON file located at the specified path into an object.
     *
     * @param resourcePath Path to the resource within the resources folder.
     * @return Object of the specified type, deserialized from the JSON.
     * @throws IllegalArgumentException if the resource is not found.
     */
    inline fun <reified T> convertJsonInputStreamAsObject(resourcePath: String): T {
        val inputStream: InputStream = loadResourceAsStream(resourcePath)
        val jsonContent: String = inputStream.bufferedReader().use { it.readText() }
        return Json.decodeFromString<T>(jsonContent)
    }
}