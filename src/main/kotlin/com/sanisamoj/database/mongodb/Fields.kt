package com.sanisamoj.database.mongodb

enum class Fields(val title: String) {
    Id(title = "_id"),
    Email(title = "email"),
    Name(title = "name"),
    Phone(title = "phone"),
    Status(title = "status"),
    Active(title = "active"),
    Password(title = "password"),
    ShortLink(title = "shortLink"),
    ExpiresAt(title = "expiresAt"),
    ShortLinksId(title = "shortLinksId"),
    TotalVisits(title = "totalVisits"),
    AccountStatus(title = "accountStatus"),
}