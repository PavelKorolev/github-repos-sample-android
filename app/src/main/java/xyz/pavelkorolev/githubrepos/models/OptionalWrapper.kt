package xyz.pavelkorolev.githubrepos.models

@Suppress("unused")
data class OptionalWrapper<T>(val value: T?) {
    fun isNull() = value == null
}