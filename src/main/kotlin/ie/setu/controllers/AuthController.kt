package ie.setu.controllers

import ie.setu.domain.LoginRequest
import ie.setu.domain.RegisterRequest
import ie.setu.security.AuthMiddleware
import ie.setu.services.AuthService
import io.javalin.http.Context

object AuthController {
    private val auth = AuthService()

    fun register(ctx: Context) {
        val request = ctx.bodyAsClass(RegisterRequest::class.java)
        ctx.status(201).json(auth.register(request))
    }

    fun login(ctx: Context) {
        val request = ctx.bodyAsClass(LoginRequest::class.java)
        ctx.json(auth.login(request))
    }

    fun logout(ctx: Context) {
        auth.logout(AuthMiddleware.currentTokenHash(ctx))
        ctx.status(204)
    }
}
