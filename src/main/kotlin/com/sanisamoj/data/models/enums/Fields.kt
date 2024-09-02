package com.sanisamoj.data.models.enums

enum class Fields(val title: String) {
    Id(title = "_id"),
    Name(title = "name"),
    Email(title = "email"),
    Phone(title = "phone"),
    Active(title = "active"),
    UserId(title = "userId"),
    Password(title = "password"),
    ShortLink(title = "shortLink"),
    ExpiresAt(title = "expiresAt"),
    TotalVisits(title = "totalVisits"),
    ImageProfile(title = "imageProfile"),
    ShortLinksId(title = "shortLinksId"),
    AccountStatus(title = "accountStatus"),
    ValidationCode(title = "validationCode")
}