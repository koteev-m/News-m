package news.data

data class NewsItem(
    val id: Int? = null,
    val title: String,
    val link: String,
    val publishedAt: Long,
    val source: String,
    val hash: String
)
