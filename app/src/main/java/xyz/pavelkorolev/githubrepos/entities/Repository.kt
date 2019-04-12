package xyz.pavelkorolev.githubrepos.entities

data class Repository(
    val id: Long,
    val title: String,
    val description: String?,
    val url: String
)