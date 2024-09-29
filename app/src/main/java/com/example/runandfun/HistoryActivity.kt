package com.example.runandfun

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // Znajdowanie elementów UI
        val runHistoryContainer: LinearLayout = findViewById(R.id.runHistoryContainer)
        val backButton: Button = findViewById(R.id.backButton)

        // Odczyt historii biegów z SharedPreferences
        val sharedPreferences = getSharedPreferences("RunData", MODE_PRIVATE)
        val runHistory = sharedPreferences.getString("runHistory", "")

        // Wyświetlanie historii biegów w oddzielnych polach dla każdej statystyki
        if (!runHistory.isNullOrEmpty()) {
            val runs = runHistory.split("\n").filter { it.isNotBlank() }
            for (run in runs) {
                // Rozbij dane biegu na czas, dystans i tempo
                val runDetails = run.split(", ")
                if (runDetails.size == 3) {
                    val (distance, time, pace) = runDetails

                    // Utwórz osobne pola dla dystansu, czasu i tempa
                    val distanceTextView = createStatTextView(distance)
                    val timeTextView = createStatTextView(time)
                    val paceTextView = createStatTextView(pace)

                    // Dodaj widoki do kontenera
                    runHistoryContainer.addView(distanceTextView)
                    runHistoryContainer.addView(timeTextView)
                    runHistoryContainer.addView(paceTextView)

                    // Dodaj separator między biegami
                    val separator = createSeparator()
                    runHistoryContainer.addView(separator)
                }
            }
        } else {
            // Jeśli nie ma historii biegów, wyświetl komunikat
            val emptyTextView = TextView(this)
            emptyTextView.text = "Brak zakończonych biegów"
            emptyTextView.textSize = 16f
            emptyTextView.setPadding(16, 16, 16, 16)
            emptyTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
            runHistoryContainer.addView(emptyTextView)
        }

        // Obsługa powrotu do poprzedniego ekranu
        backButton.setOnClickListener {
            finish()  // Zamyka aktywność i wraca do poprzedniego ekranu
        }
    }

    // Tworzenie TextView dla statystyki biegu
    private fun createStatTextView(stat: String): TextView {
        val textView = TextView(this)
        textView.text = stat
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)
        textView.setBackgroundResource(R.drawable.simple_background)
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        return textView
    }

    // Tworzenie separatora między biegami
    private fun createSeparator(): TextView {
        val separator = TextView(this)
        separator.text = ""
        separator.setPadding(0, 16, 0, 16)  // Odstęp między biegami
        return separator
    }
}
