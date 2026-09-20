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
    var selectTabInd by remember { mutableIntStateOf(0)}
    var tabTitles = listOf("Игрок", "Настройки", "Правила", "Авторы")

    Column(modifier = Modifier.fillMaxSize()){
        ScrollableTabRow(selectedTabIndex = selectTabInd) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectTabInd == index,
                    onClick = {selectTabInd = index},
                    text = { Text(title)}
                )
            }
        }
    }

    when(selectTabInd){
        0->PlayerRegistrationScreen()
        1->GameSettingsScreen()
        2->Text("Текст правил", modifier = Modifier.padding(16.dp))
        3->Text("Список Авторов", modifier = Modifier.padding(16.dp))
    }
}