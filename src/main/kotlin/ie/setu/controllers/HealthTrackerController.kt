package ie.setu.controllers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.User
import ie.setu.domain.repository.UserDAO
import io.javalin.http.Context

// object means only one instance of this class will be created and it will be a singleton
object HealthTrackerController {
    private val userDAO = UserDAO()

    fun getAllUsers(ctx: Context) {
        ctx.json(userDAO.getAllUsers()) // returns all users in JSON format
    }

    fun getUserById(ctx: Context) {
        val user = userDAO.findUserById(ctx.pathParam("user-id").toInt())
        if (user != null) {
            ctx.json(user)
        } else {
            ctx.status(404).json("User not found")
        }
    }

    fun getUserByEmail(ctx: Context) {
        val user = userDAO.findUserByEmail(ctx.pathParam("email"))
        if (user != null) {
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
        userDAO.deleteUserById(userId)
        ctx.status(204)
    }

    fun updateUser(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val mapper = jacksonObjectMapper()
        val updatedUser = mapper.readValue<User>(ctx.body())
        userDAO.updateUser(userId, updatedUser)
        ctx.status(200).json(updatedUser.copy(id = userId))
    }
}