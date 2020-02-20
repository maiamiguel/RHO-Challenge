package com.ua.rho_challenge.network.network

import androidx.lifecycle.LiveData
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ua.rho_challenge.network.access_token
import com.ua.rho_challenge.network.access_token_secret
import com.ua.rho_challenge.network.consumer_key
import com.ua.rho_challenge.network.consumer_secret
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer
import se.akerfeldt.okhttp.signpost.SigningInterceptor
import java.util.concurrent.TimeUnit

class ApiService() {
    var api: TwitterStreamingApi? = null
    val BASE_URL = "https://stream.twitter.com/1.1/"

    init {
        val consumer = OkHttpOAuthConsumer(consumer_key, consumer_secret)
        consumer.setTokenWithSecret(access_token, access_token_secret)

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(SigningInterceptor(consumer))
            .build()

        /**
         * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
         * object.
         */
        var retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()

        api = retrofit.create(TwitterStreamingApi::class.java)
    }
}

/**
 * A public interface that exposes the [getProperties] method
 */
interface TwitterStreamingApi {
    /**
     * Returns a Coroutine [Deferred] which can be fetched with await() if in a Coroutine scope.
     */
    @Streaming
    @POST("statuses/filter.json")
    fun getTweetList(
        @Query("track") terms: String?
    ): Deferred<ResponseBody>//Observable<ResponseBody>
}