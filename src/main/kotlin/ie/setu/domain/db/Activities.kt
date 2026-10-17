package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Activities : Table("activities") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val description = varchar("description", 100)
    val duration = integer("duration")
    val calories = integer("calories")
    val started = varchar("started", 50)

    override val primaryKey = PrimaryKey(id, name = "PK_Activity_ID")
}