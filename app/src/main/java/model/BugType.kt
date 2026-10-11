package com.example.pmd.model

import com.example.pmd.R

enum class BugType(
    val speedMultiplier: Float,
    val sizeMultiplier: Float,
    val score: Int,
    val drawableRes: Int
) {
    NORMAL(1.0f, 1.0f, 10, R.drawable.bug_normal),
    FAST  (2.0f, 0.7f, 20, R.drawable.bug_fast),
    RARE  (1.5f, 1.3f, 50, R.drawable.bug_rare)
}