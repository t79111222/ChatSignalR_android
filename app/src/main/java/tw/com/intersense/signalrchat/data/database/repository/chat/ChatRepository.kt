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
    private val chatUserDao: ChatUserDao,
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

    suspend fun saveChatUsers(vararg chatUsers: ChatUser)= withContext(ioDispatcher) {
        try {
            chatUserDao.insertAll(*chatUsers)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun updateLastMessage(m: Message)= withContext(ioDispatcher) {
        try {
            if(chatDao.getChat(m.chatId) != null)
                chatDao.updateLastMessage(m.chatId, m.text, m.time)
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    suspend fun getChat(id: Int): RepoResult<Chat> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(chatDao.getChat(id))
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    suspend fun getChatByProductId(productId: Int): RepoResult<Chat> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(chatDao.getChatByProductId(productId))
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }

    suspend fun getChatUser(chatId: Int): RepoResult<List<ChatUser>> = withContext(ioDispatcher) {
        return@withContext try {
            RepoResult.Success(chatUserDao.getChatUser(chatId))
        } catch (e: Exception) {
            RepoResult.Error(e)
        }
    }
}