package com.github.erotourtes.screen.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


data class HomeState(
    val text: String = "Hello World"
)

@Composable
fun Home() {
    Text(text = "Hello World")
}

@Preview(
    showBackground = true,
    name = "Home Preview"
)
@Composable
fun HomePreview() {
    Home()
}


