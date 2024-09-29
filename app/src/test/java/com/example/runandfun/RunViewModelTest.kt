package com.example.runandfun

import com.example.runandfun.viewmodel.RunViewModel
import org.junit.Assert.*
import org.junit.Test

class RunViewModelTest {

    private val runViewModel = RunViewModel()

    @Test
    fun testCalculatePace_normalValues() {
        val pace = runViewModel.calculatePace(5000f, 1500L) // 5 km, 25 min
        assertEquals("5:00 min/km", pace)
    }

    @Test
    fun testCalculatePace_zeroDistance() {
        val pace = runViewModel.calculatePace(0f, 1500L)
        assertEquals("0:00 min/km", pace)
    }

    @Test
    fun testCalculatePace_zeroTime() {
        val pace = runViewModel.calculatePace(5000f, 0L)
        assertEquals("0:00 min/km", pace)
    }

    @Test
    fun testCalculatePace_fractionalPace() {
        val pace = runViewModel.calculatePace(3000f, 850L) // 3 km, 14 min 10 sec
        assertEquals("4:43 min/km", pace)
    }

    @Test
    fun testCalculatePace_rounding() {
        val pace = runViewModel.calculatePace(1000f, 305L) // 1 km, 5 min 5 sec
        assertEquals("5:05 min/km", pace)
    }
}
