package com.example.pmd.model

data class GameState(
    val bugs: List<Bug> = emptyList(),
    val score: Int = 0,
    val hits: Int = 0,
    val misses: Int = 0,
    val timeLeftSec: Float = 60f,
    val isRunning: Boolean = true
) {
    val accuracy: Float
        get() = if (hits + misses == 0) 0f
        else hits.toFloat() / (hits + misses) * 100f
}