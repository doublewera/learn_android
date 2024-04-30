package com.example.pressbutton

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.pressbutton.databinding.FragmentFirstBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.Date


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var timer: CountDownTimer
    private lateinit var myViewModel: ItemViewModel

    private fun currDtStr(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")
        return sdf.format(Date())
    }
    private fun createHeader(trackName: String = "My New Track"): String {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<gpx creator=\"StravaGPX Android\" version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd http://www.garmin.com/xmlschemas/GpxExtensions/v3 http://www.garmin.com/xmlschemas/GpxExtensionsv3.xsd http://www.garmin.com/xmlschemas/TrackPointExtension/v1 http://www.garmin.com/xmlschemas/TrackPointExtensionv1.xsd\">\n" +
                " <metadata>\n" +
                "  <time>" + currDtStr() + "</time>\n" +
                " </metadata>\n" +
                " <trk>\n" +
                "  <name>" + trackName + "</name>\n" +
                "  <trkseg>\n"
    }

    private fun addEnding() : String{
        return "        </trkseg>\n        </trk>\n        </gpx>"
    }

    private fun addPoint(): String {
        var lat: Double? = 55.555555
        var lon: Double? = 37.777777
        var alt: Double? = 139.99999
        if (myViewModel.locationIsSet) {
            if (myViewModel.myLocation != null) {
                lat = myViewModel.myLocation?.latitude
                lon = myViewModel.myLocation?.longitude
                alt = myViewModel.myLocation?.altitude
            }
        }
        return "   <trkpt lat=\"" + lat.toString() + "\" lon=\"" + lon.toString() + "\">\n" +
                "    <ele>" + alt.toString() + "</ele>\n" +
                "    <time>" + currDtStr() + "</time>\n" +
                "   </trkpt>\n"
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        timer = object: CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                addText(binding.textviewFirst, addPoint())
            }

            override fun onFinish() {
                addText(binding.textviewFirst,addEnding())
            }
        }
        return binding.root

    }

    private fun addText(cmpnnt: TextView, txt: String) {
        cmpnnt.text = cmpnnt.text.toString().plus(txt)
    }

    //private fun addText(cmpnnt: TextView, txt: String): TimerTask.() -> Unit {
    //    return  {
    //        cmpnnt.text = cmpnnt.text.toString().plus(txt)
    //    }
    //}
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myViewModel = (activity as MainActivity).myViewModel
        binding.btnStart.setOnClickListener {
            binding.textviewFirst.text = ""
            addText(binding.textviewFirst, createHeader())
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            // Добавить код: создать файл, а если нет директории - создать директорию.
            // Запустить фоновый процесс или "подписаться на циклическое событие по времени
            timer.start()
            //timer(initialDelay = 1000L, period = 1000L ,
            //    action = addText(binding.textviewFirst, "Kuku")
            //)
            // Заменить надпись на кнопке на Pause, чтобы повторное нажатие приостанавливало треккинг координат
            // ЕСЛИ уже идёт фоновый процесс, "отписаться от циклического события", либо "остановить фоновый"
            //                                поменять на кнопке надпись на "продолжить" или "начать"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}