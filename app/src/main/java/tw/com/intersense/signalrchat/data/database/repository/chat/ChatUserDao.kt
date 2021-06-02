package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.room.*

@Dao
interface ChatUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chatUsers: ChatUser)

    @Query("SELECT * FROM ChatUser WHERE chatId = :chatId ")
    suspend fun getChatUser(chatId: Int): List<ChatUser>

}