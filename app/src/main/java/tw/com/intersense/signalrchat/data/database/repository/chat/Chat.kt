package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["ProductId","AskerPhoneId"])
data class Chat(

    val ProductId: Int,

    val AskerPhoneId: String,

    val AskerName: String,

    val AskerImageLink: String?,

    val ProductName: String,

    val Price: Int,

    val ProductImagesString: String?,

    val OwnerPhoneId: String,

    val OwnerName: String,

    val OwnerImageLink: String,

    val LastMessageText: String?,

    val LastMessageType: String?,

    val LastMessageTime: Long?,

    val NotReadCount: Int,

    )

