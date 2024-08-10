import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ClippingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.data.local.predefined.LocalData
import com.example.naturewhispers.data.mediaPlayer.IMediaPlayer
import com.example.naturewhispers.presentation.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random


class MediaPlayerImpl @Inject constructor(
    private val player: ExoPlayer,
    private val context: Context,
) : IMediaPlayer, Player.Listener {

    override var state = MutableStateFlow(
        PlayerState(
            isPlaying = player.isPlaying,
            duration = player.duration / 1000,
            amplitudes = (1..300).map { Random.nextInt(from = 1, until = 5) },
            currentPosition = player.currentPosition / 1000
        )
    )
        private set

    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    init {
        try {
            player.addListener(this)
            player.prepare()
            observePlayerState()
        } catch (e: Exception) {
            Log.i(TAG, "[Player] Error: ${e.message}")
        }
    }

    private fun observePlayerState() {
        job?.cancel()
        job = coroutineScope.launch {
            while (true) {
                if (player.playbackState == Player.STATE_READY)
                    state.value = state.value.copy(
                        duration = player.duration / 1000,
                        currentPosition = player.currentPosition / 1000
                    )
                delay(100)
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        state.value = state.value.copy(isPlaying = isPlaying)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == Player.STATE_ENDED)
            Log.i(TAG, "[Player] onPlaybackStateChanged: STATE_ENDED")
        if (playbackState == Player.STATE_BUFFERING) {
            Log.i(TAG, "[Player] onPlaybackStateChanged: STATE_BUFFERING")
            state.value = state.value.copy(isLoading = true)
        }
        if (playbackState == Player.STATE_IDLE) {
            Log.i(TAG, "[Player] onPlaybackStateChanged: STATE_IDLE")
            player.prepare()
        }
        if (playbackState == Player.STATE_READY) {
            Log.i(TAG, "[Player] onPlaybackStateChanged: STATE_READY")
            player.playWhenReady = false
            state.value = state.value.copy(isLoading = false)
        }
    }

    @OptIn(UnstableApi::class)
    override fun prepare(audio: Audio) {
        Log.i(TAG, "[Player] Player is preparing...${audio}")
        try {
            observePlayerState()

            val localAudioList = LocalData.meditationSounds.map { it.key }
            val mediaItem = MediaItem.Builder()
                .setUri(audio.uri.ifEmpty { localAudioList.find { it.title == audio.title }!!.uri })
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setAlbumArtist(audio.artist)
                        .setSubtitle(audio.subtitle)
                        .setTitle(audio.title)
                        .build()
                )
                .build()
            val dataSourceFactory = DefaultDataSourceFactory(context, "userAgent")

            val underlyingSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)

            val mediaSource = ClippingMediaSource(
                underlyingSource,
                0, // start position (in microseconds)
                audio.durationMillis * 1000// end position (in microseconds)
            )
            player.setMediaSource(mediaSource)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @OptIn(UnstableApi::class)
    override fun play() {
        Log.i(TAG, "[Player] onPlay")
        try {
            player.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun pause() {
        Log.i(TAG, "[Player] onPause")
        try {
            player.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun stop() {
        Log.i(TAG, "[Player] onStop")
        try {
            player.stop()
            player.seekTo(0)
            state.value = state.value.copy(currentPosition = 0, isPlaying = false)
            job?.cancel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun seekTo(position: Int) {
        try {
            player.seekTo(position.toLong())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
