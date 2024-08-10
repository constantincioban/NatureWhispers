package com.example.naturewhispers.data.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import com.example.naturewhispers.R
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.ui.MainActivity

private const val NOTIFICATION_ID = 101
private const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID"
private const val NOTIFICATION_CHANNEL_NAME = "NW_CHANNEL"

class NWNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer,
) {
    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    @UnstableApi
    private lateinit var playerNotificationManager: PlayerNotificationManager


    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel()
    }

    @UnstableApi
    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession,
    ) {
        buildNotification(mediaSession)
        startNotificationForegroundService(mediaSessionService)
    }

    @UnstableApi
    fun removePlayer() {
        playerNotificationManager.setPlayer(null)
    }

    @UnstableApi
    private fun buildNotification(
        mediaSession: MediaSession,
    ) {
        playerNotificationManager = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID
        )
            .setMediaDescriptionAdapter(
                NWNotificationAdapter(
                    context,
                    pendingIntent = mediaSession.sessionActivity
                )
            )

//            .setSmallIconResourceId(R.drawable.nw_logo)
            .build()
            .also {
                it.setMediaSessionToken(mediaSession.sessionCompatToken)
                it.setUseFastForwardActionInCompactView(true)
                it.setUseRewindActionInCompactView(true)
                it.setUseNextActionInCompactView(true)
                it.setPriority(NotificationCompat.PRIORITY_LOW)
                it.setPlayer(exoPlayer)
            }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun startNotificationForegroundService(
        mediaSessionService: MediaSessionService,
    ) {

        val notification = Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.baseline_play_arrow_24)
            .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}