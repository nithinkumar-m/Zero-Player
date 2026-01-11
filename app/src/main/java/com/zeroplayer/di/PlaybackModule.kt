package com.zeroplayer.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.zeroplayer.domain.playback.PlaybackCommander
import com.zeroplayer.playback.Media3PlaybackComponent
import com.zeroplayer.playback.PlaybackComponent
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class PlaybackBindingsModule {
    @Binds
    abstract fun bindPlaybackCommander(impl: Media3PlaybackComponent): PlaybackCommander

    @Binds
    abstract fun bindPlaybackComponent(impl: Media3PlaybackComponent): PlaybackComponent
}

@Module
@InstallIn(ActivityRetainedComponent::class)
object PlaybackPlayerModule {
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build()
}

