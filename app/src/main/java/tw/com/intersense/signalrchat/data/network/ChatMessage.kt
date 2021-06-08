package tw.com.intersense.signalrchat.data.network

import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.message.Message

data class ChatMessage(
    val Chat: Chat,
    val ListMessage: Array<Message>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (Chat != other.Chat) return false
        if (!ListMessage.contentEquals(other.ListMessage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Chat.hashCode()
        result = 31 * result + ListMessage.contentHashCode()
        return result
    }
}
