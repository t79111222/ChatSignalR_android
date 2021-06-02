package tw.com.intersense.signalrchat.di

import tw.com.intersense.signalrchat.MySharedPreferences
import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import tw.com.intersense.signalrchat.data.database.AppDatabase
import tw.com.intersense.signalrchat.data.database.repository.chat.ChatRepository
import tw.com.intersense.signalrchat.data.database.repository.message.MessageRepository
import tw.com.intersense.signalrchat.data.database.repository.user.UserRepository
import javax.inject.Singleton

/**
 * Module to tell Hilt how to provide instances of types that cannot be constructor-injected.
 *
 * As these types are scoped to the application lifecycle using @Singleton, they're installed
 * in Hilt's ApplicationComponent.
 */


@InstallIn(SingletonComponent::class)
@Module
object AppModule {



    @Provides
    @Singleton
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Chat.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideChatRepository(
        database: AppDatabase,
        ioDispatcher: CoroutineDispatcher
    ): ChatRepository {
        return ChatRepository(
            database.chatDao(), database.chatUserDao(), ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideMessageRepository(
        database: AppDatabase,
        ioDispatcher: CoroutineDispatcher
    ): MessageRepository {
        return MessageRepository(
            database.messageDao(), ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        database: AppDatabase,
        ioDispatcher: CoroutineDispatcher
    ): UserRepository {
        return UserRepository(
            database.userDao(), ioDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideMySharedPreferences(@ApplicationContext context: Context): MySharedPreferences {
        return MySharedPreferences(context.applicationContext)
    }

}