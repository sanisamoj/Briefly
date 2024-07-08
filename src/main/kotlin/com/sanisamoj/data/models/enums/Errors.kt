package com.sanisamoj.data.models.enums

enum class Errors(val description: String) {
    DatabaseConnectionError("Database connection error!"),
    InternalServerError("Internal Server Error!"),
    RedisNotResponding("Redis not responding!"),
    UserAlreadyExists("User already exists!"),
    UnableToComplete("Unable to complete!"),
    InvalidLogin("Invalid email/password!"),
    TooManyRequests("Too many requests!"),
    InactiveAccount("Inactive Account!"),
    BlockedAccount("Blocked Account!"),
    ExpiredSession("Expired session!"),
    UserNotFound("User Not Found!"),
    DataIsMissing("Data is missing"),
}