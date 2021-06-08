package tw.com.intersense.signalrchat.data.database.repository.chat

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import tw.com.intersense.signalrchat.data.database.repository.RepoResult
import tw.com.intersense.signalrchat.data.database.repository.message.Message

class ChatRepository internal constructor(
    private val chatDao: ChatDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
)  {

    fun observeChats(): LiveData<List<Chat>> {
        return chatDao.observeChats()
    }

    suspend fun getListChat(): RepoResult<List<Chat>> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(chatDao.getAll())
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    suspend fun saveChats(vararg chats: Chat)= withContext(ioDispatcher) {
        try {
            chatDao.insertAll(*chats)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun updateLastMessage(m: Message)= withContext(ioDispatcher) {
        try {
            var c = chatDao.getChat(m.ProductId, m.AskerPhoneId)
            c?.let {

                chatDao.updateLastMessage(m.ProductId, m.AskerPhoneId, m.MessageText, m.MessageType, m.CreateTime, it.NotReadCount+1)
            }
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun setRead(productId: Int, askerPhoneId: String)= withContext(ioDispatcher) {
        try {
            chatDao.setRead(productId, askerPhoneId)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun getChat(productId: Int, askerPhoneId: String): RepoResult<Chat> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(chatDao.getChat(productId, askerPhoneId))
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }
}