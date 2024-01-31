package com.example.tracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.time.Instant
import java.util.Timer
import java.util.TimerTask

class TrackingWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as
                LocationManager

    private val timer = Timer();
    private lateinit var db : AppDatabase;

    override suspend fun doWork(): Result {
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                @RequiresApi(Build.VERSION_CODES.Q)
                override fun run() {
                    getLocation()
                }

            },
            0,
            5000
        )

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).build()

        return Result.success();
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkPermissions(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this.applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission", "Check happens in nested method call")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLocation() {
        if (!checkPermissions())
            return;

        val gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        var net_loc: Location? = null
        var gps_loc: Location? = null
        var finalLoc: Location? = null

        if (gps_enabled) gps_loc =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (network_enabled) net_loc =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (gps_loc != null) {
            finalLoc = gps_loc
        } else if (net_loc != null) {
            finalLoc = net_loc
        }

        if (finalLoc == null)
            return;

        val newLocation = LocationEntity(
            accuracy = finalLoc.accuracy,
            altAccuracy = finalLoc.accuracy,
            altitude = finalLoc.altitude,
            latitude = finalLoc.latitude,
            longitude = finalLoc.longitude,
            speed = finalLoc.speed,
            timestamp = Instant.now().epochSecond,
            userId = 1,
            offset = 0,
            id = 0
        );

        db.locationDao().insertAll(newLocation)
    }
}
