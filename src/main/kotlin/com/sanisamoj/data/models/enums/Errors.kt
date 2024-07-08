package com.sanisamoj.data.models.enums

enum class Errors(val description: String) {
    DatabaseConnectionError("Database connection error"),
    InternalServerError("Internal Server Error"),
    UserAlreadyExists("User already exists"),
    UnableToComplete("Unable to complete"),
    TooManyRequests("Too many requests"),
    UserNotFound("User Not Found"),
    DataIsMissing("Data is missing"),
}