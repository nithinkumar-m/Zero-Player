package com.zeroplayer.di

import android.content.ContentResolver
import android.content.Context
import com.zeroplayer.data.repository.DeviceVideoRepository
import com.zeroplayer.domain.repository.VideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindVideoRepository(
        impl: DeviceVideoRepository,
    ): VideoRepository

    companion object {
        @Provides
        @Singleton
        fun provideContentResolver(
            @ApplicationContext context: Context,
        ): ContentResolver = context.contentResolver
    }
}

