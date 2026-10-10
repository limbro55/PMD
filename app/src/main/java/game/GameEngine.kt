package com.example.pmd.game

import com.example.pmd.model.Bug
import com.example.pmd.model.BugType
import com.example.pmd.model.GameState

class GameEngine(private val config: GameConfig) {

    fun tick(state: GameState, dtSec: Float): GameState {
        val updated = state.bugs.map { bug ->
            var nx = bug.x + bug.vx * dtSec
            var ny = bug.y + bug.vy * dtSec
            var nvx = bug.vx
            var nvy = bug.vy

            if (nx < 0f) { nx = 0f; nvx = -nvx }
            if (nx > 1f) { nx = 1f; nvx = -nvx }
            if (ny < 0f) { ny = 0f; nvy = -nvy }
            if (ny > 1f) { ny = 1f; nvy = -nvy }

            bug.copy(x = nx, y = ny, vx = nvx, vy = nvy)
        }
        val newTime = (state.timeLeftSec - dtSec).coerceAtLeast(0f)
        return state.copy(bugs = updated, timeLeftSec = newTime, isRunning = newTime > 0f)
    }

    fun handleTap(state: GameState, lx: Float, ly: Float): GameState {
        val hit = state.bugs.firstOrNull { bug ->
            val half = bug.size / 2f
            lx in (bug.x - half)..(bug.x + half) &&
                    ly in (bug.y - half)..(bug.y + half)
        }
        return if (hit != null) {
            state.copy(
                bugs = state.bugs - hit,
                score = state.score + hit.type.score,
                hits = state.hits + 1
            )
        } else {
            state.copy(misses = state.misses + 1, score = (state.score - 5).coerceAtLeast(0))
        }
    }

    fun spawnIfPossible(state: GameState): GameState {
        if (state.bugs.size >= config.maxBugsOnScreen) return state
        val r = (0..100).random()
        val type = when {
            r < 60 -> BugType.NORMAL
            r < 90 -> BugType.FAST
            else   -> BugType.RARE
        }
        return state.copy(bugs = state.bugs + Bug.spawn(type))
    }
}