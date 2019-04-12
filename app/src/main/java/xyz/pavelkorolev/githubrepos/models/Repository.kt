package xyz.pavelkorolev.githubrepos.models

data class Repository(
    val id: Long,
    val title: String,
    val description: String?,
    val url: String
)