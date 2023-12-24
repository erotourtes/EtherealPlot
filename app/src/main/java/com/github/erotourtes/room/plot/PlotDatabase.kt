package com.github.erotourtes.room.plot

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Plot::class],
    version = 1,
)
abstract class PlotDatabase : RoomDatabase() {
    abstract val dao: PlotDao
}