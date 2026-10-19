package ie.setu.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object TokenService {
    private val random = SecureRandom()
    private val tokenPattern = Regex("^[A-Za-z0-9_-]{43}$")

    fun newToken(): String {
        val bytes = ByteArray(32) // 256-bit unpredictability
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    fun isValidFormat(token: String): Boolean = tokenPattern.matches(token)

    // Hashes the token using SHA-256
    fun digest(token: String): String = MessageDigest.getInstance("SHA-256")
        .digest(token.toByteArray(Charsets.UTF_8))
        .joinToString("") { "%02x".format(it) }
}
