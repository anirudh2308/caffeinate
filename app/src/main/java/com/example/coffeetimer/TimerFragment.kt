package com.example.coffeetimer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide

class TimerFragment : Fragment(R.layout.fragment_timer) {

    private lateinit var timer: CountDownTimer
    private var millisLeft: Long = 0
    private var isPaused: Boolean = false

    private lateinit var timerTextView: TextView
    private lateinit var selectText: TextView
    private lateinit var finishText: TextView
    private lateinit var pauseButton: ImageButton
    private lateinit var stopButton: ImageButton
    private lateinit var coffeeBrewingGif: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timer, container, false)

        selectText = view.findViewById(R.id.selectText)
        timerTextView = view.findViewById(R.id.timerTextView)
        pauseButton = view.findViewById(R.id.pauseButton)
        stopButton = view.findViewById(R.id.stopButton)
        finishText = view.findViewById(R.id.finishText)
        coffeeBrewingGif = view.findViewById(R.id.coffeeBrewingGif)

        // Initial visibility settings
        selectText.visibility = View.VISIBLE
        timerTextView.visibility = View.GONE
        pauseButton.visibility = View.GONE
        stopButton.visibility = View.GONE
        finishText.visibility = View.GONE
        coffeeBrewingGif.visibility = View.GONE

        val coffeeType = arguments?.getString("COFFEE_TYPE")
        val brewTime = arguments?.getInt("BREW_TIME") ?: 0

        if (coffeeType != null && brewTime > 0) {
            // Update visibility for timer
            if (!::timer.isInitialized && millisLeft == 0L) {
                selectText.visibility = View.GONE
                timerTextView.visibility = View.VISIBLE
                pauseButton.visibility = View.VISIBLE
                stopButton.visibility = View.VISIBLE
                coffeeBrewingGif.visibility = View.VISIBLE

                Glide.with(this)
                    .asGif()
                    .load(R.drawable.brew_gif) // Replace with your GIF file name
                    .into(coffeeBrewingGif)
                finishText.visibility = View.GONE
                showStartNotification(coffeeType, brewTime)
                startTimer(brewTime * 60 * 1000L, coffeeType)  // Make sure duration is in milliseconds
            }

            pauseButton.setOnClickListener {
                if (isPaused) {
                    resumeTimer(coffeeType)
                } else {
                    pauseTimer()
                }
            }

            stopButton.setOnClickListener {
                stopTimer()
                findNavController().navigate(R.id.action_timerFragment_to_homeFragment)
            }
        } else {
            selectText.visibility = View.VISIBLE
            timerTextView.visibility = View.GONE
            pauseButton.visibility = View.GONE
            stopButton.visibility = View.GONE
            finishText.visibility = View.GONE
            coffeeBrewingGif.visibility = View.GONE
        }

        return view
    }

    private fun startTimer(duration: Long, coffeeType: String) {
        millisLeft = duration
        timer = object : CountDownTimer(millisLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millisLeft = millisUntilFinished
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                selectText.visibility = View.GONE
                timerTextView.visibility = View.GONE
                pauseButton.visibility = View.GONE
                stopButton.visibility = View.GONE
                finishText.visibility = View.VISIBLE
                coffeeBrewingGif.visibility = View.GONE
                finishText.text = "Your ${coffeeType.lowercase()} is ready. Enjoy your coffee!"
                showFinishNotification(coffeeType)
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_timerFragment_to_homeFragment)
                }, 2000)
            }
        }.start()
    }

    private fun pauseTimer() {
        isPaused = true
        timer.cancel()
        pauseButton.setImageResource(R.drawable.baseline_play_arrow_24)
    }

    private fun resumeTimer(coffeeType: String) {
        isPaused = false
        startTimer(millisLeft, coffeeType)
        pauseButton.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun stopTimer() {
        timer.cancel()
    }

    private fun showStartNotification(coffeeType: String?, brewTime: Int?) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "BREW_TIMER_CHANNEL"
            val channel = NotificationChannel(channelId, "Brew Timer Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), "BREW_TIMER_CHANNEL")
            .setSmallIcon(R.drawable.baseline_coffee_24)
            .setContentTitle("Coffee Ready!")
            .setContentText("Your ${coffeeType?.lowercase()} is brewing! Brew time: $brewTime minutes.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 1000))

        notificationManager.notify(1, notificationBuilder.build())
    }

    private fun showFinishNotification(coffeeType: String?) {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "BREW_TIMER_CHANNEL"
            val channel = NotificationChannel(channelId, "Brew Timer Channel", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), "BREW_TIMER_CHANNEL")
            .setSmallIcon(R.drawable.baseline_coffee_24)
            .setContentTitle("Coffee Ready!")
            .setContentText("Your ${coffeeType?.lowercase()} is ready. Enjoy your coffee!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500, 1000))

        notificationManager.notify(1, notificationBuilder.build())
    }
}
