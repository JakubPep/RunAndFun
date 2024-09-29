package com.example.runandfun.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RunViewModel(private val dispatcher: CoroutineDispatcher = Dispatchers.Main) : ViewModel() {

    private val _elapsedTime = MutableLiveData<Long>()
    val elapsedTime: LiveData<Long> get() = _elapsedTime

    private var startTime: Long = 0
    private var timerJob: Job? = null

    // Rozpocznij bieg - uruchom timer
    fun startRun() {
        startTime = System.currentTimeMillis()
        timerJob = viewModelScope.launch(dispatcher) {
            while (isActive) {
                val currentTime = System.currentTimeMillis()
                _elapsedTime.postValue((currentTime - startTime) / 1000)  // Zmiana czasu w sekundach
                delay(1000)  // Aktualizacja co 1 sekundę
            }
        }
    }

    // Zatrzymaj bieg - zatrzymaj timer
    fun stopRun() {
        timerJob?.cancel()
        timerJob = null
    }

    // Oblicz tempo na podstawie dystansu (w metrach) i czasu (w sekundach)
    fun calculatePace(distanceMeters: Float, timeSeconds: Long): String {
        if (distanceMeters == 0f || timeSeconds == 0L) {
            return "0:00 min/km"
        }
        val pace = (timeSeconds / 60f) / (distanceMeters / 1000f)
        val paceMinutes = pace.toInt()
        val paceSeconds = ((pace - paceMinutes) * 60).toInt()
        return String.format("%d:%02d min/km", paceMinutes, paceSeconds)
    }

    override fun onCleared() {
        super.onCleared()
        stopRun()  // Upewnij się, że timer jest zatrzymany, kiedy ViewModel jest niszczony
    }
}
