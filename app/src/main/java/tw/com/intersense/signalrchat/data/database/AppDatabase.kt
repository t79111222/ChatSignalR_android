package tw.com.intersense.signalrchat.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatDao
import tw.com.intersense.signalrchat.data.database.repository.chat.Chat
import tw.com.intersense.signalrchat.data.database.repository.message.Message
import tw.com.intersense.signalrchat.data.database.repository.message.MessageDao

@Database(entities = [Chat::class, Message::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
