package com.example.vk_kotlin_dz2.data.remote.api

import com.example.vk_kotlin_dz2.data.remote.dto.ImageDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageApi {

    @GET("images/search")
    suspend fun getImages(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("order") order: String = "DESC",
        @Query("mime_types") mimeTypes: String = "jpg,png"
    ): List<ImageDto>
}