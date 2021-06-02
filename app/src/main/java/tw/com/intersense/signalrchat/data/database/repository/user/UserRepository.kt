package tw.com.intersense.signalrchat.data.database.repository.user

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tw.com.intersense.signalrchat.data.database.repository.RepoResult
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.message.Message

class UserRepository internal constructor(
    private val dao: UserDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)  {

    suspend fun saveUsers(vararg users: User)= withContext(ioDispatcher) {
        try {
            dao.insertAll(*users)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun getUser(id: String):RepoResult<User> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success( dao.getUser(id))
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }
}