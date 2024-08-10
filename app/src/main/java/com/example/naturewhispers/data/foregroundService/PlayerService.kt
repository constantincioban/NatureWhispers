package com.example.naturewhispers.data.foregroundService

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.naturewhispers.R
import com.example.naturewhispers.data.notification.NWNotificationManager
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService: MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession
    @Inject
    lateinit var notificationManager: NWNotificationManager
    @Inject
    lateinit var store: Store<AppState>


    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> {

                Log.i(com.example.naturewhispers.data.di.TAG, "[Service] onStartCommand")
                notificationManager.startNotificationService(
                    mediaSession = mediaSession,
                    mediaSessionService = this,
                )
            }
            ACTION_STOP -> {
                Log.i(com.example.naturewhispers.data.di.TAG, "[Service] ACTION_STOP")
                notificationManager.removePlayer()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }


    // Create your player and media session in the onCreate lifecycle event
    @androidx.media3.common.util.UnstableApi
    override fun onCreate() {
        super.onCreate()
        Log.i(com.example.naturewhispers.data.di.TAG, "[Service] onCreate")
        val resultIntent = Intent(this, MainActivity::class.java, )
        val pendingIntentFlag = PendingIntent.FLAG_IMMUTABLE
        val activityActionPendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            101,
            resultIntent,
            pendingIntentFlag
        )
        mediaSession.setSessionActivity(activityActionPendingIntent)
    }

    // The user dismissed the app from the recent tasks
    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.i(com.example.naturewhispers.data.di.TAG, "[Service] onTaskRemoved ")
        val player = mediaSession.player
        if (!player.playWhenReady
            || player.mediaItemCount == 0
            || player.playbackState == Player.STATE_ENDED) {
            // Stop the service if not playing, continue playing in the background
            // otherwise.
            stopSelf()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    // Remember to release the player and media session in onDestroy
    override fun onDestroy() {
        super.onDestroy()
        Log.i(com.example.naturewhispers.data.di.TAG, "[Service] onDestroy: ")

    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}

