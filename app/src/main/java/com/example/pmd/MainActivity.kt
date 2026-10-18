package com.example.pmd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pmd.bd.*
import com.example.pmd.model.GameState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = GameDatabase.get(applicationContext)
        setContent { MaterialTheme { Surface(modifier = Modifier.fillMaxSize()) { MainTabScreen(db) } } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(db: GameDatabase) {
    val dao = remember(db) { db.gameDao() }
    val scope = rememberCoroutineScope()
    var tab by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(GameSettings()) }
    var players by remember { mutableStateOf<List<PlayerEntity>>(emptyList()) }
    var selectedPlayerId by remember { mutableLongStateOf(-1L) }
    var records by remember { mutableStateOf<List<ScoreRow>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun refresh() {
        try {
            val updatedPlayers = withContext(Dispatchers.IO) { dao.getPlayers() }
            players = updatedPlayers
            if (updatedPlayers.none { it.id == selectedPlayerId }) selectedPlayerId = updatedPlayers.firstOrNull()?.id ?: -1L
            records = withContext(Dispatchers.IO) { dao.getRecords() }
            error = null
        } catch (e: Exception) { error = e.message ?: "Ошибка базы данных" }
        finally { loading = false }
    }
    LaunchedEffect(db) { refresh() }

    if (playing) {
        val active = players.firstOrNull { it.id == selectedPlayerId }
        if (active != null) {
            GameScreen(settings = settings, difficulty = active.difficulty,
                onFinished = { result: GameState ->
                    val playerId = active.id
                    scope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                dao.insertRecord(RecordEntity().apply {
                                    this.playerId = playerId
                                    score = result.score
                                    hits = result.hits
                                    misses = result.misses
                                    difficulty = active.difficulty
                                    durationSeconds = settings.roundDurationSec
                                    playedAt = System.currentTimeMillis()
                                })
                            }
                            refresh()
                        } catch (e: Exception) { error = e.message }
                    }
                }, onExit = { playing = false })
        }
        return
    }

    val titles = listOf("Игрок", "Играть", "Настройки", "Правила", "Авторы", "Рекорды")
    Scaffold(topBar = {
        ScrollableTabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { i, title ->
                Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
            }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.fillMaxSize()) {
                error?.let { Text("Ошибка: $it", color = MaterialTheme.colorScheme.error) }
                if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
                when (tab) {
                    0 -> PlayerRegistrationScreen(onRegister = { data ->
                        scope.launch {
                            try {
                                val id = withContext(Dispatchers.IO) {
                                    dao.insertPlayer(PlayerEntity().apply {
                                        fullName = data.fullName
                                        gender = data.gender
                                        course = data.course
                                        difficulty = data.difficulty
                                        birthDay = data.birthDay
                                        birthMonth = data.birthMonth
                                        birthYear = data.birthYear
                                        zodiacSign = data.zodiacSign
                                    })
                                }
                                selectedPlayerId = id
                                refresh()
                            } catch (e: Exception) { error = e.message }
                        }
                    })
                    1 -> Column(Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Выберите игрока", style = MaterialTheme.typography.titleLarge)
                        if (players.isEmpty()) Text("Сначала зарегистрируйте игрока во вкладке «Игрок»")
                        players.forEach { player ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = player.id == selectedPlayerId, onClick = { selectedPlayerId = player.id })
                                Text("${player.fullName} (сложность ${player.difficulty})")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { playing = true }, enabled = players.any { it.id == selectedPlayerId }) { Text("Начать игру") }
                    }
                    2 -> GameSettingsScreen(settings = settings, onSaveSettings = { settings = it })
                    3 -> RecordsScreen(records)
                    4 -> RulesScreen()
                    5 -> AuthorsScreen()
                }
            }
        }
    }
}
