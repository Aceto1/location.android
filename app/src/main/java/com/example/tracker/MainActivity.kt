package com.example.tracker

import android.R
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.example.tracker.ui.theme.TrackerTheme


class MainActivity : ComponentActivity() {
    var mServiceIntent: Intent? = null
    private var mYourService: LocationService? = null

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                Log.i("Service status", "Running")
                return true
            }
        }
        Log.i("Service status", "Not running")
        return false
    }

    override fun onDestroy() {
        //stopService(mServiceIntent);
        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                android.Manifest.permission.POST_NOTIFICATIONS,
            ),
            111
        )

        mYourService = LocationService()
        mServiceIntent = Intent(this, LocationService::class.java)
        if (!isMyServiceRunning(LocationService::class.java)) {
            startService(mServiceIntent)
        }

        //val workRequest = OneTimeWorkRequestBuilder<TrackingWorker>().build();
        //WorkManager.getInstance(applicationContext).enqueue(workRequest)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database-name"
        ).allowMainThreadQueries().build()

        val locations = db.locationDao().getAll()

        setContent {
            TrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = locations.count().toString()
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(loc: LocationEntity) {
    Text(
        text = loc.id.toString()
    )
}

@Composable
fun Greeting2() {
    Text(
        text = "Test"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrackerTheme {
        Greeting2()
    }
}