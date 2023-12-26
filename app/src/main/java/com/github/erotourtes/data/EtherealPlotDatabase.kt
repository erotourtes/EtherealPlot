package com.github.erotourtes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.erotourtes.data.plot.Plot
import com.github.erotourtes.data.plot.PlotDao

@Database(
    entities = [Plot::class],
    version = 1,
)
abstract class EtherealPlotDatabase : RoomDatabase() {
    abstract val dao: PlotDao
}