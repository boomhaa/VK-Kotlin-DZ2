package com.example.vk_kotlin_dz2.presentation.viewmodel

import com.example.vk_kotlin_dz2.domain.model.ImageItem

data class ImageListUiState(
    val isInitialLoading: Boolean = false,
    val isPaginationLoading: Boolean = false,
    val images: List<ImageItem> = emptyList(),
    val error: String? = null,
    val currentPage: Int = 1,
    val isEndReached: Boolean = false
)
