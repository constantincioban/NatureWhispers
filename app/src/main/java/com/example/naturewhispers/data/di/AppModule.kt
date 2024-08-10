package com.example.naturewhispers.data.di

import MediaPlayerImpl
import android.app.Application
import androidx.room.Room
import com.example.naturewhispers.data.local.db.NWDatabase
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.local.preferences.SettingsManagerImpl
import com.example.naturewhispers.data.mediaPlayer.IMediaPlayer
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAppStateStore(): Store<AppState> {
        return Store(AppState())
    }

    @Provides
    @Singleton
    fun provideNWDatabase(app: Application): NWDatabase {
        return Room.databaseBuilder(
            app,
            NWDatabase::class.java,
            "nw_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePresetDao(db: NWDatabase): PresetDao = db.presetDao

    @Provides
    @Singleton
    fun provideStatDao(db: NWDatabase): StatDao = db.statDao


    @Singleton
    @Provides
    fun provideSettingsManager(app: Application): SettingsManager {
        return SettingsManagerImpl(app)
    }


}