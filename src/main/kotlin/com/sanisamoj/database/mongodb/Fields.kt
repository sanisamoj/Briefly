package com.sanisamoj.database.mongodb

enum class Fields(val title: String) {
    Id(title = "_id"),
    Email(title = "email"),
    Name(title = "name"),
    Phone(title = "phone"),
    Status(title = "status"),
    Password(title = "password"),
    ShortLink(title = "shortLink"),
    ShortLinksId(title = "shortLinksId"),
    TotalVisits(title = "totalVisits"),
    AccountStatus(title = "accountStatus"),
}