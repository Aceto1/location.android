package com.example.tracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.reflect.Constructor

@Entity
data class LocationEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "altitude") val altitude: Double,
    @ColumnInfo(name = "speed") val speed: Float,
    @ColumnInfo(name = "accuracy") val accuracy: Float,
    @ColumnInfo(name = "altAccuracy") val altAccuracy: Float,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "offset") val offset: Int
)
