package xyz.pavelkorolev.githubrepos.services

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
import xyz.pavelkorolev.githubrepos.entities.ServerRepository
import java.util.concurrent.TimeUnit

interface ApiService {
    fun getRepositories(organization: String): Observable<List<ServerRepository>>
}

class ApiServiceImpl(private val debug: Boolean) : ApiService {

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
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    private val api: GithubApi by lazy {
        retrofit.create(GithubApi::class.java)
    }

    override fun getRepositories(organization: String): Observable<List<ServerRepository>> =
        api.getRepositories(organization)

    companion object {
        private const val baseUrl: String = "https://api.github.com"
    }

}

interface GithubApi {

    @GET("orgs/{org}/repos")
    fun getRepositories(@Path("org") organization: String): Observable<List<ServerRepository>>

}
