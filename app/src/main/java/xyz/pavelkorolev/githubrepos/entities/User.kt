package xyz.pavelkorolev.githubrepos.entities

data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String?
)