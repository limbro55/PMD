package com.example.pmd

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.pmd.game.GameConfig
import com.example.pmd.game.GameEngine
import com.example.pmd.model.GameState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun GameScreen(settings: GameSettings, difficulty: Int = 1, onFinished: (GameState) -> Unit = {}, onExit: () -> Unit) {
    val config = remember(settings, difficulty) {
        GameConfig(
            roundDurationSec = settings.roundDurationSec.coerceAtLeast(1),
            maxBugsOnScreen = (settings.maxCockroaches + (difficulty - 1) / 2).coerceIn(1, 30),
            bonusIntervalSec = settings.bonusIntervalSec.coerceAtLeast(1),
            speedMultiplier = settings.gameSpeed * (1f + (difficulty.coerceIn(1,10) - 1) * 0.12f)
        )
    }
    val engine = remember(config) { GameEngine(config) }
    var state by remember(config) { mutableStateOf(GameState(timeLeftSec = config.roundDurationSec.toFloat())) }
    var round by remember { mutableIntStateOf(0) }

    LaunchedEffect(round, engine) {
        state = GameState(timeLeftSec = config.roundDurationSec.toFloat())
        state = engine.spawnIfPossible(state)
        var lastTime = android.os.SystemClock.elapsedRealtime()
        var spawnElapsed = 0f
        while (state.isRunning) {
            delay(16L)
            val now = android.os.SystemClock.elapsedRealtime()
            val dt = ((now - lastTime) / 1000f).coerceIn(0f, 0.1f)
            lastTime = now
            state = engine.tick(state, dt)
            if (!state.isRunning) break
            spawnElapsed += dt
            if (spawnElapsed >= config.spawnIntervalMs / 1000f) {
                spawnElapsed = 0f
                state = engine.spawnIfPossible(state)
            }
        }
        if (!state.isRunning) onFinished(state)
    }

    Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Очки: ${state.score}", style = MaterialTheme.typography.titleLarge)
            Text("Время: ${kotlin.math.ceil(state.timeLeftSec).toInt()} с", style = MaterialTheme.typography.titleLarge)
        }
        if (state.isRunning) {
            Text("Попадания: ${state.hits}    Промахи: ${state.misses}")
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth().weight(1f)
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp))
            ) {
                val density = LocalDensity.current
                val widthPx = with(density) { maxWidth.toPx() }
                val heightPx = with(density) { maxHeight.toPx() }
                Box(Modifier.fillMaxSize().pointerInput(engine, state.isRunning) {
                    detectTapGestures { point ->
                        if (state.isRunning && widthPx > 0 && heightPx > 0) {
                            state = engine.handleTap(state, point.x / widthPx, point.y / heightPx)
                        }
                    }
                })
                state.bugs.forEach { bug ->
                    val sizePx = bug.size * minOf(widthPx, heightPx)
                    Image(
                        painter = painterResource(bug.type.drawableRes),
                        contentDescription = "Жук: ${bug.type.name}",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.offset {
                            IntOffset(
                                (bug.x * widthPx - sizePx / 2).roundToInt(),
                                (bug.y * heightPx - sizePx / 2).roundToInt()
                            )
                        }.size(with(density) { sizePx.toDp() })
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Раунд завершён!", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(24.dp))
                Text("Очки: ${state.score}", style = MaterialTheme.typography.headlineSmall)
                Text("Попадания: ${state.hits}")
                Text("Промахи: ${state.misses}")
                Text("Точность: ${"%.1f".format(state.accuracy)}%")
                Spacer(Modifier.height(24.dp))
                Button(onClick = { round++ }) { Text("Играть снова") }
            }
        }
        OutlinedButton(onClick = onExit, modifier = Modifier.fillMaxWidth()) { Text("В меню") }
    }
}
