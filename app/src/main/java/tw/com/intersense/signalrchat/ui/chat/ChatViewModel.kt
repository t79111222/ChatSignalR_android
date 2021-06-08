package tw.com.intersense.signalrchat.ui.chat

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
import tw.com.intersense.signalrchat.data.database.repository.RepoResult
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatRepository
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.data.database.repository.message.MessageRepository
import tw.com.intersense.signalrchat.data.network.ChatHubHelper
import tw.com.intersense.signalrchat.data.network.ChatMessage
import tw.com.intersense.signalrchat.data.network.Product
import javax.inject.Inject
import kotlin.properties.Delegates
import tw.com.intersense.signalrchat.data.network.ChatHubHelperListener as ChatHubHelperListener

@HiltViewModel
class ChatViewModel @Inject constructor(
    val chatRepo: ChatRepository,
    val messageRepo: MessageRepository,
    val  mySharedPreferences: MySharedPreferences
) : ViewModel() {

    lateinit var listMessage: LiveData<List<Message>>
    var _productId by Delegates.notNull<Int>()
    lateinit var _askerPhoneId: String
    private var _product: Product? = null

    var chat: Chat? = null
    var myPhoneId = mySharedPreferences.getPhoneId()?:""

    private val _action = MutableLiveData<Event<ChatAction>>()
    val action: LiveData<Event<ChatAction>>
        get() = _action

    var chatHubHelper: ChatHubHelper = ChatHubHelper(mySharedPreferences,
        object : ChatHubHelperListener{
            override fun onTokenFail() {
                action(ChatAction(ChatActionType.TokenFail))
            }

            override fun onConnected(helper: ChatHubHelper) {
                action(ChatAction(ChatActionType.Connected))
                if(helper.listerUserAll()){
                    var time = mySharedPreferences.getUpdateChatAndMessageTime(_productId, _askerPhoneId)
                    SetChatMessageRead()
                    helper.updateChatAndMessage(_productId, _askerPhoneId, time)
                }
            }

            override fun onDisconnected() {
                action(ChatAction(ChatActionType.Disconnected))
            }

            override fun onNewChat(c: Chat) {
                viewModelScope.launch {
                    chatRepo.saveChats(c)
                    if(c.ProductId == _productId && c.AskerPhoneId == _askerPhoneId){
                        //剛剛才新增的聊天室，為現在這個聊天室
                        chat = c
                        action(ChatAction(ChatActionType.OnChatChange))
                    }
                }
            }

            override fun onUpdateChatAndMessage(chatMessage: ChatMessage) {
                super.onUpdateChatAndMessage(chatMessage)
                viewModelScope.launch {
                    chatRepo.saveChats(chatMessage.Chat)
                    messageRepo.saveMessages(*chatMessage.ListMessage)
                }
            }

            override fun onNewMessage(message: Message) {
                if(message.ProductId == _productId && message.AskerPhoneId == _askerPhoneId)SetChatMessageRead()
                viewModelScope.launch {
                    chatRepo.updateLastMessage(message)
                    messageRepo.saveMessages(message)

                }
            }
        }
    )

    fun SetChatMessageRead(){

        chatHubHelper.SetChatMessageRead(_productId, _askerPhoneId)
    }

    fun setChatValue(productId: Int, askerPhoneId: String?, product: Product?) {
        _productId = productId
        _askerPhoneId = askerPhoneId ?: myPhoneId
        viewModelScope.launch {
            chatRepo.setRead(_productId, _askerPhoneId)
            if (askerPhoneId != null) {
                //從聊天室清單來的
                chat = getChat(chatRepo.getChat(productId, askerPhoneId))
            } else if(product != null){
                //從商品清單來的
                chat = getChat(chatRepo.getChat(productId, myPhoneId))
                _product = product
                if(chat == null){
                    //本地DB找不到
                    chat = Chat(_productId, _askerPhoneId,  _askerPhoneId, "", product.Name,
                        product.Price, product.ImageLink, "", product.OwnerName, product.OwnerImageLink,
                        null, null, null, 0)
                }
            }

            action(ChatAction(ChatActionType.OnChatChange))
        }
        listMessage = messageRepo.observeMessages(_productId, _askerPhoneId)


        listMessage.observeForever {
            action(ChatAction(ChatActionType.OnMessageUpdate))
        }
    }

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

    fun onSend(messageText:String): Boolean{
        return  chatHubHelper.sendMessage(_productId,_askerPhoneId,  messageText, "Text")
    }

    private fun connectChatHub(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                chatHubHelper.connect()
            }
        }
    }

    private fun action( action: ChatAction){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                _action.value  = Event(action)
            }
        }
    }

    private fun getChat( r: RepoResult<Chat>) : Chat?{
        return if(r is RepoResult.Success){
            r.data
        } else {
            null
        }
    }
}

data class ChatAction @JvmOverloads constructor(
    var actionType: ChatActionType,
    )

enum class ChatActionType {
    TokenFail,
    Connected,
    Disconnected,
    OnChatChange,
    OnMessageUpdate
}