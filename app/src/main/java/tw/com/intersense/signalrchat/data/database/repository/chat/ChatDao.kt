package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao {

    @Query("SELECT * FROM Chat ORDER " + "BY lastMessageTime DESC")
    fun observeChats(): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat ORDER " + "BY lastMessageTime DESC")
    suspend fun getAll(): List<Chat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chats: Chat)


    @Query("UPDATE Chat SET lastMessageText = :lastMessageText, lastMessageType = :lastMessageType,lastMessageTime = :lastTime,  notReadCount = :notReadCount WHERE productId = :productId AND askerPhoneId = :askerPhoneId")
    suspend fun updateLastMessage(productId: Int, askerPhoneId: String, lastMessageText: String, lastMessageType: String, lastTime: Long, notReadCount: Int): Int

    @Query("SELECT * FROM Chat WHERE productId = :productId AND askerPhoneId = :askerPhoneId LIMIT 1")
    suspend fun getChat(productId: Int, askerPhoneId: String): Chat

    @Query("UPDATE Chat SET notReadCount = 0 WHERE productId = :productId AND askerPhoneId = :askerPhoneId")
    suspend fun setRead(productId: Int, askerPhoneId: String): Int

}