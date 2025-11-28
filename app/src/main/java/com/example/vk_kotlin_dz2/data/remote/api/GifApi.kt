package com.example.vk_kotlin_dz2.data.remote.api

import com.example.vk_kotlin_dz2.data.remote.dto.ImageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface GifApi {
    @GET("images/search")
    suspend fun getGifs(
        @Query("limit") limit: Int,
        @Query("order") order: String = "RANDOM",
        @Query("mime_types") mimeTypes: String = "gif"
    ): List<ImageDto>
}