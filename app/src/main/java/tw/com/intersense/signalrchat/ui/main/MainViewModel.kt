package tw.com.intersense.signalrchat.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.com.intersense.signalrchat.Event
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatRepository
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.data.database.repository.message.MessageRepository
import tw.com.intersense.signalrchat.data.network.ChatHubHelper
import javax.inject.Inject
import tw.com.intersense.signalrchat.data.network.ChatHubHelperListener as ChatHubHelperListener

@HiltViewModel
class MainViewModel @Inject constructor(
    chatRepo: ChatRepository,
    messageRepo: MessageRepository,
//    userRepo: UserRepository,
    mySharedPreferences: MySharedPreferences
) : ViewModel() {

    private val _action = MutableLiveData<Event<MainAction>>()
    val action: LiveData<Event<MainAction>>
        get() = _action

    var chatHubHelper: ChatHubHelper = ChatHubHelper(mySharedPreferences,
        object : ChatHubHelperListener{
            override fun onTokenFail() {
                action(MainAction(MainActionType.TokenFail))
            }

            override fun onConnected(helper: ChatHubHelper) {
                action(MainAction(MainActionType.Connected))
                if(helper.listerUserAll()){
                    helper.updateChatList()
                }
            }

            override fun onDisconnected() {
                action(MainAction(MainActionType.Disconnected))
            }

            override fun onNewChat(chat: Chat) {
                viewModelScope.launch {
                    chatRepo.saveChats(chat)
                }
            }

            override fun onUpdateChatList(listChat: Array<Chat>) {
                viewModelScope.launch {
                    chatRepo.saveChats(*listChat)
                }
            }

            override fun onNewMessage(message: Message) {
                viewModelScope.launch {
                    chatRepo.updateLastMessage(message)
                    messageRepo.saveMessages(message)
                }
            }
        }
    )

    val listChat: LiveData<List<Chat>> = chatRepo.observeChats()

    fun onResume() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                chatHubHelper.connect()
            }
        }
    }

    fun onPause() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                chatHubHelper.listerUserAll(false)//解除聽訊息
                chatHubHelper.stop()
            }
        }
    }


    private fun action( action: MainAction){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _action.value  = Event(action)
            }
        }
    }

}

data class MainAction @JvmOverloads constructor(
    var actionType: MainActionType,

    )

enum class MainActionType {
    TokenFail,
    Connected,
    Disconnected
}