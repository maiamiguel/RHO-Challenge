package com.ua.rho_challenge.network.network

import com.ua.rho_challenge.network.access_token
import com.ua.rho_challenge.network.access_token_secret
import com.ua.rho_challenge.network.consumer_key
import com.ua.rho_challenge.network.consumer_secret
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
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
        val consumer = OkHttpOAuthConsumer(
            consumer_key,
            consumer_secret
        )
        consumer.setTokenWithSecret(
            access_token,
            access_token_secret
        )

        val client = OkHttpClient.Builder()
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .addInterceptor(SigningInterceptor(consumer))
            .build()

        /**
         * Use the Retrofit builder to build a retrofit object.
         */
        val retrofit = retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(TwitterStreamingApi::class.java)
    }
}

/**
 * A public interface that exposes the [getTweetList] method
 */
interface TwitterStreamingApi {

    @Streaming
    @POST("statuses/filter.json")
    fun getTweetList(
        @Query("track") terms: String?
    ): Call<ResponseBody>
}