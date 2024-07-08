package com.sanisamoj.utils.converters

import com.google.gson.Gson

class ObjectConverter {

    val gson = Gson()

    // Function to convert object to string
    inline fun <reified T> objectToString(obj: T): String {
        return gson.toJson(obj)
    }

    // Function to convert string to object
    inline fun <reified T> stringToObject(objectInString: String): T {
        return gson.fromJson(objectInString, T::class.java)
    }

}