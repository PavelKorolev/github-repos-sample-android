package xyz.pavelkorolev.githubrepos.models

sealed class ErrorState {
    data class Message(val text: String) : ErrorState()
    object None : ErrorState()
}
