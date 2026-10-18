package com.example.pmd

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pmd.bd.ScoreRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RecordsScreen(records: List<ScoreRow>) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Таблица рекордов", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        if (records.isEmpty()) Text("Пока нет сыгранных раундов")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            itemsIndexed(records, key = { _, it -> it.id }) { index, r ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${index + 1}. ${r.fullName} — ${r.score} очков", style = MaterialTheme.typography.titleMedium)
                        Text("Сложность: ${r.difficulty} | Попадания: ${r.hits} | Промахи: ${r.misses}")
                        val accuracy = if (r.hits + r.misses == 0) 0.0 else 100.0 * r.hits / (r.hits + r.misses)
                        Text("Точность: ${"%.1f".format(accuracy)}% | Раунд: ${r.durationSeconds} с")
                        Text(SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(r.playedAt)))
                    }
                }
            }
        }
    }
}
