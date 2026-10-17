package ie.setu.domain

data class Activity(
    var id: Int,
    var description: String,
    var duration: Int, // Duration in minutes
    var calories: Int,
    var started: String, // Start time in ISO 8601 format
    var userId: Int // Foreign key to the User entity
)