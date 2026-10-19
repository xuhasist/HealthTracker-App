package ie.setu.domain.repository

import ie.setu.domain.db.AuthSessions
import ie.setu.domain.db.Users
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

data class SessionRecord(val userId: Int, val expiresAt: Long, val revokedAt: Long?)

class SessionDAO {
    fun create(tokenHash: String, userId: Int, now: Long, expiresAt: Long) = transaction {
        AuthSessions.insert {
            it[AuthSessions.tokenHash] = tokenHash
            it[AuthSessions.userId] = userId
            it[AuthSessions.createdAt] = now
            it[AuthSessions.expiresAt] = expiresAt
        }
        Unit
    }

    fun findActive(tokenHash: String, now: Long): SessionRecord? = transaction {
        val session = AuthSessions.selectAll().where { AuthSessions.tokenHash eq tokenHash }.firstOrNull()
        val record = session?.let {
            SessionRecord(
                it[AuthSessions.userId],
                it[AuthSessions.expiresAt],
                it[AuthSessions.revokedAt]
            )
        }
        if (record == null || record.revokedAt != null || record.expiresAt <= now) null
        else if (Users.selectAll().where { Users.id eq record.userId }.firstOrNull() == null) null
        else record
    }

    fun revoke(tokenHash: String, now: Long): Boolean = transaction {
        AuthSessions.update({ AuthSessions.tokenHash eq tokenHash }) {
            it[AuthSessions.revokedAt] = now
        } > 0
    }
}
