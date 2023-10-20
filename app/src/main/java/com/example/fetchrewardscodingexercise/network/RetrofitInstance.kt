package com.example.fetchrewardscodingexercise.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object to provide a Retrofit instance for network operations.
 */
object RetrofitInstance {
    // Base URL for the API endpoint
    private const val BASE_URL = "https://fetch-hiring.s3.amazonaws.com/"

    /**
     * Lazy instance of [ItemApiService] to make network calls.
     */
    val api: ItemApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // JSON responses -> Kotlin objects
            .build()
            .create(ItemApiService::class.java)
    }
}
