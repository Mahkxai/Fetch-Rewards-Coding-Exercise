package com.example.fetchrewardscodingexercise.network

import com.example.fetchrewardscodingexercise.model.Item
import retrofit2.Response
import retrofit2.http.GET

/**
 * Defines the network service for fetching items using Retrofit.
 */
interface ItemApiService {

    /**
     * Fetches a list of items from the specified endpoint.
     *
     * @return A [Response] containing a list of [Item].
     */
    @GET("/hiring.json")
    suspend fun getItems(): Response<List<Item>>
}
