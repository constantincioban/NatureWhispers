package com.example.naturewhispers.data.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.naturewhispers.R

@UnstableApi
class NWNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
): MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: Player): CharSequence =
        player.mediaMetadata.displayTitle ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence =
        player.mediaMetadata.albumArtist ?: "Unknown"


    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        Glide.with(context)
            .asBitmap()
            .load(R.drawable.bg_main)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback.onBitmap(resource)
                }
                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })
        return null
    }
}