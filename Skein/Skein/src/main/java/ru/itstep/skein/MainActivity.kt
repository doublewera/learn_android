package ru.itstep.skein

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.io.File

class MainActivity : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var lastpont = SharedData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = turnOnLocUpd()
    }

    private fun fileNameDt(): String {
        val sdf = SimpleDateFormat("yyyyMMddhhmmss'.gpx'")
        return sdf.format(Date())
    }

    private fun currDtStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
        return sdf.format(Date())
    }

    private fun addPoint(loc: Location): String {
        return "   <trkpt lat=\"" + loc.latitude.toString() + "\" lon=\"" + loc.longitude.toString() + "\">\n" +
                "    <ele>" + loc.altitude.toString() + "</ele>\n" +
                "    <time>" + currDtStr() + "</time>\n" +
                "   </trkpt>"
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
                            //myViewModel.myLocation = location
                            //myViewModel.locationIsSet = true
                            if (!lastpont.same(location)) {
                                addText(tv, addPoint(location) + "\n")
                                File(getExternalFilesDir(null), fileNameDt()).writeText(addPoint(location))
                                lastpont.myLocation = location
                            } else {
                                addText(tv, "mew-")
                            }
                        }
                    }
                }
            }
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper())
        }
        return fusedLocationClient
    }
}