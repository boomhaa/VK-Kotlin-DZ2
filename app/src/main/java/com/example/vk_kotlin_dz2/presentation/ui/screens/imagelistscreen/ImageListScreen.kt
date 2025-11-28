package com.example.vk_kotlin_dz2.presentation.ui.screens.imagelistscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.vk_kotlin_dz2.presentation.ui.screens.imagelistscreen.components.ErrorPlaceholder
import com.example.vk_kotlin_dz2.presentation.viewmodel.ImageListViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.vk_kotlin_dz2.R
import com.example.vk_kotlin_dz2.presentation.ui.screens.imagelistscreen.components.ImageCard
import kotlinx.coroutines.launch

@Composable
fun ImageListScreen(
    viewModel: ImageListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    val gridState = rememberLazyStaggeredGridState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(gridState, uiState.images.size) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filter { it != null }
            .map { it!! }
            .distinctUntilChanged()
            .collect { index ->
                val lastIndex = uiState.images.lastIndex
                if (index == lastIndex &&
                    !uiState.isPaginationLoading &&
                    !uiState.isEndReached
                ) {
                    viewModel.loadNextPage()
                }
            }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isInitialLoading && uiState.images.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                uiState.error != null && uiState.images.isEmpty() -> {
                    ErrorPlaceholder(
                        message = stringResource(id = R.string.error_title) +
                                ": ${uiState.error}",
                        onRetry = { viewModel.onRetry() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
                            state = gridState,
                            contentPadding = PaddingValues(8.dp),
                            verticalItemSpacing = 8.dp,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(
                                count = uiState.images.size,
                                key = { index -> uiState.images[index].id }
                            ) { index ->
                                val item = uiState.images[index]
                                ImageCard(
                                    image = item,
                                    imageLoader = imageLoader,
                                    onClick = {
                                        val message = context.getString(
                                            R.string.image_click_snackbar_text,
                                            index + 1
                                        )
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar(message)
                                        }
                                    }
                                )
                            }
                        }
                        if (uiState.isPaginationLoading && uiState.images.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        if (uiState.error != null && uiState.images.isNotEmpty()) {
                            ErrorPlaceholder(
                                message = stringResource(id = R.string.error_title) +
                                        ": ${uiState.error}",
                                onRetry = { viewModel.loadNextPage() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}