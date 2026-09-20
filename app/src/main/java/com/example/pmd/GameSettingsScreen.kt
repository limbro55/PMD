package com.example.pmd

import android.widget.Button
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


data class GameSettings(
    val gameSpeed: Float = 1.0f,
    val maxCockroaches: Int = 10,
    val bonusIntervalSec: Int = 5,
    val roundDurationSec: Int = 60
)

@Composable
fun GameSettingsScreen (
    onSaveSettings: (GameSettings) -> Unit = {}
){
    var gameSpeed by remember { mutableFloatStateOf(1.0f)}
    var maxCockroaches by remember {mutableFloatStateOf(1.0f)}
    var bonusInterval by remember {mutableStateOf("5")}
    var roundDuration by remember {mutableStateOf("60")}

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        Text("Настройки игры", style = MaterialTheme.typography.titleLarge)

        Text("Скорость игры: ${String.format("%.1f", gameSpeed)}x")
        Slider(
            value = gameSpeed,
            onValueChange = {gameSpeed = it},
            valueRange = 0.5f..3.0f,
            steps = 4
        )

        Divider()

        Text("Мфкс. жуков на экране: ${maxCockroaches.toInt()}")
        Slider(
            value = maxCockroaches,
            onValueChange = {maxCockroaches = it},
            valueRange = 1f..30f,
            steps = 28
        )

        Divider()

        OutlinedTextField(
            value = bonusInterval,
            onValueChange = {if (it.isEmpty() || it.all{char->char.isDigit()}) bonusInterval = it},
            label = { Text("Интервал появления бонусов (сек)")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = roundDuration,
            onValueChange = {if (it.isEmpty() || it.all{char->char.isDigit()}) roundDuration = it},
            label = { Text("Длительность раунда (сек)")},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Сохранить настройки")
        }

    }

}


