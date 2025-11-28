package com.example.vk_kotlin_dz2.presentation.ui.screens.imagelistscreen.components

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.vk_kotlin_dz2.domain.model.ImageItem

@Composable
fun ImageCard(
    image: ImageItem,
    imageLoader: ImageLoader,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image.url)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(image.aspectRatio),
            contentScale = ContentScale.Crop
        )
    }
}