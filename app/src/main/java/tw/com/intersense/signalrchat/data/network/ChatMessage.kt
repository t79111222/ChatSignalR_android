package tw.com.intersense.signalrchat.data.network

import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.message.Message

data class ChatMessage(
    val chat: Chat,
    val listMessage: Array<Message>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatMessage

        if (chat != other.chat) return false
        if (!listMessage.contentEquals(other.listMessage)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chat.hashCode()
        result = 31 * result + listMessage.contentHashCode()
        return result
    }
}
