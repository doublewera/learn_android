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
import java.util.Date


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var timer: CountDownTimer
    private var tm: Int = 0

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
                val sdf = SimpleDateFormat("\nyyyy-M-dd'T'hh:mm:ss'Z'\n".plus(tm.toString()))
                val currentDate = sdf.format(Date())
                addText(binding.textviewFirst, currentDate)
                //tm++
                //addText(binding.textviewFirst, tm.toString())
            }

            override fun onFinish() {
                addText(binding.textviewFirst,"Ura")
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

        binding.btnStart.setOnClickListener {
            addText(binding.textviewFirst,"\nStart\n")
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