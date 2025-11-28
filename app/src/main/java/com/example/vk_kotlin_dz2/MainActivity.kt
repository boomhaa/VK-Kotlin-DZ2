package com.example.vk_kotlin_dz2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.vk_kotlin_dz2.presentation.ui.screens.imagelistscreen.ImageListScreen
import com.example.vk_kotlin_dz2.presentation.ui.theme.VKKotlinDZ2Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VKKotlinDZ2Theme {
                ImageListScreen()
            }
        }
    }
}