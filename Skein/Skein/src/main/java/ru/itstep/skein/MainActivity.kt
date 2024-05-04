package ru.itstep.skein

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var notifChannel: NotificationChannel
    private lateinit var lastpoint: SharedData

    private fun applyStatusBar(notificationId: Int) {
        notifChannel = NotificationChannel("SkeinIsRunning", "Skein_running", NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notifChannel)
        var mBuilder = NotificationCompat.Builder(this, "SkeinIsRunning")
        mBuilder.setSmallIcon(R.drawable.ic_stat_name)
        val resultIntent = Intent(this, MainActivity::class.java)
        val resultPendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                PendingIntent.FLAG_MUTABLE)
        mBuilder.setContentIntent(resultPendingIntent)
        val notification = mBuilder.build()
        notification.flags =
            notification.flags or (
                    Notification.FLAG_NO_CLEAR or
                    Notification.FLAG_ONGOING_EVENT)
        notificationManager.notify(notificationId, notification)
    }
    private fun applyStatusBarPermission(notificationId: Int) {
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    applyStatusBar(notificationId)
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                applyStatusBar(notificationId)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.POST_NOTIFICATIONS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                //showInContextUI(...)
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lastpoint = SharedData(this)
        applyStatusBarPermission(1)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = turnOnLocUpd()
    }

    private fun putText(cmpnnt: TextView, txt: String) {
        cmpnnt.text = txt
    }

    private fun addText(cmpnnt: TextView, txt: String) {
        cmpnnt.text = cmpnnt.text.toString().plus(txt)
    }

    private fun turnOnLocUpd(): FusedLocationProviderClient? {
        var tv = findViewById<TextView>(R.id.stats)
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                    // Background location access granted.
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                } else -> {
                // No location access granted.
                    tv.text = "No permission"
                    //return@registerForActivityResult
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION))
            }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationRequest = LocationRequest.Builder(1000).build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0?: return
                    for (location in p0.locations){
                        if (location != null) {
                            lastpoint.change(location)
                            putText(tv, lastpoint.getStatsTxt())
                        }
                    }
                }
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
            startService(Intent(this, Bckg::class.java))
        }
        return fusedLocationClient
    }
}