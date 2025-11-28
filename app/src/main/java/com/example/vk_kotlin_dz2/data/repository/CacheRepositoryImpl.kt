package com.example.vk_kotlin_dz2.data.repository

import android.content.Context
import com.example.vk_kotlin_dz2.R
import com.example.vk_kotlin_dz2.domain.model.ImageItem
import com.example.vk_kotlin_dz2.domain.repository.CacheRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : CacheRepository {

    private val cacheFile: File
        get() {
            val name = context.getString(R.string.image_cache_file_name)
            return File(context.cacheDir, name)
        }

    private val listSerializer = ListSerializer(ImageItem.serializer())

    override suspend fun readCache(): List<ImageItem>  = withContext(Dispatchers.IO){
        if (!cacheFile.exists()) return@withContext emptyList()

        val text = cacheFile.readText()
        if (text.isBlank()) return@withContext emptyList()

        return@withContext runCatching {
            json.decodeFromString(listSerializer, text)
        }.getOrElse { emptyList() }
    }

    override suspend fun writeCache(images: List<ImageItem>) = withContext(Dispatchers.IO) {
        if (!cacheFile.exists()) cacheFile.createNewFile()

        val text = json.encodeToString(listSerializer, images)
        cacheFile.writeText(text)
    }

    override suspend fun clearCache() {
        if (!cacheFile.exists()) cacheFile.delete()
    }
}