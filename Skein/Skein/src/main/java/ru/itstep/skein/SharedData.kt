package ru.itstep.skein

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.io.File

class SharedData(context: Context) : ViewModel() {
    val context = context
    var myLocation: Location? = null
    val dtStart = Date()
    var km = 0.0
    var h = 0.0

    private fun fileNameDt(): String {
        val sdf = SimpleDateFormat("yyyyMMddhh'.gpx'")
        return sdf.format(Date())
    }

    private fun Pifagor(newLocation: Location) : Double {
        if (myLocation != null) {
            return XYZ(myLocation!!).distance(XYZ(newLocation))
        }
        return 0.0
    }

    private fun currDtStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
        return sdf.format(Date())
    }

    private fun gpxPoint(location: Location): String {
        return "   <trkpt lat=\"" + location.latitude.toString() +
                      "\" lon=\"" + location.longitude.toString() + "\">\n" +
                "    <ele>" + location.altitude.toString() + "</ele>\n" +
                "    <time>" + currDtStr() + "</time>\n" +
                "   </trkpt>\n"
    }

    private fun createHeader(trackName: String = "My New Track"): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gpx creator=\"StravaGPX Android\" version=\"1.1\" " +
                "xmlns=\"http://www.topografix.com/GPX/1/1\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 " +
                "http://www.topografix.com/GPX/1/1/gpx.xsd\">\n" +
                " <metadata>\n" +
                "  <time>" + currDtStr() + "</time>\n" +
                " </metadata>\n" +
                " <trk>\n" +
                "  <name>" + trackName + "</name>\n" +
                "  <trkseg>\n"
    }

    private fun addEnding() : String{
        return "        </trkseg>\n    </trk>\n</gpx>\n"
    }

    fun save(location: Location): String {
        val result = change(location)
        File(context.getExternalFilesDir(null), fileNameDt()).appendText(result)
        return result
    }

    fun change(location: Location): String {
        if (same(location)) return ""
        if (myLocation != null) {
            km += Pifagor(location)
            var deltah = location.altitude - myLocation!!.altitude
            if (deltah > 0) {
                h += deltah
            }
        }
        myLocation = location
        return gpxPoint(location)
    }
    fun change() {}

    private fun same(other: Location): Boolean {
        // Compares properties for structural equality
        return this.myLocation?.latitude == other.latitude &&
                this.myLocation?.longitude == other.longitude &&
                 this.myLocation?.altitude == other.altitude
    }
}