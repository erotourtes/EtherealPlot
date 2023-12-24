package com.github.erotourtes.room.plot

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Plot")
data class Plot(
    val color: Int,
    val function: String,
    val isVisible: Boolean = true,
    val isValid: Boolean = true,

    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
