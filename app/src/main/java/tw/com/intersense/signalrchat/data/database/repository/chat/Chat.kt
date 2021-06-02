package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Chat(
    @PrimaryKey
    val id: Int,

//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    val avatar: ByteArray,

    val name: String?,

//    val unreadCount: Int,

    val lastMessage: String?,

    val lastTime: Long?,

    val productId: Int?,

    val productName: String?,

//    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
//    val productImage: ByteArray,

    val productUserId: String?,

    )

