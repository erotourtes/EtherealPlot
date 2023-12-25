package com.github.erotourtes.data.plot

class PlotRepository(
    private val plotDao: PlotDao
) {

    suspend fun savePlot(plot: Plot): Long {
        return plotDao.savePlot(plot)
    }

    suspend fun savePlots(plots: List<Plot>): List<Long> {
        return plotDao.savePlots(plots)
    }

    suspend fun deletePlot(plot: Plot) {
        plotDao.deletePlot(plot)
    }

    suspend fun getPreviousPlots(): List<Plot> {
        return plotDao.getPreviousPlots()
    }
}