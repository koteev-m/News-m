package news.data

import org.jetbrains.exposed.sql.Table

object NewsTable : Table("news") {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val link = text("link")
    val publishedAt = long("published_at")
    val sourceName = text("source")
    val hash = text("hash").uniqueIndex()

    override val primaryKey = PrimaryKey(id)
}
