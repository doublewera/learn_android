package ru.itstep.skein

import android.location.Location
import androidx.lifecycle.ViewModel

class SharedData : ViewModel() {
    var data = ""
    var myLocation: Location? = Location("")
    var locationIsSet = false
    var running = false

    fun same(other: Location): Boolean {
        // Compares properties for structural equality
        return this.myLocation?.latitude == other.latitude &&
                this.myLocation?.longitude == other.longitude &&
                 this.myLocation?.altitude == other.altitude
    }
}