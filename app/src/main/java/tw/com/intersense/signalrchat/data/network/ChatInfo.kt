package tw.com.intersense.signalrchat.data.network

import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.data.database.repository.user.User

data class ChatInfo(
    val id: Int,
    val name: String,
    val listMessage: Array<Message>,
    val listUser: Array<User>,
    val product:Product
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ChatInfo

        if (id != other.id) return false
        if (name != other.name) return false
        if (!listMessage.contentEquals(other.listMessage)) return false
        if (!listUser.contentEquals(other.listUser)) return false
        if (product != other.product) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + listMessage.contentHashCode()
        result = 31 * result + listUser.contentHashCode()
        result = 31 * result + product.hashCode()
        return result
    }
}

