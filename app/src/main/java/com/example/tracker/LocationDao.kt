package com.example.tracker

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Query("SELECT * FROM locationentity")
    fun getAll(): List<LocationEntity>

    @Insert
    fun insertAll(vararg users: LocationEntity)
}
