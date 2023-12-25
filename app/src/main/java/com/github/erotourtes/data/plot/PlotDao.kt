package com.github.erotourtes.data.plot

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PlotDao {
    @Upsert
    suspend fun savePlot(plot: Plot): Long

    @Upsert // Upsert is a combination of insert and update
    suspend fun savePlots(plots: List<Plot>): List<Long>

    @Delete
    suspend fun deletePlot(plot: Plot)


    @Query("SELECT * FROM Plot LIMIT 10")
    suspend fun getPreviousPlots(): List<Plot>
}