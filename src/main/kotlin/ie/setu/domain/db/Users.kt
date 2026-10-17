package ie.setu.domain.db

import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 255)

    // Nullable to migrate existing lab users safely; legacy accounts require a password reset.
    val passwordHash = varchar("password_hash", 60).nullable()

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}
