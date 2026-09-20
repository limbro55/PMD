package com.example.pmd

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import java.util.Calendar

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

    var registeredPlayer by remember { mutableStateOf<PlayerData?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ФИО
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth()
        )

        // Пол
        Text("Пол:", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selectedGender == "Мужской",
                onClick = { selectedGender = "Мужской" }
            )
            Text("Мужской", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = selectedGender == "Женский",
                onClick = { selectedGender = "Женский" }
            )
            Text("Женский")
        }

        // Курс
        ExposedDropdownMenuBox(
            expanded = expandedCourse,
            onExpandedChange = { expandedCourse = !expandedCourse }
        ) {
            OutlinedTextField(
                value = selectedCourseText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Курс") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCourse)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedCourse,
                onDismissRequest = { expandedCourse = false }
            ) {
                courses.forEach { course ->
                    DropdownMenuItem(
                        text = { Text(course) },
                        onClick = {
                            selectedCourseText = course
                            expandedCourse = false
                        }
                    )
                }
            }
        }

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

        // Вывод сохранённых данных
        registeredPlayer?.let { player ->
            Text(
                text = """
                    Информация об игроке:
                    • ФИО: ${player.fullName}
                    • Пол: ${player.gender}
                    • Курс: ${player.course}
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
                Image(
                    painter = painterResource(id = getZodiacImageResource(player.zodiacSign)),
                    contentDescription = "Знак зодиака",
                    modifier = Modifier.size(100.dp)
                )
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
//
//fun getZodiacImageResource(zodiac: String): Int {
//    return when (zodiac) {
//        "Овен" -> R.drawable.ic_aries
//        "Телец" -> R.drawable.ic_taurus
//        "Близнецы" -> R.drawable.ic_gemini
//        "Рак" -> R.drawable.ic_cancer
//        "Лев" -> R.drawable.ic_leo
//        "Дева" -> R.drawable.ic_virgo
//        "Весы" -> R.drawable.ic_libra
//        "Скорпион" -> R.drawable.ic_scorpio
//        "Стрелец" -> R.drawable.ic_sagittarius
//        "Козерог" -> R.drawable.ic_capricorn
//        "Водолей" -> R.drawable.ic_aquarius
//        "Рыбы" -> R.drawable.ic_pisces
//        else -> android.R.drawable.ic_menu_help
//    }
//}