package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Message(
    @PrimaryKey
    val messageId: Int,
    
    val askerPhoneId: String,

    val productId: Int,

    val messageText: String,

    val speakerPhoneId: String,

    val createTime: Long,

    val messageType: String
)
