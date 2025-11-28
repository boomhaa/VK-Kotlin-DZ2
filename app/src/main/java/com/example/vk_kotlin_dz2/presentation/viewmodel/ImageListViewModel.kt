package com.example.vk_kotlin_dz2.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vk_kotlin_dz2.R
import com.example.vk_kotlin_dz2.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val imageRepository: ImageRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageListUiState())
    val uiState: StateFlow<ImageListUiState> = _uiState.asStateFlow()


    init {
        loadInitData()
    }


    fun onRetry() {
        loadInitData()
    }
    fun loadNextPage() {
        if (_uiState.value.isPaginationLoading || _uiState.value.isEndReached) return

        val isInitial = _uiState.value.images.isEmpty()

        _uiState.value = _uiState.value.copy(
            isInitialLoading = isInitial,
            isPaginationLoading = !isInitial,
            error = null
        )

        viewModelScope.launch {
            val currentImages = _uiState.value.images
            val result = imageRepository.loadPage(_uiState.value.currentPage, currentImages)

            result.onSuccess { newItems ->
                val all = currentImages + newItems
                _uiState.value = _uiState.value.copy(
                    images = all,
                    isInitialLoading = false,
                    isPaginationLoading = false,
                    isEndReached = newItems.isEmpty(),
                    currentPage = _uiState.value.currentPage + if (newItems.isNotEmpty()) 1 else 0
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    isInitialLoading = false,
                    isPaginationLoading = false,
                    error = it.message ?: "Unknown error"
                )
            }
        }
    }

    private fun loadInitData() {
        viewModelScope.launch {
            val cachedImages = imageRepository.getCachedImages()
            if (cachedImages.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        images = cachedImages,
                        currentPage = (cachedImages.size / context.getString(R.string.page_size)
                            .toInt()) + 1
                    )
                }
            }else{
                _uiState.value = _uiState.value.copy(
                    isInitialLoading = false,
                    error = null,
                    isEndReached = false,
                    images = emptyList(),
                    currentPage = 1
                )
            }
            loadNextPage()
        }
    }
}