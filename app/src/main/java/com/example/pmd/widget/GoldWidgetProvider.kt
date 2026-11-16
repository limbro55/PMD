package com.example.pmd.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.pmd.MainActivity
import com.example.pmd.R
import com.example.pmd.gold.GoldRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

class GoldWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        super.onUpdate(context, manager, ids)
        if (ids.isEmpty()) return

        val pendingResult = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                val quote = withTimeoutOrNull(8_000L) {
                    GoldRepository.latestOrCached(appContext)
                } ?: GoldRepository.cached(appContext)

                for (id in ids) {
                    val views = RemoteViews(appContext.packageName, R.layout.widget_gold)
                    views.setTextViewText(
                        R.id.gold_price,
                        quote?.let { String.format(Locale("ru", "RU"), "%,.2f ₽/г", it.rubPerGram) }
                            ?: "Нет данных"
                    )
                    views.setTextViewText(
                        R.id.gold_date,
                        quote?.date?.let { "ЦБ РФ · $it" } ?: "Проверь интернет"
                    )
                    val intent = Intent(appContext, MainActivity::class.java)
                    val openApp = PendingIntent.getActivity(
                        appContext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    views.setOnClickPendingIntent(R.id.gold_widget_root, openApp)
                    manager.updateAppWidget(id, views)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
