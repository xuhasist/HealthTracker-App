package ie.setu.controllers

import ie.setu.domain.repository.ActivityDAO
import io.javalin.http.Context

object ActivityController {
    private val activityDAO = ActivityDAO()

    fun getAllActivities(ctx: Context) {
        ctx.json(activityDAO.getAllActivities())
    }

    fun getActivityById(ctx: Context) {
        val activity = activityDAO.findActivityById(ctx.pathParam("activity-id").toInt())
        if (activity != null) {
            ctx.json(activity)
        } else {
            ctx.status(404).json("Activity not found")
        }
    }

    fun addActivity(ctx: Context) {
        val activity = ctx.bodyAsClass(ie.setu.domain.Activity::class.java)
        val savedActivity = activityDAO.saveActivity(activity)
        ctx.status(201).json(savedActivity)
    }

    fun updateActivity(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()
        val updatedActivity = ctx.bodyAsClass(ie.setu.domain.Activity::class.java)
        val result = activityDAO.updateActivity(updatedActivity.copy(id = activityId))
        if (result != null) {
            ctx.status(200).json(result)
        } else {
            ctx.status(404).json("Activity not found")
        }
    }

    fun deleteActivityById(ctx: Context) {
        val activityId = ctx.pathParam("activity-id").toInt()
        activityDAO.deleteActivityById(activityId)
        ctx.status(204)
    }

    fun getActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        val activities = activityDAO.findActivityByUserId(userId)

        ctx.status(200).json(activities)
    }

    fun deleteActivitiesByUserId(ctx: Context) {
        val userId = ctx.pathParam("user-id").toInt()
        activityDAO.deleteActivitiesByUserId(userId)
        ctx.status(204)
    }
}