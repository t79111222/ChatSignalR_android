package tw.com.intersense.signalrchat.data.network

data class LoginResponse(
    val IsAuthenticated : Boolean,
    val AccessToken: String,
    val ExpiresIn: String,
    val UserName: String,
    val UserId: String,
    val ResultCode: Int,
    val Message: String
)
