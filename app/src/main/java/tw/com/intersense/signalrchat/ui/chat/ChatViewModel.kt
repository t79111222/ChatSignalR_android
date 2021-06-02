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
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatUser
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.data.database.repository.message.MessageRepository
import tw.com.intersense.signalrchat.data.database.repository.user.User
import tw.com.intersense.signalrchat.data.database.repository.user.UserRepository
import tw.com.intersense.signalrchat.data.network.ChatHubHelper
import tw.com.intersense.signalrchat.data.network.ChatInfo
import java.time.Instant
import javax.inject.Inject
import tw.com.intersense.signalrchat.data.network.ChatHubHelperListener as ChatHubHelperListener

@HiltViewModel
class ChatViewModel @Inject constructor(
    val chatRepo: ChatRepository,
    val messageRepo: MessageRepository,
    val userRepo: UserRepository,
    val  mySharedPreferences: MySharedPreferences
) : ViewModel() {

    lateinit var listMessage: LiveData<List<Message>>
    private var _chatId = 0
    private var _productId = 0
    var chat: Chat? = null
    var productUser: User? = null
    var reqestUser: User? = null
    var myUserId = mySharedPreferences.getUserId()
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
                helper.listerUserAll()
            }

            override fun onDisconnected() {
                action(ChatAction(ChatActionType.Disconnected))
            }

            override fun onNewMessage(message: Message) {
                viewModelScope.launch {
                    chatRepo.updateLastMessage(message)
                    messageRepo.saveMessages(message)
                }
            }

            override fun onUpdateChatInfo(chatInfo: ChatInfo) {
                viewModelScope.launch {
                    mySharedPreferences.setChatInfoLastUpdateTime(chatInfo.id, Instant.now().epochSecond)
                    var result = chatRepo.getChat(chatInfo.id)
                    if(result is RepoResult.Success){
                        var dbChat = result.data
                        var lastM = dbChat.lastMessage
                        var lastT = dbChat.lastTime
                        if(chatInfo.listMessage.isNotEmpty()){
                            var lastMessage = chatInfo.listMessage.last()
                            lastM = lastMessage.text
                            lastT = lastMessage.time
                        }
                        var chat = Chat(chatInfo.id, chatInfo.name, lastM, lastT
                            , chatInfo.product?.id, chatInfo.product?.name, chatInfo.product?.userId)
                        chatRepo.saveChats(chat)
                        messageRepo.saveMessages(*chatInfo.listMessage)
                        userRepo.saveUsers(*chatInfo.listUser)
                        for(user in chatInfo.listUser){
                            chatRepo.saveChatUsers(ChatUser(chat.id, user.id))
                        }
                    }
                }
            }

            override fun onNewChat(c: Chat) {
                viewModelScope.launch {
                    chatRepo.saveChats(c)
                    if(_chatId == 0 && c.productId == _productId){
                        //剛剛才新增的聊天室，為現在這個聊天室
                        chat = c
                        _chatId = c.id
                        hadCorrectChat()
                    }
                }
            }
        }
    )


    fun onResume() {
        if(chat != null) {//剛進來不會連線,直到拿到正確的chat id才連線
            connectChatHub()
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
        return if(chat ==null){
            chatHubHelper.createChatAndSendMessage(_productId, messageText, 1)
        }else{
            chatHubHelper.sendMessage(_chatId, messageText, 1)//message type = 1 is Text
        }
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

    fun setChatValue(chatId: Int, productId: Int) {
        _productId = productId
        viewModelScope.launch {
            if (chatId != 0) {
                chat = getChat(chatRepo.getChat(chatId))
            } else if(productId != 0){
                chat = getChat(chatRepo.getChatByProductId(productId))
            }
            if(chat != null){
                _chatId = chat!!.id
                listMessage = messageRepo.observeMessages(chat!!.id)
                if(!chat!!.productUserId.isNullOrEmpty()){
                    productUser = getUser(userRepo.getUser(chat!!.productUserId!!))
                }
                var listChatUser = getChatUSer(chatRepo.getChatUser(_chatId))
                listChatUser?.let {
                    for (chatUser in it){
                        if (productUser == null){//先當成自己的產品
                            if(chatUser.userId != myUserId){
                                reqestUser =getUser(userRepo.getUser(chatUser.userId))
                                break
                            }
                        } else{
                            if(chatUser.userId != productUser!!.id){
                                reqestUser =getUser(userRepo.getUser(chatUser.userId))
                                break
                            }
                        }
                    }
                }
            }
            hadCorrectChat()
        }
    }

    private fun hadCorrectChat(){
        listMessage = messageRepo.observeMessages(_chatId)
        var lastUpdateTime = mySharedPreferences.getChatInfoLastUpdateTime(_chatId)
        chatHubHelper.updateChatInfo(_chatId, lastUpdateTime)
        action(ChatAction(ChatActionType.OnChatChange))
    }

    private fun getChat( r: RepoResult<Chat>) : Chat?{
        return if(r is RepoResult.Success){
            r.data
        } else {
            null
        }
    }

    private fun getUser( r: RepoResult<User>) : User?{
        return if(r is RepoResult.Success){
            r.data
        } else {
            null
        }
    }

    private fun getChatUSer( r: RepoResult<List<ChatUser>>) : List<ChatUser>?{
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
    OnChatChange
}