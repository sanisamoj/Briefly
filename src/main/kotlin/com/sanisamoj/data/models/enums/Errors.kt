package com.sanisamoj.data.models.enums

enum class Errors(val description: String) {
    TermsOfServiceNotFound("Terms of Service not found!"),
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
    DataIsMissing("Data is missing"),
    UserNotFound("User Not Found!"),
    ExpiredLink("Expired link!"),
    InvalidLink("Invalid Link!"),
}