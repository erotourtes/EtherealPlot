package com.github.erotourtes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.erotourtes.ui.EtherealPlotApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer = (application as EtherealPlotApplication).container

        setContent { EtherealPlotApp(appContainer) }
    }
}