package com.example.lesson3.API

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val CONNECT_TIMEOUT_INTERVAL_SEC = 10L
private const val READ_TIMEOUT_INTERVAL_SEC = 10L
private const val WRITE_TIMEOUT_INTERVAL_SEC = 10L
private const val BASE_URL = "https://kotlin-api-poan.onrender.com"


object ListConnection {
    private var retrofit: Retrofit? = null

    private val client = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_INTERVAL_SEC, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_INTERVAL_SEC, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_INTERVAL_SEC, TimeUnit.SECONDS)
        .build()

    var gson = GsonBuilder()
        .setDateFormat("yyyy.MM.dd")
        .create()

    fun getClient(): Retrofit {
        if(retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!
    }

}