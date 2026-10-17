package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import ie.setu.security.AuthMiddleware
import ie.setu.services.AuthService
import io.javalin.http.Context

// object means only one instance of this class will be created and it will be a singleton
object UserController {
    private val userDAO = UserDAO()
    private val authService = AuthService()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDAO.getAllUsers()) // returns all users in JSON format
    }

    fun getMyProfile(ctx: Context) {
        val user = userDAO.findUserById(AuthMiddleware.userId(ctx))
        if (user != null) ctx.json(user) else ctx.status(404).json("User not found")
    }

    fun getUserById(ctx: Context) {
        AuthMiddleware.ownResource(ctx, ctx.pathParam("user-id").toInt())   // only allow access to own profile
        val user = userDAO.findUserById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        } else {
            ctx.status(404).json("User not found")
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDAO.findUserByEmail(ctx.pathParam("email"))
        // Return 404 for other people's email addresses.
        if (user != null && user.id == AuthMiddleware.userId(ctx)) {
            ctx.json(user)
        } else {
            ctx.status(404).json("User not found")
        }
    }

    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        val savedUser = userDAO.saveUser(user)
        ctx.status(201).json(savedUser)
    }

    fun deleteUserById(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        AuthMiddleware.ownResource(ctx, userId)
        userDAO.deleteUserById(userId)
        ctx.status(204)
    }

    fun updateUser(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        AuthMiddleware.ownResource(ctx, userId)
        val mapper = jacksonObjectMapper()
        val updatedUser = mapper.readValue<User>(ctx.body())
        // Use the existing AuthService to validate and normalise email on profile update.
        val user = authService.updateProfile(userId, updatedUser.name, updatedUser.email)
        if (user != null) ctx.status(200).json(user) else ctx.status(404).json("User not found")
    }
}