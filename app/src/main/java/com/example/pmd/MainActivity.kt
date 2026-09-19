package com.example.pmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PlayerRegistrationScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerRegistrationScreen() {
    var fullName by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Мужской") }

    var expandedCourse by remember { mutableStateOf(false) }
    val courses = listOf("1 курс", "2 курс", "3 курс", "4 курс", "5 курс")
    var selectedCourseText by remember { mutableStateOf(courses[0]) }

    // Уровень сложности
    var sliderPosition by remember { mutableFloatStateOf(1f) }

    // Дата рождения
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("ФИО") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Пол:", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = (selectedGender == "Мужской"),
                onClick = { selectedGender = "Мужской" }
            )
            Text("Мужской")
            Spacer(modifier = Modifier.width(16.dp))
            RadioButton(
                selected = (selectedGender == "Женский"),
                onClick = { selectedGender = "Женский" }
            )
            Text("Женский")
        }

        ExposedDropdownMenuBox(
            expanded = expandedCourse,
            onExpandedChange = { expandedCourse = !expandedCourse }
        ) {
            OutlinedTextField(
                value = selectedCourseText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Курс") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCourse) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        Text(
            text = "Дата рождения:",
            style = MaterialTheme.typography.titleMedium
        )
        DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth()
        )
    }
}