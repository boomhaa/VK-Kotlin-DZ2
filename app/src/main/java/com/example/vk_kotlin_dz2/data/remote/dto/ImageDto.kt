package com.example.vk_kotlin_dz2.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageDto(
    val id: String,
    val url: String,
    val width: Int,
    val height: Int,
    @SerialName("mime_type")
    val mimeType: String? = null
)