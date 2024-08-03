package com.sanisamoj.utils.converters

class BytesConverter(bytes: Long = 0) {
    private val kb : Double = bytes.toDouble() / 1024
    private val mb = kb / 1024

    fun getInMegabyte() : Double {
        return mb
    }
}