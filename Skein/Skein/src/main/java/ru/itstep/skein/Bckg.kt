package ru.itstep.skein

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.annotation.Nullable
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class Bckg : Service() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var lastpoint: SharedData
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // do your jobs here
        lastpoint = SharedData(this)
        turnOnLocUpd()
        return super.onStartCommand(intent, flags, startId)
    }
    @SuppressLint("MissingPermission")
    private fun turnOnLocUpd() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(1000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0?: return
                for (location in p0.locations){
                    if (location != null) {
                        lastpoint.save(location)
                    }
                }
            }
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
    /* private fun showRecordingNotification() {
        val not = Notification(
            R.drawable.icon,
            "Координаты записываются в файл " + lastpoint.fileNameDt(),
            System.currentTimeMillis())
        val contentIntent = PendingIntent.getActivity(
            this, 0, Intent(
                this,
                main::class.java
            ), Notification.FLAG_ONGOING_EVENT
        )
        not.flags = Notification.FLAG_ONGOING_EVENT
        not.setLatestEventInfo(this, R.string.app_name, "Application Description", contentIntent)
        mNotificationManager.notify(1, not)
    }*/
}
