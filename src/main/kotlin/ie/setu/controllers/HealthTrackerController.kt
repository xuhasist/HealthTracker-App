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
        }
    }

    fun addUser(ctx: Context) {
        val mapper = jacksonObjectMapper()
        val user = mapper.readValue<User>(ctx.body())
        userDAO.saveUser(user)
        ctx.status(201).json(user)
    }
}