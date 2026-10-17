package ie.setu.security

import ie.setu.domain.repository.SessionDAO
import io.javalin.http.Context
import io.javalin.http.ForbiddenResponse
import io.javalin.http.UnauthorizedResponse

object AuthMiddleware {
    private val sessions = SessionDAO()
    private val publicEndpoints =
        setOf("/api/auth/register", "/api/auth/login")    // no need to authenticate for these endpoints

    // checks if the request is authenticated and finds the user ID and token hash for the request
    fun authenticate(ctx: Context) {
        if (ctx.path() in publicEndpoints) return
        val token = bearerToken(ctx)
        val session = sessions.findActive(TokenService.digest(token), System.currentTimeMillis())
            ?: throw UnauthorizedResponse("Invalid or expired session")
        ctx.attribute("authenticatedUserId", session.userId)
        ctx.attribute("authenticatedTokenHash", TokenService.digest(token))
    }

    private fun bearerToken(ctx: Context): String {
        val parts = ctx.header("Authorization")?.split(' ') ?: throw UnauthorizedResponse("Bearer token required")
        if (parts.size != 2 || !parts[0].equals("Bearer", ignoreCase = true) || !TokenService.isValidFormat(parts[1])) {
            throw UnauthorizedResponse("Bearer token required")
        }
        return parts[1]
    }

    fun userId(ctx: Context): Int = ctx.attribute<Int>("authenticatedUserId")
        ?: throw UnauthorizedResponse("Authentication required")

    // checks if the authenticated user is the owner of the resource being accessed
    fun ownResource(ctx: Context, requestedId: Int) {
        if (userId(ctx) != requestedId) throw ForbiddenResponse("Access denied")
    }

    fun currentTokenHash(ctx: Context): String = ctx.attribute<String>("authenticatedTokenHash")
        ?: throw UnauthorizedResponse("Authentication required")
}
