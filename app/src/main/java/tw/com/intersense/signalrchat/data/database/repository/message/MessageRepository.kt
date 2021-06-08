package tw.com.intersense.signalrchat.data.database.repository.message

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tw.com.intersense.signalrchat.data.database.repository.RepoResult

class MessageRepository internal constructor(
    private val dao: MessageDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)  {

    fun observeMessages(productId: Int, askerPhoneId: String): LiveData<List<Message>> {
        return dao.observeMessages(productId,askerPhoneId)
    }

    suspend fun getListMessage(): RepoResult<List<Message>> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(dao.getAll())
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    suspend fun saveMessages(vararg messages: Message)= withContext(ioDispatcher) {
        try {
            dao.insertAll(*messages)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }
}