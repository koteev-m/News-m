package news

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import news.data.NewsItem

class TelegramPoster(
    private val token: String,
    private val channelId: String,
    private val client: HttpClient = HttpClient(CIO),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    fun start(source: ReceiveChannel<NewsItem>) = scope.launch {
        for (item in source) {
            post("${item.title}\n${item.link}")
        }
    }

    suspend fun post(message: String) {
        client.post("$BASE_URL$token/sendMessage") {
            parameter("chat_id", channelId)
            parameter("text", message)
        }
    }

    companion object {
        private const val BASE_URL = "https://api.telegram.org/bot"
    }
}

