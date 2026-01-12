package com.zeroplayer.di

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.zeroplayer.data.db.PlaybackResumeDao
import com.zeroplayer.data.db.ZeroPlayerDatabase
import com.zeroplayer.data.repository.DeviceVideoRepository
import com.zeroplayer.data.settings.DataStoreSettingsRepository
import com.zeroplayer.domain.repository.SettingsRepository
import com.zeroplayer.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        impl: DeviceVideoRepository,
    ): VideoRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: DataStoreSettingsRepository,
    ): SettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideContentResolver(
            @ApplicationContext context: Context,
        ): ContentResolver = context.contentResolver

        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope =
            CoroutineScope(SupervisorJob() + Dispatchers.Default)

        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context,
        ): ZeroPlayerDatabase =
            Room.databaseBuilder(context, ZeroPlayerDatabase::class.java, "zeroplayer.db")
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        fun providePlaybackResumeDao(
            db: ZeroPlayerDatabase,
        ): PlaybackResumeDao = db.playbackResumeDao()
    }
}

