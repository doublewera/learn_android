package ru.itstep.skein

import android.location.Location
import androidx.lifecycle.ViewModel

class SharedData : ViewModel() {
    var data = ""
    var myLocation: Location? = Location("")
    var locationIsSet = false
    var running = false


}