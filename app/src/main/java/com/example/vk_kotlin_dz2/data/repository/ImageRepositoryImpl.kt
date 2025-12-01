package com.example.vk_kotlin_dz2.data.repository

import android.content.Context
import com.example.vk_kotlin_dz2.R
import com.example.vk_kotlin_dz2.data.local.CacheManager
import com.example.vk_kotlin_dz2.data.remote.api.GifApi
import com.example.vk_kotlin_dz2.data.remote.api.ImageApi
import com.example.vk_kotlin_dz2.domain.model.ImageItem
import com.example.vk_kotlin_dz2.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    private val api: ImageApi,
    private val cacheManager: CacheManager,
    private val gifApi: GifApi,
    @ApplicationContext private val context: Context
) : ImageRepository {
    override suspend fun getCachedImages(): List<ImageItem> = withContext(Dispatchers.IO) {
        return@withContext cacheManager.readCache()
    }

    override suspend fun loadPage(
        page: Int,
    ): Result<List<ImageItem>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val pageSize = context.getString(R.string.page_size).toInt()
            val catPage = (page - 1).coerceAtLeast(0)

            val staticDtos = api.getImages(
                page = catPage,
                limit = pageSize
            )

            val gifLimit = (pageSize / 4).coerceAtLeast(1)
            val gifDtos = gifApi.getGifs(limit = gifLimit)

            val imageDtos = staticDtos + gifDtos
            val convertedImages = imageDtos.map {
                ImageItem(
                    id = it.id,
                    url = it.url,
                    width = it.width,
                    height = it.height
                )
            }

            val cachedImages = cacheManager.readCache()
            val allImages = cachedImages + convertedImages
            cacheManager.writeCache(allImages)
            Result.success(convertedImages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}