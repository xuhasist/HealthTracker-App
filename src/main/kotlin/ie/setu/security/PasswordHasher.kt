package ie.setu.security

import org.mindrot.jbcrypt.BCrypt

object PasswordHasher {
    // BCrypt automatically generates a unique random salt for each password.
    fun hash(password: String): String = BCrypt.hashpw(password, BCrypt.gensalt(12))

    fun verify(password: String, hash: String?): Boolean =
        if (hash.isNullOrBlank()) false
        else try {
            BCrypt.checkpw(password, hash)
        } catch (_: IllegalArgumentException) {
            false
        }
}
