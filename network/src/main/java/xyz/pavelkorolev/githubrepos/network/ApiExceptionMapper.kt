package xyz.pavelkorolev.githubrepos.network

import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

interface ApiExceptionMapper {
    fun map(throwable: Throwable): ApiException
}

class ApiExceptionMapperImpl : ApiExceptionMapper {

    override fun map(throwable: Throwable): ApiException = when (throwable) {
        is HttpException -> ApiException.HttpException(throwable, throwable.code())
        is SocketTimeoutException -> ApiException.Timeout(throwable)
        is IOException -> ApiException.IOException(throwable)
        is IllegalStateException -> ApiException.IllegalStateException(throwable)
        else -> ApiException.Unknown(throwable)
    }

}
