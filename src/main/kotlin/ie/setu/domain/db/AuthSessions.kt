package ie.setu.domain.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object AuthSessions : Table("auth_sessions") {
    // session is for a single login session, identified by a unique token hash.
    // It is associated with a user and has timestamps for creation, expiration, and revocation.

    val tokenHash = varchar("token_hash", 64)
    val userId = integer("user_id").references(
        Users.id,
        onDelete = ReferenceOption.CASCADE
    )    // delete sessions when user is deleted
    val createdAt = long("created_at")
    val expiresAt = long("expires_at")
    val revokedAt = long("revoked_at").nullable()   // when user logs out or session is revoked

    override val primaryKey = PrimaryKey(tokenHash, name = "PK_Auth_Session")
}
