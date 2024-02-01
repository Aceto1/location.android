package com.example.tracker

import android.location.Location
import android.location.LocationListener
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant

class LocationUpdateListener() : LocationListener {
    private lateinit var db : AppDatabase;

    constructor (db: AppDatabase) : this() {
        this.db = db;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveNewLocation(l: Location) {
        val newLocation = LocationEntity(
            accuracy = l.accuracy,
            altAccuracy = l.accuracy,
            altitude = l.altitude,
            latitude = l.latitude,
            longitude = l.longitude,
            speed = l.speed,
            timestamp = Instant.now().epochSecond,
            userId = 1,
            offset = 0,
            id = 0
        );

        db.locationDao().insertAll(newLocation)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onLocationChanged(loc: Location) {
        saveNewLocation(loc)
    }
}