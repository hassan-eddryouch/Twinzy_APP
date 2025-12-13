package com.example.twinzy_app.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.twinzy_app.ui.components.ParticleBackground
import com.example.twinzy_app.ui.theme.*

@Composable
fun SwipeScreen(
    viewModel: DiscoverViewModel = hiltViewModel()
) {
    // Use the new DiscoverScreen
    DiscoverScreen(viewModel = viewModel)
}