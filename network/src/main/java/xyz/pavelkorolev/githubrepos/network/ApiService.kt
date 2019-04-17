package xyz.pavelkorolev.githubrepos.network

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import xyz.pavelkorolev.githubrepos.network.models.ServerRepository
import xyz.pavelkorolev.githubrepos.network.models.ServerUser
import java.util.concurrent.TimeUnit

private const val BASE_URL: String = "https://api.github.com"

interface ApiService {
    fun getRepositories(organization: String): Observable<List<ServerRepository>>
    fun getContributors(organization: String, repository: String): Observable<List<ServerUser>>
}

class ApiServiceImpl(
    private val debug: Boolean,
    private val apiExceptionMapper: ApiExceptionMapper
) : ApiService {

    private val client: OkHttpClient by lazy {
        var builder = OkHttpClient.Builder()

        if (debug) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder = builder
                .addInterceptor(loggingInterceptor)
        }

        builder
            .addInterceptor(NoContentInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    private val api: GithubApi by lazy {
        retrofit.create(GithubApi::class.java)
    }

    private fun <T> Observable<T>.mapApiException(): Observable<T> = onErrorResumeNext { throwable: Throwable ->
        Observable.error(apiExceptionMapper.map(throwable))
    }

    override fun getRepositories(organization: String): Observable<List<ServerRepository>> =
        api.getRepositories(organization)
            .mapApiException()

    override fun getContributors(organization: String, repository: String): Observable<List<ServerUser>> =
        api.getContributors(organization, repository)
            .onErrorResumeNext { throwable: Throwable ->
                throwable.printStackTrace()
                if (throwable is GithubNoContentException) {
                    Observable.just(emptyList())
                } else {
                    Observable.error(throwable)
                }
            }
            .mapApiException()

}

interface GithubApi {

    @GET("orgs/{org}/repos")
    fun getRepositories(@Path("org") organization: String): Observable<List<ServerRepository>>

    @GET("repos/{owner}/{repo}/contributors")
    fun getContributors(
        @Path("owner") organization: String,
        @Path("repo") repository: String
    ): Observable<List<ServerUser>>

}
