package news.data

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class NewsRepository {
    fun isDuplicate(hash: String): Boolean = transaction {
        !NewsTable.select(NewsTable.hash eq hash).empty()
    }

    fun save(item: NewsItem) = transaction {
        NewsTable.insert {
            it[title] = item.title
            it[link] = item.link
            it[publishedAt] = item.publishedAt
            it[sourceName] = item.source
            it[hash] = item.hash
        }
    }
}
