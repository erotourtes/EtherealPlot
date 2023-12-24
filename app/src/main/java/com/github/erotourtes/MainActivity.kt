package com.github.erotourtes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.room.plot.PlotDatabase
import com.github.erotourtes.ui.EtherealPlotApp

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            PlotDatabase::class.java,
            "plot-database"
        ).build()
    }

    private val plotModel by viewModels<PlotViewModel>(factoryProducer = {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PlotViewModel(db.dao) as T
            }
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("MainActivity", "onCreate")
        plotModel.loadStateSync()
        setContent { EtherealPlotApp(plotModel) }
    }
}