package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao {

    @Query("SELECT * FROM Chat ORDER " + "BY LastMessageTime DESC")
    fun observeChats(): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat ORDER " + "BY LastMessageTime DESC")
    suspend fun getAll(): List<Chat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chats: Chat)


    @Query("UPDATE Chat SET LastMessageText = :lastMessageText, LastMessageType = :lastMessageType,LastMessageTime = :lastTime,  NotReadCount = :notReadCount WHERE ProductId = :productId AND AskerPhoneId = :askerPhoneId")
    suspend fun updateLastMessage(productId: Int, askerPhoneId: String, lastMessageText: String, lastMessageType: String, lastTime: Long, notReadCount: Int): Int

    @Query("SELECT * FROM Chat WHERE ProductId = :productId AND AskerPhoneId = :askerPhoneId LIMIT 1")
    suspend fun getChat(productId: Int, askerPhoneId: String): Chat

    @Query("UPDATE Chat SET NotReadCount = 0 WHERE ProductId = :productId AND AskerPhoneId = :askerPhoneId")
    suspend fun setRead(productId: Int, askerPhoneId: String): Int

}