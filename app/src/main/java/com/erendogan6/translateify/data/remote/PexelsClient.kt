package com.erendogan6.translateify.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PexelsClient {
    private const val BASE_URL = "https://api.pexels.com/"

    fun create(): PexelsService {
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(PexelsService::class.java)
    }
}
