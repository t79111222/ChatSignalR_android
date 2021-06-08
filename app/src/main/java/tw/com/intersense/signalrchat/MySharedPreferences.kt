package tw.com.intersense.signalrchat

import android.content.Context
import tw.com.intersense.signalrchat.data.network.Product

class MySharedPreferences(private val context: Context) {
    companion object {
        private const val DATA = "DATA"
        private const val KEY_IS_LOGIN = "KEY_IS_LOGIN"
        private const val KEY_TOKEN = "KEY_TOKEN"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_CHAT_INFO_UPDATE_TIME = "KEY_CHAT_INFO_UPDATE_TIME"
    }

    fun isLogin(): Boolean {
        val settings = context.getSharedPreferences(DATA, 0)
        return settings.getBoolean(KEY_IS_LOGIN, false)
    }

    fun isLogin(b: Boolean) {
        val settings = context.getSharedPreferences(DATA, 0)
        settings.edit().putBoolean(KEY_IS_LOGIN, b).apply()
    }

    fun getToken(): String? {
        val settings = context.getSharedPreferences(DATA, 0)
        return settings.getString(KEY_TOKEN, "")
    }

    fun setToken(token: String) {
        val settings = context.getSharedPreferences(DATA, 0)
        settings.edit().putString(KEY_TOKEN, token).apply()
    }
    fun getPhoneId(): String? {
        val settings = context.getSharedPreferences(DATA, 0)
        return settings.getString(KEY_USER_ID, "")
    }

    fun setPhoneId(id: String) {
        val settings = context.getSharedPreferences(DATA, 0)
        settings.edit().putString(KEY_USER_ID, id).apply()
    }

    fun getUpdateChatAndMessageTime(productId: Int, askerPhoneId:String): Long {
        val settings = context.getSharedPreferences(DATA, 0)
        return settings.getLong("${KEY_CHAT_INFO_UPDATE_TIME}_${productId}_${askerPhoneId}", 0)
    }

    fun setUpdateChatAndMessageTime(productId: Int, askerPhoneId:String, lastUpdateTime: Long) {
        val settings = context.getSharedPreferences(DATA, 0)
        settings.edit().putLong("${KEY_CHAT_INFO_UPDATE_TIME}_${productId}_${askerPhoneId}", lastUpdateTime).apply()
    }

}