package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM Message WHERE chatId = :chatId ORDER BY time")
    fun observeMessages(chatId: Int): LiveData<List<Message>>

    @Query("SELECT * FROM Message")
    suspend fun getAll(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg messages: Message)

}