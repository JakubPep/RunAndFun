package com.example.runandfun

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        // Odbieranie danych z MainActivity
        val totalTime = intent.getStringExtra("TOTAL_TIME") ?: "00:00:00"
        val totalDistance = intent.getFloatExtra("TOTAL_DISTANCE", 0f)
        val totalTimeSeconds = intent.getLongExtra("TOTAL_TIME_SECONDS", 0L)

        // Obliczanie czasu w minutach
        val timeInMinutes = totalTimeSeconds / 60.0

        // Obliczanie tempa (min/km)
        val pace = if (totalDistance > 0) timeInMinutes / totalDistance else 0.0
        val paceMinutes = pace.toInt()  // Część całkowita minut
        val paceSeconds = ((pace - paceMinutes) * 60).toInt()  // Reszta w sekundach

        // Znajdowanie elementów UI
        val timeTextView: TextView = findViewById(R.id.timeTextView)
        val totalDistanceTextView: TextView = findViewById(R.id.totalDistanceTextView)
        val paceTextView: TextView = findViewById(R.id.paceTextView)
        val finishButton: Button = findViewById(R.id.finishButton)

        // Ustawianie wartości dla UI
        timeTextView.text = "Czas: $totalTime"
        totalDistanceTextView.text = String.format("Dystans: %.2f km", totalDistance)
        paceTextView.text = String.format("Średnie tempo: %d:%02d min/km", paceMinutes, paceSeconds)

        // Powrót do głównego ekranu po zakończeniu
        finishButton.setOnClickListener {
            finish()  // Zamyka aktywność i wraca do MainActivity
        }
    }
}
