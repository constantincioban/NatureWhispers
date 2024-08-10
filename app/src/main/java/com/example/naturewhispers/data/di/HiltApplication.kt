package com.example.naturewhispers.data.di

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class HiltApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "player_channel",
            "Player notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

const val TAG = "NATURE_WHISPERS"
