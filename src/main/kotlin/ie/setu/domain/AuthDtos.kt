package ie.setu.domain

data class RegisterRequest(val name: String = "", val email: String = "", val password: String = "")
data class LoginRequest(val email: String = "", val password: String = "")
data class UpdateProfileRequest(val name: String? = null, val email: String? = null)
data class AuthResponse(val token: String, val tokenType: String = "Bearer", val expiresAt: String, val user: User)
data class ActivityInput(
    val description: String = "",
    val duration: Int = -1,
    val calories: Int = -1,
    val started: String = ""
)
