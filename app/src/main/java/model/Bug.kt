package com.example.pmd.model

import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

data class Bug(
    val id: String = UUID.randomUUID().toString(),
    val type: BugType,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val size: Float
) {
    companion object {
        fun spawn(type: BugType, baseSize: Float = 0.1f): Bug {
            val angle = Math.toRadians((0..360).random().toDouble())
            val speed = 0.2f * type.speedMultiplier
            return Bug(
                type = type,
                x = (0.1f..0.9f).random(),
                y = (0.1f..0.9f).random(),
                vx = (speed * cos(angle)).toFloat(),
                vy = (speed * sin(angle)).toFloat(),
                size = baseSize * type.sizeMultiplier
            )
        }
    }
}