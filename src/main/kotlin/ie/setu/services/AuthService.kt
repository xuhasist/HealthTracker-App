package ie.setu.services

import ie.setu.domain.AuthResponse
import ie.setu.domain.LoginRequest
import ie.setu.domain.RegisterRequest
import ie.setu.domain.User
import ie.setu.domain.repository.SessionDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.security.PasswordHasher
import ie.setu.security.TokenService
import io.javalin.http.BadRequestResponse
import io.javalin.http.ConflictResponse
import io.javalin.http.UnauthorizedResponse
import java.time.Instant
import java.util.Locale

class AuthService(
    private val users: UserDAO = UserDAO(),
    private val sessions: SessionDAO = SessionDAO()
) {
    fun register(body: RegisterRequest): User {
        val name = body.name.trim()
        val email = normalizeEmail(body.email)
        validateName(name)
        validateEmail(email)
        validatePassword(body.password)
        if (users.findUserByEmail(email) != null) throw ConflictResponse("Email already registered")
        // Database also enforces unique lower(email), protecting concurrent registrations.
        return users.createUser(name, email, PasswordHasher.hash(body.password))
    }

    fun login(body: LoginRequest): AuthResponse {
        val email = normalizeEmail(body.email)
        val credentials = users.findCredentialsByEmail(email)
        // Identical result for unknown accounts and wrong passwords prevents enumeration.
        if (credentials == null || !PasswordHasher.verify(body.password, credentials.second)) {
            throw UnauthorizedResponse("Invalid email or password")
        }
        val now = System.currentTimeMillis()
        val expiresAt = now + 24L * 60 * 60 * 1000
        val token = TokenService.newToken()
        sessions.create(TokenService.digest(token), credentials.first.id, now, expiresAt)
        return AuthResponse(
            token = token,
            expiresAt = Instant.ofEpochMilli(expiresAt).toString(),
            user = credentials.first
        )
    }

    fun logout(tokenHash: String) {
        sessions.revoke(tokenHash, System.currentTimeMillis())
    }

    fun updateProfile(id: Int, name: String, email: String): User? {
        val cleanName = name.trim()
        val cleanEmail = normalizeEmail(email)
        validateName(cleanName)
        validateEmail(cleanEmail)
        val other = users.findUserByEmail(cleanEmail)
        if (other != null && other.id != id) throw ConflictResponse("Email already registered")
        return users.updateOwnProfile(id, cleanName, cleanEmail)
    }

    private fun normalizeEmail(email: String) = email.trim().lowercase(Locale.ROOT)
    private fun validateName(name: String) {
        if (name.isBlank() || name.length > 100) throw BadRequestResponse("Name must be 1-100 characters")
    }

    private fun validateEmail(email: String) {
        if (email.length > 255 || !Regex("""^[^@\s]+@[^@\s]+\.[^@\s]+$""").matches(email)) {
            throw BadRequestResponse("Valid email is required")
        }
    }

    private fun validatePassword(password: String) {
        // BCrypt input limit is 72 bytes; avoid silent truncation.
        val bytes = password.toByteArray(Charsets.UTF_8).size
        if (bytes < 12 || bytes > 72) throw BadRequestResponse("Password must be 12-72 UTF-8 bytes")
    }
}
