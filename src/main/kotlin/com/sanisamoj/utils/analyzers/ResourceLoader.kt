package com.sanisamoj.utils.analyzers

import java.io.File
import java.io.InputStream

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
}