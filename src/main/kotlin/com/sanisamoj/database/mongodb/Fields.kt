package com.sanisamoj.database.mongodb

enum class Fields(val title: String) {
    Id(title = "_id"),
    Email(title = "email"),
    Phone(title = "phone"),
    Active(title = "active"),
    UserId(title = "userId"),
    ShortLink(title = "shortLink"),
    ExpiresAt(title = "expiresAt"),
    ShortLinksId(title = "shortLinksId"),
    TotalVisits(title = "totalVisits"),
    AccountStatus(title = "accountStatus"),
    ImageProfile(title = "imageProfile")
}