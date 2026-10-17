package ie.setu.config

import ie.setu.controllers.ActivityController
import ie.setu.controllers.AuthController
import ie.setu.security.AuthMiddleware
import ie.setu.controllers.UserController
import io.javalin.Javalin
import io.javalin.config.JavalinConfig

class ServerConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create { config ->
            // Javalin handles HTTP errors (401/403/404) correctly by default.
            // The old generic Exception handler swallowed their status codes.

            config.routes.error(404) { ctx ->
                ctx.json("404 - Not Found")
            }
            registerRoutes(config)
        }.start(getRemoteAssignedPort())

        return app
    }

    private fun getRemoteAssignedPort(): Int {
        val remotePort = System.getenv("PORT")
        return if (remotePort != null) {
            Integer.parseInt(remotePort)
        } else 8080
    }


    private fun registerRoutes(config: JavalinConfig) {
        config.routes.before("/api/*", AuthMiddleware::authenticate)
        config.routes.post("/api/auth/register", AuthController::register)
        config.routes.post("/api/auth/login", AuthController::login)
        config.routes.post("/api/auth/logout", AuthController::logout)

        //config.routes.get("/api/users", UserController::getAllUsers)
        config.routes.get("/api/users/me", UserController::getMyProfile)
        config.routes.get("/api/users/{user-id}", UserController::getUserById)
        config.routes.get("/api/users/email/{email}", UserController::getUserByEmail)
        //config.routes.post("/api/users", UserController::addUser)
        config.routes.delete("/api/users/{user-id}", UserController::deleteUserById)
        config.routes.patch("/api/users/{user-id}", UserController::updateUser)

        config.routes.get("/api/activities", ActivityController::getAllActivities)
        config.routes.get("/api/activities/{activity-id}", ActivityController::getActivityById)
        config.routes.post("/api/activities", ActivityController::addActivity)
        config.routes.patch("/api/activities/{activity-id}", ActivityController::updateActivity)
        config.routes.delete("/api/activities/{activity-id}", ActivityController::deleteActivityById)
        config.routes.get("/api/users/{user-id}/activities", ActivityController::getActivitiesByUserId)
        config.routes.delete("/api/users/{user-id}/activities", ActivityController::deleteActivitiesByUserId)
    }
}