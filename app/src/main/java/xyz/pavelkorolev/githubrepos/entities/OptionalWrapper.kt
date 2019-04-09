package xyz.pavelkorolev.githubrepos.entities

@Suppress("unused")
data class OptionalWrapper<T>(val value: T?) {
    fun isNull() = value == null
}