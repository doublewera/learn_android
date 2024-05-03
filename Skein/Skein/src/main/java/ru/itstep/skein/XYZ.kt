package ru.itstep.skein

import android.location.Location

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class XYZ(location: Location) {
    val alt = location.altitude / 1000.0         // km
    val Rp = 6356.8  // km. Not used
    val Re = 6378.0  // km. Not used
    val R  = 6371.0 + alt  // km
    val lon = Math.toRadians(location.longitude)
    val lat = Math.toRadians(location.latitude)
    val z = R * sin(lat)
    val x = R * cos(lat) * cos(lon)
    val y = R * cos(lat) * sin(lon)

    fun distance(p: XYZ): Double {
        return sqrt((p.z - z) * (p.z - z) +
                (p.x - x) * (p.x - x) +
                (p.y - y) * (p.y - y))
    }
}
