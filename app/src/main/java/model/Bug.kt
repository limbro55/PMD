package com.example.pmd.model

import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

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
            val angle = Random.nextDouble(0.0, 2 * Math.PI)
            val speed = 0.2f * type.speedMultiplier
            return Bug(
                type = type,
                x = Random.nextFloat() * 0.8f + 0.1f,
                y = Random.nextFloat() * 0.8f + 0.1f,
                vx = (speed * cos(angle)).toFloat(),
                vy = (speed * sin(angle)).toFloat(),
                size = baseSize * type.sizeMultiplier
            )
        }
    }
}