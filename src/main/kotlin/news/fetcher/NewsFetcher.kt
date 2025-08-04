package news.fetcher

import news.data.NewsItem

interface NewsFetcher {
    suspend fun fetch(): List<NewsItem>
}

