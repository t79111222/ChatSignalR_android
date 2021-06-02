package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Message(
    @PrimaryKey
    val id: Int,

    val chatId: Int,

    val userId: String,

    val text: String,

    val time: Long,

    val type: Int
)
