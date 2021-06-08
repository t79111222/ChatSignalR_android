package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["productId","askerPhoneId"])
data class Chat(

    val productId: Int,

    val askerPhoneId: String,

    val askerName: String,

    val askerImageLink: String?,

    val productName: String,

    val price: Int,

    val productImagesString: String?,

    val ownerPhoneId: String,

    val ownerName: String,

    val ownerImageLink: String,

    val lastMessageText: String?,

    val lastMessageType: String?,

    val lastMessageTime: Long?,

    val notReadCount: Int,

    )

