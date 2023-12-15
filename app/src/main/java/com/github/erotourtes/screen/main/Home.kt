package com.github.erotourtes.screen.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.erotourtes.ui.theme.AppTheme


data class HomeState(
    val text: String = "Hello World"
)

@Composable
fun Home() {
    Text(text = "Hello World", modifier = Modifier.padding(16.dp))
}

@Preview(
    showBackground = true,
    name = "Home Preview"
)
@Composable
fun HomePreview() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.error) {
            Home()
        }
    }
}


