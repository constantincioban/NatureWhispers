package com.example.naturewhispers.data.di

import android.app.Application
import androidx.room.Room
import com.example.naturewhispers.data.auth.GoogleAuthHelper
import com.example.naturewhispers.data.auth.IAuthHelper
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.cloud.FirestoreHelper
import com.example.naturewhispers.data.cloud.IFirestoreHelper
import com.example.naturewhispers.data.local.db.NWDatabase
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.preferences.SettingsManagerFake
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TestAppModule {

    @Provides
    @Singleton
    fun provideGoogleAuthHelper(app: Application): IAuthHelper {
        return GoogleAuthHelper(app)

    }

    @Provides
    @Singleton
    fun provideFirestoreHelper(): IFirestoreHelper {
        return FirestoreHelper()
    }

    @Provides
    @Singleton
    fun provideAppStateStore(): Store<AppState> {
        return Store(AppState())
    }

    @Provides
    @Singleton
    fun provideNWDatabase(app: Application): NWDatabase {
        val db =  Room.inMemoryDatabaseBuilder(
            app,
            NWDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries() // Useful for testing in instrumented tests
            .build()

        // Pre-insert test data into the database
        // Assuming you have a DAO called `presetDao`
        runBlocking {
            db.presetDao.apply {
                // Insert test data (this can be a list or individual items)
                upsertPreset(Preset(id = 1, title = "Test Preset 1", duration = 120, sound = "Bonfire", fileUri = "https://www.dropbox.com/scl/fi/e0lvo15n3uz0o9dsk2tmg/Bonfire.mp3?rlkey=449cq46hmxqmoqh7kckvjgudd&st=1v8v3v9w&dl=1"))
                upsertPreset(Preset(id = 2, title = "Test Preset 2", duration = 180, sound = "Forest", fileUri = "https://www.dropbox.com/scl/fi/n84sncdo8jcfrf994wlsb/Forest.mp3?rlkey=nf3d9y80rouux2p41jzv7cvue&st=h9c98lrn&dl=1"))
            }
        }

        return db
    }

    @Provides
    @Singleton
    fun providePresetDao(db: NWDatabase): PresetDao = db.presetDao

    @Provides
    @Singleton
    fun provideStatDao(db: NWDatabase): StatDao = db.statDao


    @Provides
    @Singleton
    fun provideSettingsManager(): SettingsManager {
        return SettingsManagerFake()
    }


}