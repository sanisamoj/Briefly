package com.sanisamoj.utils.converters

class BytesConverter(bytes: Long = 0) {
    private val byteToReturn: Double = bytes.toDouble()
    private val kb : Double = bytes.toDouble() / 1024
    private val mb = kb / 1024
    private val gb = mb / 1024

    fun getInKb(bytes: Double = byteToReturn): Double {
        return kb
    }

    fun getInMegabyte() : Double {
        return mb
    }

    fun getInGigabyte() : Double {
        return gb
    }
}