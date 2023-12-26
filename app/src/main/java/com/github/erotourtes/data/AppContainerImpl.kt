package com.github.erotourtes.data

import android.content.Context
import androidx.room.Room
import com.github.erotourtes.data.plot.PlotRepository


interface AppContainer {
    val plotRepository: PlotRepository
}

class AppContainerImpl(private val applicationContext: Context) : AppContainer {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext, EtherealPlotDatabase::class.java, "plot-database"
        ).build()
    }

    // TODO: use interface
    override val plotRepository: PlotRepository by lazy {
        PlotRepository(plotDao = db.dao)
    }
}