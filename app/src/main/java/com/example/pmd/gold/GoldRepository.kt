package com.example.pmd.gold

import android.content.Context
import android.util.Xml
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.ResponseBody
import org.xmlpull.v1.XmlPullParser
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

/** Bank of Russia's accounting price of gold (RUB per gram), not a retail quote. */
data class GoldQuote(val rubPerGram: Double, val date: String) {
    // Explicit game rule: 1 point for 100 RUB per gram, rounded to the nearest point.
    val points: Int get() = (rubPerGram / 100.0).roundToInt().coerceIn(1, 10_000)
}

/** CBR XML service: https://www.cbr.ru/scripts/xml_metall.asp */
private interface CbrMetalsApi {
    @GET("scripts/xml_metall.asp")
    suspend fun history(
        @Query("date_req1") from: String,
        @Query("date_req2") to: String
    ): ResponseBody
}

object GoldRepository {
    private const val CACHE = "cbr_gold_quote"
    private val api by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.cbr.ru/")
            .build()
            .create(CbrMetalsApi::class.java)
    }

    /** Read last successfully downloaded quote. No invented default price. */
    fun cached(context: Context): GoldQuote? {
        val prefs = context.getSharedPreferences(CACHE, Context.MODE_PRIVATE)
        val price = prefs.getString("price", null)?.toDoubleOrNull() ?: return null
        val date = prefs.getString("date", null) ?: return null
        return if (price.isFinite() && price > 0 && date.isNotBlank()) GoldQuote(price, date) else null
    }

    /** Downloads latest available rate from the last 14 days (includes weekends). */
    suspend fun load(context: Context): GoldQuote = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val endDate = format.format(cal.time)
        cal.add(Calendar.DAY_OF_YEAR, -14)
        val startDate = format.format(cal.time)

        // Closing ResponseBody and InputStream avoids leaking network connections.
        val quote = withTimeout(10_000L) {
            api.history(startDate, endDate).use { body ->
                body.byteStream().use { stream ->
                    // CBR delivers XML in Windows-1251 encoding.
                    val parser = Xml.newPullParser()
                    parser.setInput(stream, "windows-1251")
                    parseGold(parser)
                }
            }
        }
        context.getSharedPreferences(CACHE, Context.MODE_PRIVATE).edit()
            .putString("price", quote.rubPerGram.toString())
            .putString("date", quote.date)
            .apply()
        quote
    }

    /** If offline, keep the actual last downloaded quotation (not a made-up rate). */
    suspend fun latestOrCached(context: Context): GoldQuote? {
        return try {
            load(context)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            cached(context)
        }
    }

    /** XML: <Record Date="dd.MM.yyyy" Code="1"><Buy>...</Buy><Sell>...</Sell></Record>.
     *  Code=1 means gold; Sell is the reported accounting price used for the game.
     */
    private fun parseGold(parser: XmlPullParser): GoldQuote {
        var latest: GoldQuote? = null
        var latestDateKey = ""
        var isGold = false
        var recordDate = ""
        var sell: String? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> when (parser.name) {
                    "Record" -> {
                        isGold = parser.getAttributeValue(null, "Code") == "1"
                        recordDate = parser.getAttributeValue(null, "Date") ?: ""
                        sell = null
                    }
                    "Sell" -> if (isGold) sell = parser.nextText()
                }
                XmlPullParser.END_TAG -> if (parser.name == "Record" && isGold) {
                    val parts = recordDate.split('.')
                    val price = sell?.trim()?.replace(',', '.')?.toDoubleOrNull()
                    if (parts.size == 3 && parts[0].length == 2 && parts[1].length == 2 &&
                        parts[2].length == 4 && price != null && price.isFinite() && price > 0) {
                        val dateKey = parts[2] + parts[1] + parts[0] // sortable yyyyMMdd
                        if (dateKey > latestDateKey) {
                            latestDateKey = dateKey
                            latest = GoldQuote(price, recordDate)
                        }
                    }
                    isGold = false
                }
            }
            event = parser.next()
        }
        return latest ?: error("CBR did not provide a gold quote for the requested period")
    }
}
