package com.example.pmd

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Calendar
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerRegistrationScreen() {
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Мужской") }

    var expandedCourse by remember { mutableStateOf(false) }
    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс")
    var selectedCourseText by remember { mutableStateOf(courses[0]) }

    var sliderPosition by remember { mutableFloatStateOf(1f) }
    val datePickerState = rememberDatePickerState()

    // Состояние сохранённых данных игрока
    var registeredPlayer by remember { mutableStateOf<PlayerData?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // Уровень сложности
        Text(
            text = "Уровень сложности: ${sliderPosition.toInt()}",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 1f..10f,
            steps = 8
        )

        // Дата рождения
        Text(
            text = "Дата рождения:",
            style = MaterialTheme.typography.titleMedium
        )
        DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth()
        )

        // Кнопка регистрации
        Button(
            onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }

                val day = cal.get(Calendar.DAY_OF_MONTH)
                val month = cal.get(Calendar.MONTH) + 1
                val year = cal.get(Calendar.YEAR)

                val zodiac = getZodiacSign(day, month)
                val courseNum = courses.indexOf(selectedCourseText) + 1

                registeredPlayer = PlayerData(
                    fullName = fullName.ifEmpty { "Не указано" },
                    gender = selectedGender,
                    course = courseNum,
                    difficulty = sliderPosition.toInt(),
                    birthDay = day,
                    birthMonth = month,
                    birthYear = year,
                    zodiacSign = zodiac
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Вывод сохранённых данных (TextView) и картинки (ImageBox)
        registeredPlayer?.let { player ->
            Text(
                text = """
                    Информация об игроке:
//                    • ФИО: ${player.fullName}
//                    • Пол: ${player.gender}
//                    • Курс: ${player.course}
                    • Сложность: ${player.difficulty}
                    • Дата рождения: ${player.birthDay}.${player.birthMonth}.${player.birthYear}
                    • Знак зодиака: ${player.zodiacSign}
                """.trimIndent(),
                style = MaterialTheme.typography.bodyLarge
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
            }
        }
    }
}

fun getZodiacSign(day: Int, month: Int): String {
    return when (month) {
        1 -> if (day < 20) "Козерог" else "Водолей"
        2 -> if (day < 19) "Водолей" else "Рыбы"
        3 -> if (day < 21) "Рыбы" else "Овен"
        4 -> if (day < 20) "Овен" else "Телец"
        5 -> if (day < 21) "Телец" else "Близнецы"
        6 -> if (day < 21) "Близнецы" else "Рак"
        7 -> if (day < 23) "Рак" else "Лев"
        8 -> if (day < 23) "Лев" else "Дева"
        9 -> if (day < 23) "Дева" else "Весы"
        10 -> if (day < 23) "Весы" else "Скорпион"
        11 -> if (day < 22) "Скорпион" else "Стрелец"
        12 -> if (day < 22) "Стрелец" else "Козерог"
        else -> "Неизвестно"
    }
}