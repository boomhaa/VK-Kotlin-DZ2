package com.example.vk_kotlin_dz2.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageItem(
    val id: String,
    val url: String,
    val height: Int,
    val width: Int
) {
    val aspectRatio: Float
        get() = if (height == 0) 1f else height.toFloat() / width.toFloat()
}
