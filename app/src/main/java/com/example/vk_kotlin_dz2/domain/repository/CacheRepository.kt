package com.example.vk_kotlin_dz2.domain.repository

import com.example.vk_kotlin_dz2.domain.model.ImageItem

interface CacheRepository {
    suspend fun readCache(): List<ImageItem>
    suspend fun writeCache(images: List<ImageItem>)
    suspend fun clearCache()
}