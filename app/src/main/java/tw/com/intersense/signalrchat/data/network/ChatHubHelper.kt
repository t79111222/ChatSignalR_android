package tw.com.intersense.signalrchat.data.network

import com.google.gson.Gson
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import tw.com.intersense.signalrchat.MySharedPreferences
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import java.lang.Exception

private const val BASE_URL = "http://192.168.1.101:44389/"
private const val HUB_CHAT = "ChatHub"

class ChatHubHelper internal constructor(
    private var mySharedPreferences: MySharedPreferences,
    private var listener: ChatHubHelperListener
) {

    private val TAG = "ChatHubHelper"

    private var chatHubConnection: HubConnection =
        HubConnectionBuilder.create("$BASE_URL$HUB_CHAT").build()

    fun connect(): HubConnectionState {
        //取得紀錄的token
        var token = mySharedPreferences.getToken()
        if (token.isNullOrEmpty()) {
            listener.onTokenFail()
            return HubConnectionState.DISCONNECTED
        }
        if (chatHubConnection.connectionState == HubConnectionState.DISCONNECTED) {
            //未連線
            chatHubConnection = HubConnectionBuilder.create("$BASE_URL$HUB_CHAT")
                .withAccessTokenProvider(Single.defer {
                    Single.just(
                        token
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
                //11.0.1-監聽User的所有資訊 & 11.0.2-取消監聽User的所有資訊
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

        //HubRequest.GET_LIST_CHAT 的回傳資訊
        chatHubConnection.on(HubReceive.GET_LIST_CHAT.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.GET_LIST_CHAT.method} : $jsonString")

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

        //HubRequest.GET_CHAT_AND_MESSAGE 的回傳資訊
        chatHubConnection.on(HubReceive.GET_CHAT_AND_MESSAGE.method, { jsonString ->
            if(!jsonString.isNullOrEmpty()){
                try {
                    Timber.i(TAG, "${HubReceive.GET_CHAT_AND_MESSAGE.method} : $jsonString")
                    val gson= Gson()
                    var list = gson.fromJson(jsonString, ChatMessage::class.java)
                    list?.let {
                        listener.onUpdateChatAndMessage(it)
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
                    var message = gson.fromJson(jsonString, Message::class.java)
                    message?.let {
                         listener.onNewMessage(it)
                     }
                }catch (ex: Exception){
                    Timber.d(TAG, ex.message)
                }
            }

        }, String::class.java)

    }

    //11.2.1-請求聊天室清單
    fun updateChatList(): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.GET_LIST_CHAT.method)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    //11.3.1-請求聊天室&訊息資訊
    fun updateChatAndMessage(productId:Int , askerPhoneId: String, startTime: Long ): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.GET_CHAT_AND_MESSAGE.method, productId, askerPhoneId, startTime)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    //11.4.1-新增訊息 ※若為第一次提問，會自動開聊天室(由11.1接收新聊天室)
    fun sendMessage(productId:Int , askerPhoneId: String, messageText: String, messageType: String): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.CREATE_MESSAGE.method, productId, askerPhoneId, messageText, messageType)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    //11.5-請求聊天室已讀
    fun SetChatMessageRead(productId:Int , askerPhoneId: String): Boolean {
        //要先Listen User All,不然會接收不到資訊
        try {
            if (connect() == HubConnectionState.CONNECTED) {
                chatHubConnection.invoke(HubRequest.SET_CHAT_MESSAGE_READ.method, productId, askerPhoneId)
                return true
            }
        } catch (ex: Exception) {
            Timber.d(TAG, ex.message)
        }
        return false
    }

    enum class HubListen(val method: String){
        USER_ALL("ListenUserAll")//11.0.1-監聽User的所有資訊
    }

    enum class HubUnListen(val method: String){
        USER_ALL("UnListenUserAll")//11.0.2-取消監聽User的所有資訊
    }

    enum class HubRequest(val method: String){
        GET_LIST_CHAT("GetListChat"),//11.2.1-請求聊天室清單
        GET_CHAT_AND_MESSAGE("GetChatAndMessage"),//11.3.1-請求聊天室&訊息資訊
        CREATE_MESSAGE("CreateMessage"),//11.4.1-新增訊息
        SET_CHAT_MESSAGE_READ("SetChatMessageRead"),//11.5-請求聊天室已讀
    }

    enum class HubReceive(val method: String){
        NEW_CHAT("NewChat"),//11.1-接收新聊天室
        GET_LIST_CHAT("ReceiveListChat"),//11.2.2-接收聊天室資訊
        GET_CHAT_AND_MESSAGE("ReceiveChatAndMessage"),//11.3.2-接收聊天室&訊息資訊
        NEW_MESSAGE("NewMessage"),//11.4.2-接收新訊息
    }
}

interface ChatHubHelperListener{
    fun onTokenFail(){}
    fun onConnected(helper: ChatHubHelper){}
    fun onDisconnected(){}
    fun onNewChat(chat: Chat){}
    fun onUpdateChatList(listChat: Array<Chat>){}
    fun onUpdateChatAndMessage(chatMessage: ChatMessage){}
    fun onNewMessage(message: Message){}
}