package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ChatDao {

    @Query("SELECT * FROM Chat ORDER " + "BY lastTime DESC")
    fun observeChats(): LiveData<List<Chat>>

    @Query("SELECT * FROM Chat ORDER " + "BY lastTime DESC")
    suspend fun getAll(): List<Chat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg chats: Chat)


    @Query("UPDATE Chat SET lastMessage = :lastMessage, lastTime = :lastTime WHERE id = :id")
    suspend fun updateLastMessage(id: Int, lastMessage: String, lastTime: Long): Int

    @Query("SELECT * FROM Chat WHERE id = :id LIMIT 1")
    suspend fun getChat(id: Int): Chat


    @Query("SELECT * FROM Chat WHERE productId = :id LIMIT 1")
    suspend fun getChatByProductId(id: Int): Chat
}