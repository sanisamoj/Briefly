package com.sanisamoj.data.models.enums

enum class Errors(val description: String) {
    DatabaseConnectionError("Database connection error!"),
    InternalServerError("Internal Server Error!"),
    InactiveRedirection("Inactive redirection!"),
    LinkIsNotActive("Link is not active"),
    RedisNotResponding("Redis not responding!"),
    ShortLinkNotFound("Short link not found!"),
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