package ie.setu.config

import ie.setu.controllers.HealthTrackerController
import io.javalin.Javalin
import io.javalin.config.JavalinConfig

class ServerConfig {
    fun startJavalinService(): Javalin {
        val app = Javalin.create { config ->
            config.routes.exception(Exception::class.java) { e, ctx ->
                e.printStackTrace()
            }

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
        config.routes.get("/api/users", HealthTrackerController::getAllUsers)
        config.routes.get("/api/users/{user-id}", HealthTrackerController::getUserById)
        config.routes.get("/api/users/email/{email}", HealthTrackerController::getUserByEmail)
        config.routes.post("/api/users", HealthTrackerController::addUser)
        config.routes.delete("/api/users/{user-id}", HealthTrackerController::deleteUserById)
        config.routes.patch("/api/users/{user-id}", HealthTrackerController::updateUser)
    }
}