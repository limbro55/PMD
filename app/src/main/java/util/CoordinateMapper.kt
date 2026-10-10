package com.example.pmd.util

import androidx.compose.ui.geometry.Offset

object CoordinateMapper {
    fun toPixels(x: Float, y: Float, width: Float, height: Float): Offset =
        Offset(x * width, y * height)

    fun toLogical(px: Float, py: Float, width: Float, height: Float): Pair<Float, Float> =
        px / width to py / height

    fun toPixelSize(size: Float, width: Float, height: Float): Float =
        size * minOf(width, height)
}