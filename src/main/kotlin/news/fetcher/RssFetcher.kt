package news.fetcher

import com.prof18.rssparser.RssParser
import news.data.NewsItem
import java.security.MessageDigest
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RssFetcher(
    private val feedUrl: String,
    private val source: String,
) : NewsFetcher {

    private val parser = RssParser()

    override suspend fun fetch(): List<NewsItem> {
        val channel = parser.parse(feedUrl)
        return channel.items.mapNotNull { item ->
            val title = item.title ?: return@mapNotNull null
            val link = item.link ?: return@mapNotNull null
            val publishedAt = item.pubDate?.let {
                runCatching {
                    ZonedDateTime.parse(it, DateTimeFormatter.RFC_1123_DATE_TIME)
                        .toInstant().toEpochMilli()
                }.getOrElse { System.currentTimeMillis() }
            } ?: System.currentTimeMillis()
            NewsItem(
                title = title,
                link = link,
                publishedAt = publishedAt,
                source = source,
                hash = generateHash(source, title, link)
            )
        }
    }

    private fun generateHash(vararg values: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(values.joinToString("|").toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

