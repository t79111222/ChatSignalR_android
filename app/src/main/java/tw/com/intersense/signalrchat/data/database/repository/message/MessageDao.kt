package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Query("SELECT * FROM Message WHERE ProductId = :productId AND AskerPhoneId = :askerPhoneId ORDER BY CreateTime desc")
    fun observeMessages(productId: Int, askerPhoneId: String): LiveData<List<Message>>

    @Query("SELECT * FROM Message")
    suspend fun getAll(): List<Message>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg messages: Message)

}