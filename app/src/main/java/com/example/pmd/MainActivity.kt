package com.example.pmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainTabScreen()
                }
            }
        }
    }
}

@Composable
fun MainTabScreen(){
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Игрок", "Настройки", "Правила", "Авторы")

    Scaffold(
        topBar = {
            ScrollableTabRow(selectedTabIndex = selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ){
            when (selectedTabIndex) {
                0 -> PlayerRegistrationScreen()
                1 -> GameSettingsScreen()
                2 -> Text("Текст правил", modifier = Modifier.padding(16.dp))
                3 -> Text("Сафонов, Чепурняк", modifier = Modifier.padding(16.dp))
        }
    }
}
}