package xyz.pavelkorolev.githubrepos.network

import okhttp3.Interceptor
import okhttp3.Response

class GithubNoContentException : RuntimeException()

/**
 * Github returns empty body with 204 code instead of empty contributors list when repository is empty.
 * [NoContentInterceptor] throws [GithubNoContentException] in such cases.
 */
class NoContentInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code() == 204) {
            throw GithubNoContentException()
        }
        return response
    }

}
