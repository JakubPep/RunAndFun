package com.example.runandfun

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.runandfun.viewmodel.RunViewModel
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mMap: GoogleMap  // Zmienna do obsługi mapy

    private lateinit var polylineOptions: PolylineOptions // Zmienna do rysowania ścieżki

    private var totalDistance = 0f  // Całkowity dystans biegu w metrach
    private var lastLocation: android.location.Location? = null  // Poprzednia lokalizacja

    private var isRunning = false

    // Uzyskaj referencję do ViewModel
    private lateinit var runViewModel: RunViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runViewModel = ViewModelProvider(this).get(RunViewModel::class.java)
        setContentView(R.layout.activity_main)

        // Sprawdź, czy użytkownik ma już zapisane imię
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", null)

        if (userName == null) {
            // Wyświetl dialog pytający o imię przy pierwszym uruchomieniu
            promptForUserName()
        }

        // Inicjalizacja opcji Polyline do rysowania ścieżki
        polylineOptions = PolylineOptions()
            .width(25f)
            .color(ContextCompat.getColor(this, R.color.primaryColor))  // Kolor linii
            .geodesic(true)

        // Inicjalizacja mapy
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Sprawdź uprawnienia do lokalizacji
        checkLocationPermission()
        startTrackingLocation()

        val timerTextView: TextView = findViewById(R.id.timerTextView)
        val startStopButton: Button = findViewById(R.id.startStopButton)

        // Obserwowanie czasu w ViewModel i aktualizowanie TextView
        runViewModel.elapsedTime.observe(this, Observer { elapsedTime ->
            val formattedTime = formatElapsedTime(elapsedTime)
            timerTextView.text = "Czas: $formattedTime"
        })

        val profileButton: Button = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        startStopButton.setOnClickListener {
            if (isRunning) {
                // Zakończ bieg
                runViewModel.stopRun()

                // Oblicz tempo (min/km) na zakończenie biegu
                val timeInMinutes = runViewModel.elapsedTime.value?.div(60.0) ?: 0.0
                val pace = if (totalDistance > 0) timeInMinutes / (totalDistance / 1000) else 0.0
                val paceMinutes = pace.toInt()
                val paceSeconds = ((pace - paceMinutes) * 60).toInt()

                // Zapisz dane biegu
                saveRunData(
                    totalDistance / 1000,
                    runViewModel.elapsedTime.value ?: 0L,
                    paceMinutes,
                    paceSeconds
                )

                // Przejdź do ekranu podsumowania
                val intent = Intent(this, SummaryActivity::class.java).apply {
                    putExtra("TOTAL_TIME", formatElapsedTime(runViewModel.elapsedTime.value ?: 0))
                    putExtra("TOTAL_TIME_SECONDS", runViewModel.elapsedTime.value ?: 0)
                    putExtra("TOTAL_DISTANCE", totalDistance / 1000)
                }
                startActivity(intent)

                // Zresetuj dystans i ostatnią lokalizację po zakończeniu biegu
                totalDistance = 0f
                lastLocation = null

                stopTrackingLocation()

                startStopButton.text = "Start"
                isRunning = false
            } else {
                // Rozpocznij bieg
                runViewModel.startRun()
                startTrackingLocation()
                startStopButton.text = "Stop"
                isRunning = true
            }
        }
    }



    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap = googleMap

        // Sprawdzanie uprawnień do lokalizacji
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            mMap.isMyLocationEnabled = true
            showCurrentLocation()
        }
    }

    private fun promptForUserName() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Podaj swoje imię")

        // Pole tekstowe do wprowadzenia imienia
        val input = android.widget.EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Zapisz", DialogInterface.OnClickListener { dialog, which ->
            val userName = input.text.toString()

            // Zapisz imię w SharedPreferences
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userName", userName)
            editor.apply()

            // Przenieś użytkownika na ekran profilu po zapisaniu imienia
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        })
        builder.setNegativeButton("Anuluj", DialogInterface.OnClickListener { dialog, _ ->
            dialog.cancel()
        })

        builder.show()
    }

    // Wyświetlanie aktualnej lokalizacji na mapie
    private fun showCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    // Sprawdzenie, czy mamy już uprawnienia do lokalizacji
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Poproś o uprawnienia
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startTrackingLocation() {
        val distanceTextView: TextView = findViewById(R.id.distanceTextView)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
                    setMinUpdateIntervalMillis(1000)  // Najszybszy interwał 1 sekunda
                }.build()

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult ?: return
                    for (location in locationResult.locations) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)

                        // Zawsze przesuwaj mapę wraz z ruchem użytkownika
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))

                        lastLocation?.let {
                            if (isRunning) {
                                val results = FloatArray(1)
                                android.location.Location.distanceBetween(
                                    it.latitude, it.longitude,
                                    location.latitude, location.longitude,
                                    results
                                )
                                totalDistance += results[0]

                                // Dodawanie lokalizacji do Polyline (rysowanie ścieżki)
                                polylineOptions.add(currentLatLng)
                                mMap.addPolyline(polylineOptions)

                                // Przelicz dystans na kilometry
                                val distanceInKm = totalDistance / 1000

                                // Wyświetlanie dystansu
                                distanceTextView.text =
                                    String.format("Dystans: %.2f km", distanceInKm)

                                // Obliczanie czasu w minutach
                                val timeInMinutes = runViewModel.elapsedTime.value?.div(60.0) ?: 0.0

                                // Obliczanie tempa (min/km)
                                val pace =
                                    if (distanceInKm > 0) timeInMinutes / distanceInKm else 0.0
                                val paceMinutes = pace.toInt()  // Część całkowita minut
                                val paceSeconds =
                                    ((pace - paceMinutes) * 60).toInt()  // Reszta w sekundach

                                // Wyświetlanie tempa
                                val paceTextView: TextView = findViewById(R.id.paceTextView)
                                paceTextView.text =
                                    String.format("Tempo: %d:%02d min/km", paceMinutes, paceSeconds)
                            }
                        }

                        // Aktualizowanie ostatniej lokalizacji
                        lastLocation = location
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }


    private fun stopTrackingLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun saveRunData(
        totalDistance: Float,
        totalTimeSeconds: Long,
        paceMinutes: Int,
        paceSeconds: Int
    ) {
        val sharedPreferences = getSharedPreferences("RunData", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Odczytaj bieżące wartości łączne
        val previousDistance = sharedPreferences.getFloat("totalDistance", 0f)
        val previousTime = sharedPreferences.getLong("totalTime", 0L)

        // Zaktualizuj łączne wartości
        val newDistance = previousDistance + totalDistance
        val newTime = previousTime + totalTimeSeconds

        editor.putFloat("totalDistance", newDistance)
        editor.putLong("totalTime", newTime)

        // Zapisz dane ostatniego biegu
        editor.putFloat("lastRunDistance", totalDistance)
        editor.putLong("lastRunTime", totalTimeSeconds)
        editor.putInt("lastRunPaceMinutes", paceMinutes)
        editor.putInt("lastRunPaceSeconds", paceSeconds)

        // Odczytaj istniejącą historię biegów (jeśli istnieje)
        val previousRuns = sharedPreferences.getString("runHistory", "") ?: ""

        // Formatowanie danych biegu do zapisu w historii
        val runData = "Dystans: %.2f km, Czas: ${formatElapsedTime(totalTimeSeconds)}, Tempo: %d:%02d min/km".format(
            totalDistance, paceMinutes, paceSeconds
        )

        // Dodaj nowy bieg do historii
        val newRunHistory = "$previousRuns\n$runData"

        editor.putString("runHistory", newRunHistory)
        editor.apply()  // Zapisz zmiany
    }


    // Wynik prośby o uprawnienia
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    mMap.isMyLocationEnabled = true
                    showCurrentLocation()
                }
            } else {
                // Użytkownik nie przyznał uprawnień, obsłuż to odpowiednio (np. pokaż komunikat)
            }
        }
    }

    // Stała do oznaczenia prośby o uprawnienia
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    // Formatowanie czasu na HH:MM:SS
    private fun formatElapsedTime(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTrackingLocation()
    }
}
