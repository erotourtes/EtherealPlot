package com.github.erotourtes.ui.screen.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.github.erotourtes.model.PlotViewModel


@Composable
fun MainScreen(
    plotViewModel: PlotViewModel,
    navController: NavController
) {

}

@Composable
fun MainLayout(
    onGoToSettings : () -> Unit,
) {
    Scaffold(
        bottomBar = {
                    // bottom bar has 2 icons: go to settings and go to plots
                    BottomAppBar() {



                    }

        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Text(text = "Hello world")
            }
        }
    )
}

@Preview
@Composable
private fun Preview() {
    MainLayout(
        onGoToSettings = {}
    )
}
