package ie.setu.controllers

import ie.setu.domain.repository.ActivityDAO
import ie.setu.security.AuthMiddleware
import io.javalin.http.Context

object ActivityController {
    private val activityDAO = ActivityDAO()

    fun getAllActivities(ctx: Context) {
        ctx.json(activityDAO.findActivityByUserId(AuthMiddleware.userId(ctx)))
    }

    fun getActivityById(ctx: Context) {
        val activity = activityDAO.findActivityById(ctx.pathParam("activity-id").toInt())
        if (activity != null && activity.userId == AuthMiddleware.userId(ctx)) {
            ctx.json(activity)
        } else {
            ctx.status(404).json("Activity not found")
        }
    }

    fun addActivity(ctx: Context) {
        val activity = ctx.bodyAsClass(ie.setu.domain.Activity::class.java)
        // Ignore userId in JSON; ownership comes from the authenticated session.
        val savedActivity = activityDAO.saveActivity(activity.copy(userId = AuthMiddleware.userId(ctx)))
        ctx.status(201).json(savedActivity)
    }

    fun updateActivity(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()
        val updatedActivity = ctx.bodyAsClass(ie.setu.domain.Activity::class.java)
        // The DAO also checks the owner in its SQL WHERE clause.
        val result =
            activityDAO.updateActivity(updatedActivity.copy(id = activityId, userId = AuthMiddleware.userId(ctx)))
        if (result != null) {
            ctx.status(200).json(result)
        } else {
            ctx.status(404).json("Activity not found")
        }
    }

    fun deleteActivityById(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()
        val deleted = activityDAO.deleteActivityById(activityId, AuthMiddleware.userId(ctx))
        if (deleted > 0) ctx.status(204) else ctx.status(404).json("Activity not found")
    }

    fun getActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        AuthMiddleware.ownResource(ctx, userId)
        val activities = activityDAO.findActivityByUserId(userId)

        ctx.status(200).json(activities)
    }

    fun deleteActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        AuthMiddleware.ownResource(ctx, userId)
        activityDAO.deleteActivitiesByUserId(userId)
        ctx.status(204)
    }
}