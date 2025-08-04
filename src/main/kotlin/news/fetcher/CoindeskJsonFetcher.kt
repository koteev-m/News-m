package news.fetcher

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import news.data.NewsItem
import java.security.MessageDigest
import java.time.Instant

class CoindeskJsonFetcher(
    private val client: HttpClient = HttpClient(CIO)
) : NewsFetcher {

    override suspend fun fetch(): List<NewsItem> {
        val json = client.get(URL).bodyAsText()
        val response = Json.decodeFromString<CoinDeskResponse>(json)
        val usd = response.bpi["USD"] ?: return emptyList()
        val title = "Bitcoin price is ${usd.rate} USD"
        val link = "https://www.coindesk.com/price/bitcoin"
        val publishedAt = runCatching {
            Instant.parse(response.time.updatedISO).toEpochMilli()
        }.getOrElse { System.currentTimeMillis() }
        val source = "CoinDesk"
        return listOf(
            NewsItem(
                title = title,
                link = link,
                publishedAt = publishedAt,
                source = source,
                hash = generateHash(source, title, link)
            )
        )
    }

    @Serializable
    private data class CoinDeskResponse(
        val time: Time,
        val bpi: Map<String, Currency>
    )

    @Serializable
    private data class Time(
        @SerialName("updatedISO") val updatedISO: String
    )

    @Serializable
    private data class Currency(
        val code: String,
        val rate: String,
        @SerialName("rate_float") val rateFloat: Double,
        val description: String
    )

    private fun generateHash(vararg values: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(values.joinToString("|").toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val URL = "https://api.coindesk.com/v1/bpi/currentprice.json"
    }
}

