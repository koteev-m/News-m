package news

import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import news.data.DatabaseFactory
import news.data.NewsItem
import news.data.NewsRepository
import news.fetcher.CoindeskJsonFetcher
import news.fetcher.NewsFetcher
import news.fetcher.RssFetcher

fun main() = runBlocking {
    val env = dotenv()
    DatabaseFactory.init()

    val token = env["TELEGRAM_BOT_TOKEN"] ?: error("TELEGRAM_BOT_TOKEN not set")
    val channelId = env["CHANNEL_ID"] ?: error("CHANNEL_ID not set")
    val testMode = env["TEST_MODE"]?.lowercase() == "true"

    val postingChannel = Channel<NewsItem>(Channel.UNLIMITED)
    val repository = NewsRepository()
    val fetchers: List<NewsFetcher> = listOf(
        CoindeskJsonFetcher(),
        RssFetcher("https://news.ycombinator.com/rss", "HackerNews")
    )

    val scheduler = Scheduler(fetchers, repository, postingChannel)
    val poster = TelegramPoster(token, channelId)

    val posterJob = poster.start(postingChannel)
    val schedulerJob = scheduler.start()

    if (testMode) {
        poster.post("Hello, World!")
    }

    joinAll(posterJob, schedulerJob)
}

