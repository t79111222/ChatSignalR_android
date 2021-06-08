package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Message(
    @PrimaryKey
    val MessageId: Int,

    val AskerPhoneId: String,

    val ProductId: Int,

    val MessageText: String,

    val SpeakerPhoneId: String,

    val CreateTime: Long,

    val MessageType: String
)
