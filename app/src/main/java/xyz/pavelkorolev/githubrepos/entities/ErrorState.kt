package xyz.pavelkorolev.githubrepos.entities

sealed class ErrorState {
    data class Message(val text: String) : ErrorState()
    object None : ErrorState()
}
