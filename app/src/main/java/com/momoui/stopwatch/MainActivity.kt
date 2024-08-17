package com.momoui.stopwatch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.momoui.stopwatch.NotificationManager.showNotification
import com.momoui.stopwatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var startTime: Long = 0
    private var elapsedTime: Long = 0
    private var isRunning = false
    private lateinit var handler: Handler
    private var laps = mutableListOf<String>()
    private lateinit var lapAdapter: LapAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(Looper.getMainLooper())

        NotificationManager.createNotificationChannel(this)

        binding.onItemClick = View.OnClickListener { view ->
            when (view.id) {
                R.id.start_button -> startTimer()
                R.id.stop_button -> stopTimer()
                R.id.reset_button -> resetTimer()
                R.id.lap_button -> addLap()
            }
        }

        lapAdapter = LapAdapter(laps)
        binding.lapView.layoutManager = LinearLayoutManager(this)
        binding.lapView.adapter = lapAdapter
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 206)
            }
        }
    }


    private fun startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis() - elapsedTime
            isRunning = true
            handler.post(updateTimerRunnable)
        }
    }

    private fun stopTimer() {
        if (isRunning) {
            elapsedTime = System.currentTimeMillis() - startTime
            isRunning = false
            handler.removeCallbacks(updateTimerRunnable)
        }
    }

    private fun resetTimer() {
        stopTimer()
        startTime = 0
        elapsedTime = 0
        laps.clear()
        lapAdapter.notifyDataSetChanged()
        binding.timerTextView.text = "00:00:00"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun addLap() {
        if (isRunning) {
            val lapTime = System.currentTimeMillis() - startTime
            val formattedTime = formatTime(lapTime)
            laps.add("Lap ${laps.size + 1}: $formattedTime")
            lapAdapter.notifyDataSetChanged()


            showNotification(this, "New Lap Added", "Lap Time: $formattedTime")
        }
    }

    private val updateTimerRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val time = currentTime - startTime
                binding.timerTextView.text = formatTime(time)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val displaySeconds = seconds % 60
        val displayMinutes = minutes % 60
        return String.format("%02d:%02d:%02d", hours, displayMinutes, displaySeconds)
    }
}
