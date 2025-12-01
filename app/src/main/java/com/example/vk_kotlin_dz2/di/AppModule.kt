package com.example.vk_kotlin_dz2.di

import android.content.Context
import com.example.vk_kotlin_dz2.BuildConfig
import com.example.vk_kotlin_dz2.R
import com.example.vk_kotlin_dz2.data.local.CacheManager
import com.example.vk_kotlin_dz2.data.remote.api.GifApi
import com.example.vk_kotlin_dz2.data.remote.api.ImageApi
import com.example.vk_kotlin_dz2.data.repository.ImageRepositoryImpl
import com.example.vk_kotlin_dz2.domain.repository.ImageRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCacheManager(
        @ApplicationContext context: Context,
        json: Json
    ): CacheManager {
        return CacheManager(
            context = context,
            json = json
        )
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        api: ImageApi,
        gifApi: GifApi,
        cacheManager: CacheManager,
        @ApplicationContext context: Context
    ): ImageRepository {
        return ImageRepositoryImpl(
            api = api,
            gifApi = gifApi,
            cacheManager = cacheManager,
            context = context
        )
    }

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = BuildConfig.API_KEY

        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("x-api-key", apiKey)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        json: Json,
        client: OkHttpClient
    ): Retrofit {
        val baseUrl = context.getString(R.string.base_url)
        val contentType = "application/json".toMediaType()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideImageApi(retrofit: Retrofit): ImageApi =
        retrofit.create(ImageApi::class.java)

    @Provides
    @Singleton
    fun provideGifApi(retrofit: Retrofit): GifApi =
        retrofit.create(GifApi::class.java)
}