package com.example.vk_kotlin_dz2.domain.repository

import com.example.vk_kotlin_dz2.domain.model.ImageItem

interface ImageRepository {
    suspend fun getCachedImages(): List<ImageItem>
    suspend fun loadPage(page: Int): Result<List<ImageItem>>
}