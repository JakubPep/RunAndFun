package com.example.runandfun

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Znajdowanie elementów UI
        val totalDistanceTextView: TextView = findViewById(R.id.totalDistanceTextView)
        val totalTimeTextView: TextView = findViewById(R.id.totalTimeTextView)
        val averagePaceTextView: TextView = findViewById(R.id.averagePaceTextView)
        val backButton: Button = findViewById(R.id.backButton)
        val historyButton: Button = findViewById(R.id.historyButton)
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        val changeNameButton: Button = findViewById(R.id.changeNameButton)

        // Odczyt danych z SharedPreferences
        val sharedPreferences = getSharedPreferences("RunData", MODE_PRIVATE)
        val totalDistance = sharedPreferences.getFloat("totalDistance", 0f)
        val totalTimeSeconds = sharedPreferences.getLong("totalTime", 0L)

        // Obliczanie średniego tempa (min/km)
        val totalTimeMinutes = totalTimeSeconds / 60.0
        val averagePace = if (totalDistance > 0) totalTimeMinutes / totalDistance else 0.0
        val paceMinutes = averagePace.toInt()
        val paceSeconds = ((averagePace - paceMinutes) * 60).toInt()

        // Wyświetlanie danych w interfejsie użytkownika
        totalDistanceTextView.text = String.format("Łączny dystans: %.2f km", totalDistance)
        totalTimeTextView.text = formatElapsedTime(totalTimeSeconds)
        averagePaceTextView.text = String.format("Średnie tempo: %d:%02d min/km", paceMinutes, paceSeconds)

        // Odczytanie zapisanego imienia z SharedPreferences (UserData)
        val userSharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userName = userSharedPreferences.getString("userName", "Brak imienia")

        // Wyświetlanie imienia w TextView
        userNameTextView.text = userName

        // Przejście do ekranu historii biegów
        historyButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // Zmiana imienia użytkownika po kliknięciu przycisku
        changeNameButton.setOnClickListener {
            showChangeNameDialog()
        }

        // Powrót do ekranu głównego
        backButton.setOnClickListener {
            finish()
        }
    }

    // Funkcja zmiany imienia użytkownika
    private fun showChangeNameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Zmień swoje imię")

        // Pole tekstowe do wprowadzenia nowego imienia
        val input = android.widget.EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Zapisz", DialogInterface.OnClickListener { dialog, which ->
            val newUserName = input.text.toString()

            // Zapisz nowe imię w SharedPreferences (UserData)
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userName", newUserName)
            editor.apply()

            // Zaktualizuj wyświetlane imię na ekranie profilu
            val userNameTextView: TextView = findViewById(R.id.userNameTextView)
            userNameTextView.text = newUserName
        })
        builder.setNegativeButton("Anuluj", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        builder.show()
    }

    // Funkcja formatowania czasu na HH:MM:SS
    private fun formatElapsedTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("Łączny czas: %02d:%02d:%02d", hours, minutes, secs)
    }
}
