package tw.com.intersense.signalrchat.data.network

data class LoginResponse(
    val isAuthenticated : Boolean,
    val accessToken: String,
    val expiresIn: String,
    val userName: String,
    val userId: String,
    val resultCode: Int,
    val message: String
)
