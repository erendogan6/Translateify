package com.erendogan6.translateify.data.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class PexelsPhotoResponse(
    val photos: List<Photo>,
)

data class Photo(
    val src: PhotoSrc,
)

data class PhotoSrc(
    val original: String,
)

interface PexelsService {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 1,
    ): PexelsPhotoResponse
}
