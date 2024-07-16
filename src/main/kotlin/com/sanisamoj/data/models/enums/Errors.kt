package com.sanisamoj.data.models.enums

enum class Errors(val description: String) {
    MaximumShortLinksExceeded("maximum short links exceeded!"),
    DatabaseConnectionError("Database connection error!"),
    InternalServerError("Internal Server Error!"),
    InactiveRedirection("Inactive redirection!"),
    RedisNotResponding("Redis not responding!"),
    ShortLinkNotFound("Short link not found!"),
    UserAlreadyExists("User already exists!"),
    UnableToComplete("Unable to complete!"),
    InvalidLogin("Invalid email/password!"),
    AccessProhibited("Access prohibited!"),
    LinkIsNotActive("Link is not active"),
    TooManyRequests("Too many requests!"),
    InactiveAccount("Inactive Account!"),
    BlockedAccount("Blocked Account!"),
    ExpiredSession("Expired session!"),
    UserNotFound("User Not Found!"),
    DataIsMissing("Data is missing"),
    ExpiredLink("Expired link!")
}