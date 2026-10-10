package com.example.pmd.game

data class GameConfig(
    val roundDurationSec: Int = 60,
    val spawnIntervalMs: Long = 800L,
    val maxBugsOnScreen: Int = 6,
    val bonusIntervalSec: Int = 15,
    val speedMultiplier: Float = 1.0f
)