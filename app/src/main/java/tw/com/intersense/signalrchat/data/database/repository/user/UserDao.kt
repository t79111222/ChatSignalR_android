package tw.com.intersense.signalrchat.data.database.repository.user

import androidx.lifecycle.LiveData
import androidx.room.*
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg users: User)


    @Query("SELECT * FROM User WHERE id = :id LIMIT 1")
    suspend fun getUser(id: String): User
}