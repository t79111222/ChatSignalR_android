package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.room.Entity
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.user.User

@Entity(primaryKeys = ["chatId", "userId"])
data class ChatUser(
    val chatId: Int,
    val userId: String
)
