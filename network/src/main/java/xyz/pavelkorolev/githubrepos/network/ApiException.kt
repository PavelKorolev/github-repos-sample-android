package xyz.pavelkorolev.githubrepos.network

sealed class ApiException(open val throwable: Throwable) : RuntimeException() {
    data class HttpException(
        override val throwable: Throwable,
        val code: Int
    ) : ApiException(throwable)

    data class Timeout(override val throwable: Throwable) : ApiException(throwable)
    data class IOException(override val throwable: Throwable) : ApiException(throwable)
    data class IllegalStateException(override val throwable: Throwable) : ApiException(throwable)
    data class Unknown(override val throwable: Throwable) : ApiException(throwable)
}