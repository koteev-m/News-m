package news

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel
import news.data.NewsItem
import news.data.NewsRepository
import news.fetcher.NewsFetcher

class Scheduler(
    private val fetchers: List<NewsFetcher>,
    private val repository: NewsRepository,
    private val postingQueue: SendChannel<NewsItem>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    fun start() = scope.launch {
        while (isActive) {
            runFetchers()
            delay(INTERVAL_MILLIS)
        }
    }

    private suspend fun runFetchers() = coroutineScope {
        fetchers.map { fetcher ->
            async { fetcher.fetch() }
        }.awaitAll().flatten().forEach { item ->
            if (!repository.isDuplicate(item.hash)) {
                repository.save(item)
                postingQueue.send(item)
            }
        }
    }

    companion object {
        private const val INTERVAL_MILLIS = 15 * 60 * 1000L
    }
}

