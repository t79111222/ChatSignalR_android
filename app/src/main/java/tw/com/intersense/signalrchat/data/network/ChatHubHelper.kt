package tw.com.intersense.signalrchat.data.network

import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatRepository
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import java.lang.Exception

private const val BASE_URL = "http://192.168.1.105:44389/"
private const val HUB_CHAT = "ChatHub"

class ChatHubHelper internal constructor(
    private var mySharedPreferences: MySharedPreferences,
    private var listener: ChatHubHelperListener
) {

    private val TAG = "ChatHubHelper"

    private var chatHubConnection: HubConnection =
        HubConnectionBuilder.create("$BASE_URL$HUB_CHAT").build()

    fun connect(): HubConnectionState {
        var toekn = mySharedPreferences.getToken()
        if (toekn.isNullOrEmpty()) {
            listener.onTokenFail()
            return HubConnectionState.DISCONNECTED
        }
        if (chatHubConnection.connectionState == HubConnectionState.DISCONNECTED) {
            chatHubConnection = HubConnectionBuilder.create("$BASE_URL$HUB_CHAT")
                .withAccessTokenProvider(Single.defer {
                    Single.just(
                        toekn
                    )
                }).build()
            setHubReceive()//設定接收各通知

            try {
                chatHubConnection.start().doOnComplete {
                    listener.onConnected(this)

                }.doOnError {
                    Timber.d(TAG, it.message)
                    listener.onDisconnected()
                    //TODO:如果token失效要回登入
                }.blockingAwait()
            } catch (ex: Exception) {
                listener.onDisconnected()
                //TODO:如果token失效要回登入
                Timber.d(TAG, ex.message)
            }
        }
        return chatHubConnection.connectionState
    }

    fun stop() {
        if (chatHubConnection.connectionState == HubConnectionState.CONNECTED) chatHubConnection.stop()
    }

    //註冊/解除 接收使用者所有的Chat訊息
    fun listerUserAll(isListen:Boolean = true): Boolean {
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(if(isListen) HubListen.USER_ALL.method else  HubUnListen.USER_ALL.method)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    private fun setHubReceive() {

        //監聽離線
        chatHubConnection.onClosed {
            listener.onDisconnected()
        }

        //HubRequest.GET_USER_CHATS 的回傳資訊
        chatHubConnection.on(HubReceive.USER_CHATS.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.USER_CHATS.method} : $jsonString")

                    val gson= Gson()
                    var list = gson.fromJson(jsonString, Array<Chat>::class.java)
                    list?.let {
                        listener.onUpdateChatList(it)
                    }
                }catch (ex: Exception){
                    Timber.d(TAG, ex.message)
                }
            }

        }, String::class.java)

        //新的Message
        chatHubConnection.on(HubReceive.NEW_MESSAGE.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.NEW_MESSAGE.method} : $jsonString")

                    val gson= Gson()
                    var messge = gson.fromJson(jsonString, Message::class.java)
                     messge?.let {
                         listener.onNewMessage(it)
                     }
                }catch (ex: Exception){
                    Timber.d(TAG, ex.message)
                }
            }

        }, String::class.java)

        //Chat 更詳細的資訊
        chatHubConnection.on(HubReceive.CHAT_INFO.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.CHAT_INFO.method} : $jsonString")

                    val gson= Gson()
                    var chatInfo = gson.fromJson(jsonString, ChatInfo::class.java)
                    chatInfo?.let {
                        listener.onUpdateChatInfo(it)
                    }

                }catch (ex: Exception){
                    Timber.d(TAG, ex.message)
                }
            }

        }, String::class.java)

        //有新的Chat
        chatHubConnection.on(HubReceive.NEW_CHAT.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.NEW_CHAT.method} : $jsonString")

                    val gson= Gson()
                    var chatInfo = gson.fromJson(jsonString, Chat::class.java)
                    chatInfo?.let {
                        listener.onNewChat(it)
                    }

                }catch (ex: Exception){
                    Timber.d(TAG, ex.message)
                }
            }

        }, String::class.java)

    }

    fun updateChatList(): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.GET_USER_CHATS.method)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    fun updateChatInfo(chatId:Int, messageStartTime: Long): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.GET_CHAT_INFO.method, chatId, messageStartTime)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    fun sendMessage(chatId:Int, messageText: String, messageType: Int): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.SEND_MESSAGE.method, chatId, messageText, messageType)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    fun createChatAndSendMessage(productId:Int, messageText: String, messageType: Int): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.CREATE_CHAT_AND_SEND_MESSAGE.method,productId, messageText, messageType)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    enum class HubListen(val method: String){
        USER_ALL("ListenUserAll")
    }

    enum class HubUnListen(val method: String){
        USER_ALL("UnListenUserAll")
    }

    enum class HubRequest(val method: String){
        GET_USER_CHATS("GetUserChats"),
        GET_CHAT_INFO("GetChatInfo"),
        SEND_MESSAGE("SendMessage"),
        CREATE_CHAT_AND_SEND_MESSAGE("CreateChatAndSendMessage")
    }

    enum class HubReceive(val method: String){
        USER_CHATS("UserChats"),
        NEW_MESSAGE("RecieveMessage"),
        CHAT_INFO("ChatInfo"),
        NEW_CHAT("NewChat")
    }
}

interface ChatHubHelperListener{
    fun onTokenFail(){}
    fun onConnected(helper: ChatHubHelper){}
    fun onDisconnected(){}
    fun onUpdateChatList(listChat: Array<Chat>){}
    fun onNewMessage(message: Message){}
    fun onUpdateChatInfo(chatInfo: ChatInfo){}
    fun onNewChat(chat: Chat){}
}