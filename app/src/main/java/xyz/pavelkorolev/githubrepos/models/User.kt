package xyz.pavelkorolev.githubrepos.models

data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String?
)