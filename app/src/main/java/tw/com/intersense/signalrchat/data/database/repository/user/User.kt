package tw.com.intersense.signalrchat.data.database.repository.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: String,

    val name: String
)
