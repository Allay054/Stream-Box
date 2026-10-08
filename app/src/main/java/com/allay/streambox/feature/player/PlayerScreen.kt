@file:OptIn(androidx.media3.common.util.UnstableApi::class)

package com.allay.streambox.feature.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.Rational
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import com.allay.streambox.MainActivity
import com.allay.streambox.domain.usecase.DeletePlaybackProgressUseCase
import com.allay.streambox.domain.usecase.GetPlaybackProgressUseCase
import com.allay.streambox.domain.usecase.SavePlaybackProgressUseCase
import com.allay.streambox.feature.channels.ChannelItem
import kotlinx.coroutines.launch

/**
 * Custom PlayerView for Android TV D-pad support.
 *
 * Media3 controller is disabled.
 * We use our own Previous / Next controls.
 */
class StreamBoxPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : PlayerView(context, attrs) {

    var onPreviousChannel: (() -> Unit)? = null

    var onNextChannel: (() -> Unit)? = null

    var onBackPressed: (() -> Unit)? = null

    override fun dispatchKeyEvent(
        event: KeyEvent
    ): Boolean {

        if (event.action != KeyEvent.ACTION_DOWN) {
            return super.dispatchKeyEvent(event)
        }

        Log.d(
            "StreamBoxPlayer",
            "KEY EVENT RECEIVED: ${event.keyCode}"
        )

        when (event.keyCode) {

            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {

                Log.d(
                    "StreamBoxPlayer",
                    "D-PAD LEFT / PREVIOUS"
                )

                onPreviousChannel?.invoke()

                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_MEDIA_NEXT -> {

                Log.d(
                    "StreamBoxPlayer",
                    "D-PAD RIGHT / NEXT"
                )

                onNextChannel?.invoke()

                return true
            }

            KeyEvent.KEYCODE_BACK -> {

                Log.d(
                    "StreamBoxPlayer",
                    "BACK KEY"
                )

                onBackPressed?.invoke()

                return true
            }
        }

        return super.dispatchKeyEvent(event)
    }
}

@Composable
private fun NetflixControlButton(
    text: String,
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .widthIn(min = 58.dp, max = 92.dp)
            .alpha(if (enabled) 1f else 0.35f)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .focusable(enabled = enabled)
            .clickable(
                enabled = enabled,
                onClick = onClick
            )
            .padding(
                horizontal = 6.dp,
                vertical = 3.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = if (isFocused) {
                Color.White
            } else {
                Color.White.copy(alpha = 0.95f)
            },
            textAlign = TextAlign.Center
        )

        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.80f),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun NetflixControlDivider() {
    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp)
            .widthIn(min = 1.dp, max = 1.dp)
            .height(38.dp)
            .background(
                Color.White.copy(alpha = 0.22f)
            )
    )
}


@Composable
private fun NetflixLevelBar(
    label: String,
    valueText: String,
    progress: Float,
    decreaseEnabled: Boolean,
    increaseEnabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(
        modifier = Modifier
            .widthIn(min = 210.dp, max = 280.dp)
            .padding(
                horizontal = 6.dp,
                vertical = 2.dp
            )
    ) {
        Text(
            text = "$label  $valueText",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.82f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NetflixControlButton(
                text = "−",
                label = "",
                enabled = decreaseEnabled,
                onClick = onDecrease
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Color.White.copy(alpha = 0.25f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            progress.coerceIn(0f, 1f)
                        )
                        .height(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )
            }

            NetflixControlButton(
                text = "+",
                label = "",
                enabled = increaseEnabled,
                onClick = onIncrease
            )
        }
    }
}


@Composable
fun PlayerScreen(
    channelId: String,
    channels: List<ChannelItem>,
    onBack: () -> Unit,
    getPlaybackProgressUseCase: GetPlaybackProgressUseCase,
    savePlaybackProgressUseCase: SavePlaybackProgressUseCase,
    deletePlaybackProgressUseCase: DeletePlaybackProgressUseCase
) {

    val context = LocalContext.current

    val activity = context as? Activity

    val mainActivity = activity as? MainActivity

    val playbackProgressScope = rememberCoroutineScope()

    /*
     * PiP state.
     *
     * MainActivity owns this state and updates it from
     * onPictureInPictureModeChanged().
     *
     * IMPORTANT:
     *
     * We do NOT release the player when this becomes false.
     *
     * False simply means the user returned from PiP.
     */
    val isInPipMode =
        mainActivity?.isInPipMode == true

    /*
     * System media volume.
     */
    val audioManager =
        remember(context) {
            context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager
        }

    var currentVolume by remember {
        mutableStateOf(
            audioManager.getStreamVolume(
                AudioManager.STREAM_MUSIC
            )
        )
    }

    val maxVolume =
        remember(audioManager) {
            audioManager.getStreamMaxVolume(
                AudioManager.STREAM_MUSIC
            )
        }

    var isMuted by remember {
        mutableStateOf(
            currentVolume == 0
        )
    }

    var volumeBeforeMute by remember {
        mutableStateOf(
            currentVolume.coerceAtLeast(1)
        )
    }

    /*
     * App-window brightness.
     *
     * This changes only the StreamBox window.
     */
    var currentBrightness by remember {
        mutableStateOf(
            activity
                ?.window
                ?.attributes
                ?.screenBrightness
                ?.takeIf { it >= 0f }
                ?: 0.5f
        )
    }

    /*
     * Register PlayerScreen as the active player.
     */
    DisposableEffect(mainActivity) {

        Log.d(
            "StreamBoxPiP",
            "PlayerScreen ACTIVE"
        )

        mainActivity?.isPlayerActive = true

        onDispose {

            Log.d(
                "StreamBoxPiP",
                "PlayerScreen DISPOSED"
            )

            mainActivity?.isPlayerActive = false
        }
    }

    /*
     * Current channel.
     */
    var currentChannelId by remember {
        mutableStateOf(channelId)
    }

    /*
     * Find current channel.
     */
    val currentChannel =
        channels.firstOrNull { channel ->
            channel.id == currentChannelId
        }

    /*
     * Current channel index.
     */
    val currentIndex =
        channels.indexOfFirst { channel ->
            channel.id == currentChannelId
        }

    /*
     * If channel does not exist,
     * return to previous screen.
     */
    if (currentChannel == null) {

        Log.e(
            "StreamBoxPlayer",
            "Current channel not found: $currentChannelId"
        )

        LaunchedEffect(Unit) {
            onBack()
        }

        return
    }

    /*
     * Create player once.
     */
    val streamPlayer = remember {

        Log.d(
            "StreamBoxPlayer",
            "Creating StreamPlayer"
        )

        StreamPlayer(context)
    }

    /*
     * Player state.
     */
    var isBuffering by remember {
        mutableStateOf(false)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var playbackError by remember {
        mutableStateOf<PlaybackException?>(null)
    }

    /*
     * Volume helpers.
     */
    fun setVolume(
        value: Int
    ) {

        val safeVolume =
            value.coerceIn(
                0,
                maxVolume
            )

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            safeVolume,
            0
        )

        currentVolume = safeVolume

        isMuted =
            safeVolume == 0

        if (safeVolume > 0) {
            volumeBeforeMute = safeVolume
        }

        Log.d(
            "StreamBoxControls",
            "Volume changed: $safeVolume/$maxVolume"
        )
    }

    fun volumeUp() {

        setVolume(
            currentVolume + 1
        )
    }

    fun volumeDown() {

        setVolume(
            currentVolume - 1
        )
    }

    fun toggleMute() {

        if (isMuted) {

            val restoreVolume =
                volumeBeforeMute.coerceIn(
                    1,
                    maxVolume
                )

            setVolume(
                restoreVolume
            )

        } else {

            volumeBeforeMute =
                currentVolume.coerceAtLeast(1)

            setVolume(0)
        }
    }

    /*
     * Brightness helpers.
     */
    fun applyBrightness(
        value: Float
    ) {

        val safeBrightness =
            value.coerceIn(
                0.05f,
                1.0f
            )

        activity?.let { currentActivity ->

            val params =
                currentActivity.window.attributes

            params.screenBrightness =
                safeBrightness

            currentActivity.window.attributes =
                params
        }

        currentBrightness =
            safeBrightness

        Log.d(
            "StreamBoxControls",
            "Brightness changed: $safeBrightness"
        )
    }

    fun brightnessUp() {

        applyBrightness(
            currentBrightness + 0.10f
        )
    }

    fun brightnessDown() {

        applyBrightness(
            currentBrightness - 0.10f
        )
    }

    /*
     * Enter Android Picture-in-Picture.
     *
     * The player is NOT stopped before entering PiP.
     */
    fun enterPictureInPicture() {

        Log.d(
            "StreamBoxPiP",
            "================================"
        )

        Log.d(
            "StreamBoxPiP",
            "PiP requested"
        )

        Log.d(
            "StreamBoxPiP",
            "Activity available: ${activity != null}"
        )

        Log.d(
            "StreamBoxPiP",
            "Player active: ${mainActivity?.isPlayerActive}"
        )

        Log.d(
            "StreamBoxPiP",
            "Player isPlaying: $isPlaying"
        )

        Log.d(
            "StreamBoxPiP",
            "Player playbackState: " +
                    streamPlayer.player.playbackState
        )

        if (activity == null) {

            Log.w(
                "StreamBoxPiP",
                "Cannot enter PiP: Activity is null"
            )

            onBack()

            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            Log.w(
                "StreamBoxPiP",
                "PiP requires Android 8.0 or higher"
            )

            onBack()

            return
        }

        if (activity.isInPictureInPictureMode) {

            Log.d(
                "StreamBoxPiP",
                "Already in Picture-in-Picture"
            )

            return
        }

        try {

            val params =
                PictureInPictureParams.Builder()
                    .setAspectRatio(
                        Rational(16, 9)
                    )
                    .build()

            Log.d(
                "StreamBoxPiP",
                "Calling enterPictureInPictureMode()"
            )

            val entered =
                activity.enterPictureInPictureMode(
                    params
                )

            Log.d(
                "StreamBoxPiP",
                "PiP requested. entered=$entered"
            )

            Log.d(
                "StreamBoxPiP",
                "================================"
            )

        } catch (exception: Exception) {

            Log.e(
                "StreamBoxPiP",
                "Failed to enter Picture-in-Picture",
                exception
            )

            onBack()
        }
    }

    /*
     * Android Back button / gesture.
     *
     * Back is normal navigation.
     * PiP is entered from Home through MainActivity.onUserLeaveHint().
     */
    BackHandler {

        Log.d(
            "StreamBoxPiP",
            "ANDROID BACK PRESSED -> normal navigation"
        )

        onBack()
    }

    /*
     * Keep screen awake.
     */
    DisposableEffect(activity) {

        Log.d(
            "StreamBoxPlayer",
            "Enabling KEEP_SCREEN_ON"
        )

        activity?.window?.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        onDispose {

            Log.d(
                "StreamBoxPlayer",
                "Disabling KEEP_SCREEN_ON"
            )

            activity?.window?.clearFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
    }

    /*
     * Player listener.
     */
    DisposableEffect(streamPlayer) {

        val listener =
            object : Player.Listener {

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {

                    when (playbackState) {

                        Player.STATE_IDLE -> {

                            Log.d(
                                "StreamBoxPlayer",
                                "Playback state: IDLE"
                            )

                            isBuffering = false
                        }

                        Player.STATE_BUFFERING -> {

                            Log.d(
                                "StreamBoxPlayer",
                                "Playback state: BUFFERING"
                            )

                            isBuffering = true
                        }

                        Player.STATE_READY -> {

                            Log.d(
                                "StreamBoxPlayer",
                                "Playback state: READY"
                            )

                            isBuffering = false
                        }

                        Player.STATE_ENDED -> {

                            Log.d(
                                "StreamBoxPlayer",
                                "Playback state: ENDED"
                            )

                            isBuffering = false

                            if (!currentChannel.isLive) {
                                playbackProgressScope.launch {
                                    try {
                                        deletePlaybackProgressUseCase(
                                            currentChannel.id
                                        )

                                        Log.d(
                                            "StreamBoxContinueWatching",
                                            "Playback completed -> removed: ${currentChannel.id}"
                                        )
                                    } catch (exception: Exception) {
                                        Log.e(
                                            "StreamBoxContinueWatching",
                                            "Failed to remove completed progress: ${currentChannel.id}",
                                            exception
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onIsPlayingChanged(
                    playing: Boolean
                ) {

                    Log.d(
                        "StreamBoxPlayer",
                        "Is playing: $playing"
                    )

                    isPlaying = playing
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {

                    Log.e(
                        "StreamBoxPlayer",
                        "PLAYER ERROR",
                        error
                    )

                    isBuffering = false

                    isPlaying = false

                    playbackError = error
                }
            }

        streamPlayer.addListener(
            listener
        )

        onDispose {

            streamPlayer.removeListener(
                listener
            )
        }
    }

    /*
     * Play whenever channel changes.
     */
    LaunchedEffect(currentChannelId) {

        Log.d(
            "StreamBoxPlayer",
            "================================"
        )

        Log.d(
            "StreamBoxPlayer",
            "CHANNEL CHANGED"
        )

        Log.d(
            "StreamBoxPlayer",
            "Channel ID: ${currentChannel.id}"
        )

        Log.d(
            "StreamBoxPlayer",
            "Channel Name: ${currentChannel.name}"
        )

        Log.d(
            "StreamBoxPlayer",
            "Channel Index: $currentIndex"
        )

        Log.d(
            "StreamBoxPlayer",
            "Total Channels: ${channels.size}"
        )

        Log.d(
            "StreamBoxPlayer",
            "Stream URL: ${currentChannel.streamUrl}"
        )

        Log.d(
            "StreamBoxPlayer",
            "================================"
        )

        playbackError = null

        isBuffering = false

        isPlaying = false

        val url =
            currentChannel.streamUrl

        if (!url.isNullOrBlank()) {

            Log.d(
                "StreamBoxPlayer",
                "Starting channel playback"
            )

            isBuffering = true

            var resumePositionMs = 0L

            if (!currentChannel.isLive) {
                try {
                    val savedProgress =
                        getPlaybackProgressUseCase(
                            currentChannel.id
                        )

                    if (savedProgress != null &&
                        savedProgress.positionMs > 0L &&
                        savedProgress.durationMs > 0L &&
                        savedProgress.positionMs < savedProgress.durationMs
                    ) {
                        resumePositionMs =
                            savedProgress.positionMs

                        Log.d(
                            "StreamBoxContinueWatching",
                            "RESUMING ${currentChannel.id} at ${resumePositionMs}ms"
                        )
                    } else {
                        Log.d(
                            "StreamBoxContinueWatching",
                            "No valid saved position for ${currentChannel.id}"
                        )
                    }
                } catch (exception: Exception) {
                    Log.e(
                        "StreamBoxContinueWatching",
                        "Failed to load saved position: ${currentChannel.id}",
                        exception
                    )
                }
            } else {
                Log.d(
                    "StreamBoxContinueWatching",
                    "Live channel -> resume disabled: ${currentChannel.id}"
                )
            }

            streamPlayer.play(
                streamUrl = url,
                startPositionMs = resumePositionMs
            )

        } else {

            Log.w(
                "StreamBoxPlayer",
                "Channel has no playable stream"
            )

            isBuffering = false
        }
    }

    /*
     * Save VOD playback progress while playing.
     *
     * Live channels are intentionally excluded because their
     * playback position is not a meaningful resume position.
     */
    LaunchedEffect(
        currentChannelId,
        isPlaying
    ) {

        if (currentChannel.isLive || !isPlaying) {
            return@LaunchedEffect
        }

        while (true) {

            kotlinx.coroutines.delay(5_000L)

            val positionMs =
                streamPlayer.player.currentPosition

            val durationMs =
                streamPlayer.player.duration

            if (positionMs > 0L &&
                durationMs > 0L && positionMs < durationMs
            ) {
                try {
                    savePlaybackProgressUseCase(
                        channelId = currentChannel.id,
                        positionMs = positionMs,
                        durationMs = durationMs
                    )

                    Log.d(
                        "StreamBoxContinueWatching",
                        "PROGRESS SAVED: ${currentChannel.id} $positionMs/$durationMs"
                    )
                } catch (exception: Exception) {
                    Log.e(
                        "StreamBoxContinueWatching",
                        "Failed to save progress: ${currentChannel.id}",
                        exception
                    )
                }
            }
        }
    }

    /*
     * Save the latest VOD position when the current channel changes
     * or PlayerScreen is disposed.
     */
    DisposableEffect(currentChannelId) {

        onDispose {

            if (!currentChannel.isLive) {

                val positionMs =
                    streamPlayer.player.currentPosition

                val durationMs =
                    streamPlayer.player.duration

                if (positionMs > 0L &&
                    durationMs > 0L && positionMs < durationMs
                ) {
                    playbackProgressScope.launch {
                        try {
                            savePlaybackProgressUseCase(
                                channelId = currentChannel.id,
                                positionMs = positionMs,
                                durationMs = durationMs
                            )

                            Log.d(
                                "StreamBoxContinueWatching",
                                "FINAL PROGRESS SAVED: ${currentChannel.id} $positionMs/$durationMs"
                            )
                        } catch (exception: Exception) {
                            Log.e(
                                "StreamBoxContinueWatching",
                                "Failed to save final progress: ${currentChannel.id}",
                                exception
                            )
                        }
                    }
                }
            }
        }
    }

    /*
     * Previous channel.
     */
    fun playPreviousChannel() {

        Log.d(
            "StreamBoxPlayer",
            "PREVIOUS CHANNEL CLICKED"
        )

        Log.d(
            "StreamBoxPlayer",
            "Current index: $currentIndex"
        )

        if (currentIndex <= 0) {

            Log.d(
                "StreamBoxPlayer",
                "Already at first channel"
            )

            return
        }

        val previousIndex =
            currentIndex - 1

        val previousChannel =
            channels[previousIndex]

        Log.d(
            "StreamBoxPlayer",
            "Switching to previous: " +
                    previousChannel.name
        )

        currentChannelId =
            previousChannel.id
    }

    /*
     * Next channel.
     */
    fun playNextChannel() {

        Log.d(
            "StreamBoxPlayer",
            "NEXT CHANNEL CLICKED"
        )

        Log.d(
            "StreamBoxPlayer",
            "Current index: $currentIndex"
        )

        if (
            currentIndex < 0 ||
            currentIndex >= channels.lastIndex
        ) {

            Log.d(
                "StreamBoxPlayer",
                "Already at last channel"
            )

            return
        }

        val nextIndex =
            currentIndex + 1

        val nextChannel =
            channels[nextIndex]

        Log.d(
            "StreamBoxPlayer",
            "Switching to next: " +
                    nextChannel.name
        )

        currentChannelId =
            nextChannel.id
    }

    /*
     * Release player ONLY when PlayerScreen is actually
     * removed/destroyed.
     *
     * IMPORTANT:
     *
     * This effect is intentionally NOT tied to PiP state.
     *
     * Entering or exiting PiP must not release the player.
     */
    DisposableEffect(streamPlayer) {

        onDispose {

            Log.d(
                "StreamBoxPlayer",
                "PlayerScreen disposed -> releasing player"
            )

            try {

                streamPlayer.stop()

            } catch (exception: Exception) {

                Log.w(
                    "StreamBoxPlayer",
                    "Player stop failed during cleanup",
                    exception
                )
            }

            try {

                streamPlayer.release()

            } catch (exception: Exception) {

                Log.w(
                    "StreamBoxPlayer",
                    "Player release failed during cleanup",
                    exception
                )
            }

            Log.d(
                "StreamBoxPlayer",
                "Player released"
            )
        }
    }

    /*
     * UI.
     */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        /*
         * Media3 Player.
         */
        AndroidView(
            modifier = Modifier.fillMaxSize(),

            factory = { viewContext ->

                StreamBoxPlayerView(
                    viewContext
                ).apply {

                    player =
                        streamPlayer.player

                    useController = false

                    keepScreenOn = true

                    isFocusable = true

                    isFocusableInTouchMode = true

                    onPreviousChannel = {

                        Log.d(
                            "StreamBoxPlayer",
                            "PlayerView -> PREVIOUS"
                        )

                        playPreviousChannel()
                    }

                    onNextChannel = {

                        Log.d(
                            "StreamBoxPlayer",
                            "PlayerView -> NEXT"
                        )

                        playNextChannel()
                    }

                    onBackPressed = {

                        Log.d(
                            "StreamBoxPlayer",
                            "PlayerView -> BACK -> normal navigation"
                        )

                        onBack()
                    }

                    post {

                        requestFocus()

                        Log.d(
                            "StreamBoxPlayer",
                            "PlayerView focus: ${hasFocus()}"
                        )
                    }
                }
            },

            update = { playerView ->

                if (
                    playerView.player !==
                    streamPlayer.player
                ) {

                    playerView.player =
                        streamPlayer.player
                }

                playerView.keepScreenOn = true

                playerView.onPreviousChannel = {
                    playPreviousChannel()
                }

                playerView.onNextChannel = {
                    playNextChannel()
                }

                playerView.onBackPressed = {
                    onBack()
                }
            }
        )

        /*
         * Everything below this point is intentionally hidden
         * while Android is displaying the activity in PiP.
         */
        if (!isInPipMode) {

            /*
             * Channel information.
             */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(
                        horizontal = 24.dp,
                        vertical = 20.dp
                    )
            ) {

                Text(
                    text = currentChannel.name,
                    style =
                        MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "● LIVE",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier.padding(
                            top = 4.dp
                        )
                )
            }

            /*
             * Buffering.
             */
            if (
                isBuffering &&
                playbackError == null
            ) {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(56.dp)
                        )

                        Text(
                            text = "Buffering...",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            modifier =
                                Modifier.padding(
                                    top = 16.dp
                                )
                        )
                    }
                }
            }

            /*
             * Error.
             */
            if (playbackError != null) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(
                                alpha = 0.90f
                            )
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment =
                            Alignment.CenterHorizontally,
                        verticalArrangement =
                            Arrangement.Center
                    ) {

                        Text(
                            text =
                                "Unable to play stream",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall
                        )

                        Text(
                            text =
                                "The stream may be temporarily unavailable.",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyLarge,
                            modifier =
                                Modifier.padding(
                                    top = 12.dp
                                ),
                            textAlign =
                                TextAlign.Center
                        )

                        Button(
                            onClick = {

                                Log.d(
                                    "StreamBoxPlayer",
                                    "RETRY clicked"
                                )

                                playbackError = null

                                isBuffering = true

                                isPlaying = false

                                val url =
                                    currentChannel.streamUrl

                                if (
                                    !url.isNullOrBlank()
                                ) {

                                    streamPlayer.play(
                                        url
                                    )

                                } else {

                                    isBuffering = false
                                }
                            },
                            modifier =
                                Modifier.padding(
                                    top = 32.dp
                                )
                        ) {

                            Text("RETRY")
                        }

                        Button(
                            onClick = {

                                Log.d(
                                    "StreamBoxPlayer",
                                    "ERROR MINIMIZE clicked"
                                )

                                enterPictureInPicture()
                            },
                            modifier =
                                Modifier.padding(
                                    top = 16.dp
                                )
                        ) {

                            Text("MINIMIZE")
                        }
                    }
                }
            }

            /*
             * Netflix-style player controls.
             *
             * IMPORTANT:
             *
             * - One bottom overlay only.
             * - Volume uses one level bar with - / +.
             * - Brightness uses one level bar with - / +.
             * - The panel is above the navigation area so controls
             *   are never clipped at the bottom.
             */
            if (playbackError == null) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 20.dp
                        )
                        .align(Alignment.BottomCenter),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 1180.dp)
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background(
                                Color.Black.copy(
                                    alpha = 0.72f
                                )
                            )
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            ),
                        horizontalArrangement =
                            Arrangement.Center,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        /*
                         * Channel / playback.
                         */
                        NetflixControlButton(
                            text = "‹",
                            label = "PREV",
                            enabled = currentIndex > 0
                        ) {
                            playPreviousChannel()
                        }

                        NetflixControlButton(
                            text = if (isPlaying) {
                                "Ⅱ"
                            } else {
                                "▶"
                            },
                            label = if (isPlaying) {
                                "PAUSE"
                            } else {
                                "PLAY"
                            }
                        ) {
                            if (isPlaying) {
                                streamPlayer.pause()
                            } else {
                                streamPlayer.resume()
                            }
                        }

                        NetflixControlButton(
                            text = "›",
                            label = "NEXT",
                            enabled =
                                currentIndex >= 0 &&
                                        currentIndex < channels.lastIndex
                        ) {
                            playNextChannel()
                        }

                        NetflixControlDivider()

                        /*
                         * Volume:
                         *
                         * - / progress bar / +
                         */
                        NetflixLevelBar(
                            label = "VOLUME",
                            valueText =
                                "$currentVolume/$maxVolume",
                            progress =
                                if (maxVolume > 0) {
                                    currentVolume.toFloat() /
                                            maxVolume.toFloat()
                                } else {
                                    0f
                                },
                            decreaseEnabled =
                                currentVolume > 0,
                            increaseEnabled =
                                currentVolume < maxVolume,
                            onDecrease = {
                                volumeDown()
                            },
                            onIncrease = {
                                volumeUp()
                            }
                        )

                        NetflixControlButton(
                            text = if (isMuted) {
                                "🔇"
                            } else {
                                "🔊"
                            },
                            label = if (isMuted) {
                                "UNMUTE"
                            } else {
                                "MUTE"
                            }
                        ) {
                            toggleMute()
                        }

                        NetflixControlDivider()

                        /*
                         * Brightness:
                         *
                         * - / progress bar / +
                         */
                        NetflixLevelBar(
                            label = "BRIGHTNESS",
                            valueText =
                                "${(
                                        currentBrightness * 100
                                        ).toInt()}%",
                            progress =
                                currentBrightness.coerceIn(
                                    0f,
                                    1f
                                ),
                            decreaseEnabled =
                                currentBrightness > 0.05f,
                            increaseEnabled =
                                currentBrightness < 1.0f,
                            onDecrease = {
                                brightnessDown()
                            },
                            onIncrease = {
                                brightnessUp()
                            }
                        )

                        NetflixControlDivider()

                        /*
                         * Explicit PiP button is still available.
                         *
                         * Home also enters PiP automatically.
                         */
                        NetflixControlButton(
                            text = "⤢",
                            label = "PIP"
                        ) {
                            enterPictureInPicture()
                        }
                    }
                }
            }
        }
    }
}

//@file:OptIn(androidx.media3.common.util.UnstableApi::class)
//
//package com.allay.streambox.feature.player
//
//import android.app.Activity
//import android.app.PictureInPictureParams
//import android.content.Context
//import android.media.AudioManager
//import android.os.Build
//import android.util.AttributeSet
//import android.util.Log
//import android.util.Rational
//import android.view.KeyEvent
//import android.view.WindowManager
//import androidx.activity.compose.BackHandler
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.focusable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.heightIn
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.widthIn
//import androidx.compose.foundation.layout.navigationBarsPadding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.alpha
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.media3.common.PlaybackException
//import androidx.media3.common.Player
//import androidx.media3.ui.PlayerView
//import androidx.tv.material3.Button
//import androidx.tv.material3.MaterialTheme
//import com.allay.streambox.MainActivity
//import com.allay.streambox.feature.channels.ChannelItem
//
///**
// * Custom PlayerView for Android TV D-pad support.
// *
// * Media3 controller is disabled.
// * We use our own Previous / Next controls.
// */
//class StreamBoxPlayerView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : PlayerView(context, attrs) {
//
//    var onPreviousChannel: (() -> Unit)? = null
//
//    var onNextChannel: (() -> Unit)? = null
//
//    var onBackPressed: (() -> Unit)? = null
//
//    override fun dispatchKeyEvent(
//        event: KeyEvent
//    ): Boolean {
//
//        if (event.action != KeyEvent.ACTION_DOWN) {
//            return super.dispatchKeyEvent(event)
//        }
//
//        Log.d(
//            "StreamBoxPlayer",
//            "KEY EVENT RECEIVED: ${event.keyCode}"
//        )
//
//        when (event.keyCode) {
//
//            KeyEvent.KEYCODE_DPAD_LEFT,
//            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
//
//                Log.d(
//                    "StreamBoxPlayer",
//                    "D-PAD LEFT / PREVIOUS"
//                )
//
//                onPreviousChannel?.invoke()
//
//                return true
//            }
//
//            KeyEvent.KEYCODE_DPAD_RIGHT,
//            KeyEvent.KEYCODE_MEDIA_NEXT -> {
//
//                Log.d(
//                    "StreamBoxPlayer",
//                    "D-PAD RIGHT / NEXT"
//                )
//
//                onNextChannel?.invoke()
//
//                return true
//            }
//
//            KeyEvent.KEYCODE_BACK -> {
//
//                Log.d(
//                    "StreamBoxPlayer",
//                    "BACK KEY"
//                )
//
//                onBackPressed?.invoke()
//
//                return true
//            }
//        }
//
//        return super.dispatchKeyEvent(event)
//    }
//}
//
//@Composable
//private fun NetflixControlButton(
//    text: String,
//    label: String,
//    enabled: Boolean = true,
//    onClick: () -> Unit
//) {
//    var isFocused by remember {
//        mutableStateOf(false)
//    }
//
//    Column(
//        modifier = Modifier
//            .widthIn(min = 58.dp, max = 92.dp)
//            .alpha(if (enabled) 1f else 0.35f)
//            .onFocusChanged {
//                isFocused = it.isFocused
//            }
//            .focusable(enabled = enabled)
//            .clickable(
//                enabled = enabled,
//                onClick = onClick
//            )
//            .padding(
//                horizontal = 6.dp,
//                vertical = 3.dp
//            ),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = text,
//            style = MaterialTheme.typography.headlineSmall,
//            color = if (isFocused) {
//                Color.White
//            } else {
//                Color.White.copy(alpha = 0.95f)
//            },
//            textAlign = TextAlign.Center
//        )
//
//        if (label.isNotBlank()) {
//            Text(
//                text = label,
//                style = MaterialTheme.typography.labelSmall,
//                color = Color.White.copy(alpha = 0.80f),
//                textAlign = TextAlign.Center
//            )
//        }
//    }
//}
//
//
//@Composable
//private fun NetflixControlDivider() {
//    Box(
//        modifier = Modifier
//            .padding(horizontal = 2.dp)
//            .widthIn(min = 1.dp, max = 1.dp)
//            .height(38.dp)
//            .background(
//                Color.White.copy(alpha = 0.22f)
//            )
//    )
//}
//
//
//@Composable
//private fun NetflixLevelBar(
//    label: String,
//    valueText: String,
//    progress: Float,
//    decreaseEnabled: Boolean,
//    increaseEnabled: Boolean,
//    onDecrease: () -> Unit,
//    onIncrease: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .widthIn(min = 210.dp, max = 280.dp)
//            .padding(
//                horizontal = 6.dp,
//                vertical = 2.dp
//            )
//    ) {
//        Text(
//            text = "$label  $valueText",
//            style = MaterialTheme.typography.labelSmall,
//            color = Color.White.copy(alpha = 0.82f),
//            textAlign = TextAlign.Center,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 2.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            NetflixControlButton(
//                text = "−",
//                label = "",
//                enabled = decreaseEnabled,
//                onClick = onDecrease
//            )
//
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .height(8.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(
//                        Color.White.copy(alpha = 0.25f)
//                    )
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth(
//                            progress.coerceIn(0f, 1f)
//                        )
//                        .height(8.dp)
//                        .clip(RoundedCornerShape(8.dp))
//                        .background(Color.White)
//                )
//            }
//
//            NetflixControlButton(
//                text = "+",
//                label = "",
//                enabled = increaseEnabled,
//                onClick = onIncrease
//            )
//        }
//    }
//}
//
//
//@Composable
//fun PlayerScreen(
//    channelId: String,
//    channels: List<ChannelItem>,
//    onBack: () -> Unit
//) {
//
//    val context = LocalContext.current
//
//    val activity = context as? Activity
//
//    val mainActivity = activity as? MainActivity
//
//    /*
//     * PiP state.
//     *
//     * MainActivity owns this state and updates it from
//     * onPictureInPictureModeChanged().
//     *
//     * IMPORTANT:
//     *
//     * We do NOT release the player when this becomes false.
//     *
//     * False simply means the user returned from PiP.
//     */
//    val isInPipMode =
//        mainActivity?.isInPipMode == true
//
//    /*
//     * System media volume.
//     */
//    val audioManager =
//        remember(context) {
//            context.getSystemService(
//                Context.AUDIO_SERVICE
//            ) as AudioManager
//        }
//
//    var currentVolume by remember {
//        mutableStateOf(
//            audioManager.getStreamVolume(
//                AudioManager.STREAM_MUSIC
//            )
//        )
//    }
//
//    val maxVolume =
//        remember(audioManager) {
//            audioManager.getStreamMaxVolume(
//                AudioManager.STREAM_MUSIC
//            )
//        }
//
//    var isMuted by remember {
//        mutableStateOf(
//            currentVolume == 0
//        )
//    }
//
//    var volumeBeforeMute by remember {
//        mutableStateOf(
//            currentVolume.coerceAtLeast(1)
//        )
//    }
//
//    /*
//     * App-window brightness.
//     *
//     * This changes only the StreamBox window.
//     */
//    var currentBrightness by remember {
//        mutableStateOf(
//            activity
//                ?.window
//                ?.attributes
//                ?.screenBrightness
//                ?.takeIf { it >= 0f }
//                ?: 0.5f
//        )
//    }
//
//    /*
//     * Register PlayerScreen as the active player.
//     */
//    DisposableEffect(mainActivity) {
//
//        Log.d(
//            "StreamBoxPiP",
//            "PlayerScreen ACTIVE"
//        )
//
//        mainActivity?.isPlayerActive = true
//
//        onDispose {
//
//            Log.d(
//                "StreamBoxPiP",
//                "PlayerScreen DISPOSED"
//            )
//
//            mainActivity?.isPlayerActive = false
//        }
//    }
//
//    /*
//     * Current channel.
//     */
//    var currentChannelId by remember {
//        mutableStateOf(channelId)
//    }
//
//    /*
//     * Find current channel.
//     */
//    val currentChannel =
//        channels.firstOrNull { channel ->
//            channel.id == currentChannelId
//        }
//
//    /*
//     * Current channel index.
//     */
//    val currentIndex =
//        channels.indexOfFirst { channel ->
//            channel.id == currentChannelId
//        }
//
//    /*
//     * If channel does not exist,
//     * return to previous screen.
//     */
//    if (currentChannel == null) {
//
//        Log.e(
//            "StreamBoxPlayer",
//            "Current channel not found: $currentChannelId"
//        )
//
//        LaunchedEffect(Unit) {
//            onBack()
//        }
//
//        return
//    }
//
//    /*
//     * Create player once.
//     */
//    val streamPlayer = remember {
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Creating StreamPlayer"
//        )
//
//        StreamPlayer(context)
//    }
//
//    /*
//     * Player state.
//     */
//    var isBuffering by remember {
//        mutableStateOf(false)
//    }
//
//    var isPlaying by remember {
//        mutableStateOf(false)
//    }
//
//    var playbackError by remember {
//        mutableStateOf<PlaybackException?>(null)
//    }
//
//    /*
//     * Volume helpers.
//     */
//    fun setVolume(
//        value: Int
//    ) {
//
//        val safeVolume =
//            value.coerceIn(
//                0,
//                maxVolume
//            )
//
//        audioManager.setStreamVolume(
//            AudioManager.STREAM_MUSIC,
//            safeVolume,
//            0
//        )
//
//        currentVolume = safeVolume
//
//        isMuted =
//            safeVolume == 0
//
//        if (safeVolume > 0) {
//            volumeBeforeMute = safeVolume
//        }
//
//        Log.d(
//            "StreamBoxControls",
//            "Volume changed: $safeVolume/$maxVolume"
//        )
//    }
//
//    fun volumeUp() {
//
//        setVolume(
//            currentVolume + 1
//        )
//    }
//
//    fun volumeDown() {
//
//        setVolume(
//            currentVolume - 1
//        )
//    }
//
//    fun toggleMute() {
//
//        if (isMuted) {
//
//            val restoreVolume =
//                volumeBeforeMute.coerceIn(
//                    1,
//                    maxVolume
//                )
//
//            setVolume(
//                restoreVolume
//            )
//
//        } else {
//
//            volumeBeforeMute =
//                currentVolume.coerceAtLeast(1)
//
//            setVolume(0)
//        }
//    }
//
//    /*
//     * Brightness helpers.
//     */
//    fun applyBrightness(
//        value: Float
//    ) {
//
//        val safeBrightness =
//            value.coerceIn(
//                0.05f,
//                1.0f
//            )
//
//        activity?.let { currentActivity ->
//
//            val params =
//                currentActivity.window.attributes
//
//            params.screenBrightness =
//                safeBrightness
//
//            currentActivity.window.attributes =
//                params
//        }
//
//        currentBrightness =
//            safeBrightness
//
//        Log.d(
//            "StreamBoxControls",
//            "Brightness changed: $safeBrightness"
//        )
//    }
//
//    fun brightnessUp() {
//
//        applyBrightness(
//            currentBrightness + 0.10f
//        )
//    }
//
//    fun brightnessDown() {
//
//        applyBrightness(
//            currentBrightness - 0.10f
//        )
//    }
//
//    /*
//     * Enter Android Picture-in-Picture.
//     *
//     * The player is NOT stopped before entering PiP.
//     */
//    fun enterPictureInPicture() {
//
//        Log.d(
//            "StreamBoxPiP",
//            "================================"
//        )
//
//        Log.d(
//            "StreamBoxPiP",
//            "PiP requested"
//        )
//
//        Log.d(
//            "StreamBoxPiP",
//            "Activity available: ${activity != null}"
//        )
//
//        Log.d(
//            "StreamBoxPiP",
//            "Player active: ${mainActivity?.isPlayerActive}"
//        )
//
//        Log.d(
//            "StreamBoxPiP",
//            "Player isPlaying: $isPlaying"
//        )
//
//        Log.d(
//            "StreamBoxPiP",
//            "Player playbackState: " +
//                    streamPlayer.player.playbackState
//        )
//
//        if (activity == null) {
//
//            Log.w(
//                "StreamBoxPiP",
//                "Cannot enter PiP: Activity is null"
//            )
//
//            onBack()
//
//            return
//        }
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//
//            Log.w(
//                "StreamBoxPiP",
//                "PiP requires Android 8.0 or higher"
//            )
//
//            onBack()
//
//            return
//        }
//
//        if (activity.isInPictureInPictureMode) {
//
//            Log.d(
//                "StreamBoxPiP",
//                "Already in Picture-in-Picture"
//            )
//
//            return
//        }
//
//        try {
//
//            val params =
//                PictureInPictureParams.Builder()
//                    .setAspectRatio(
//                        Rational(16, 9)
//                    )
//                    .build()
//
//            Log.d(
//                "StreamBoxPiP",
//                "Calling enterPictureInPictureMode()"
//            )
//
//            val entered =
//                activity.enterPictureInPictureMode(
//                    params
//                )
//
//            Log.d(
//                "StreamBoxPiP",
//                "PiP requested. entered=$entered"
//            )
//
//            Log.d(
//                "StreamBoxPiP",
//                "================================"
//            )
//
//        } catch (exception: Exception) {
//
//            Log.e(
//                "StreamBoxPiP",
//                "Failed to enter Picture-in-Picture",
//                exception
//            )
//
//            onBack()
//        }
//    }
//
//    /*
//     * Android Back button / gesture.
//     *
//     * Back is normal navigation.
//     * PiP is entered from Home through MainActivity.onUserLeaveHint().
//     */
//    BackHandler {
//
//        Log.d(
//            "StreamBoxPiP",
//            "ANDROID BACK PRESSED -> normal navigation"
//        )
//
//        onBack()
//    }
//
//    /*
//     * Keep screen awake.
//     */
//    DisposableEffect(activity) {
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Enabling KEEP_SCREEN_ON"
//        )
//
//        activity?.window?.addFlags(
//            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//        )
//
//        onDispose {
//
//            Log.d(
//                "StreamBoxPlayer",
//                "Disabling KEEP_SCREEN_ON"
//            )
//
//            activity?.window?.clearFlags(
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//            )
//        }
//    }
//
//    /*
//     * Player listener.
//     */
//    DisposableEffect(streamPlayer) {
//
//        val listener =
//            object : Player.Listener {
//
//                override fun onPlaybackStateChanged(
//                    playbackState: Int
//                ) {
//
//                    when (playbackState) {
//
//                        Player.STATE_IDLE -> {
//
//                            Log.d(
//                                "StreamBoxPlayer",
//                                "Playback state: IDLE"
//                            )
//
//                            isBuffering = false
//                        }
//
//                        Player.STATE_BUFFERING -> {
//
//                            Log.d(
//                                "StreamBoxPlayer",
//                                "Playback state: BUFFERING"
//                            )
//
//                            isBuffering = true
//                        }
//
//                        Player.STATE_READY -> {
//
//                            Log.d(
//                                "StreamBoxPlayer",
//                                "Playback state: READY"
//                            )
//
//                            isBuffering = false
//                        }
//
//                        Player.STATE_ENDED -> {
//
//                            Log.d(
//                                "StreamBoxPlayer",
//                                "Playback state: ENDED"
//                            )
//
//                            isBuffering = false
//                        }
//                    }
//                }
//
//                override fun onIsPlayingChanged(
//                    playing: Boolean
//                ) {
//
//                    Log.d(
//                        "StreamBoxPlayer",
//                        "Is playing: $playing"
//                    )
//
//                    isPlaying = playing
//                }
//
//                override fun onPlayerError(
//                    error: PlaybackException
//                ) {
//
//                    Log.e(
//                        "StreamBoxPlayer",
//                        "PLAYER ERROR",
//                        error
//                    )
//
//                    isBuffering = false
//
//                    isPlaying = false
//
//                    playbackError = error
//                }
//            }
//
//        streamPlayer.addListener(
//            listener
//        )
//
//        onDispose {
//
//            streamPlayer.removeListener(
//                listener
//            )
//        }
//    }
//
//    /*
//     * Play whenever channel changes.
//     */
//    LaunchedEffect(currentChannelId) {
//
//        Log.d(
//            "StreamBoxPlayer",
//            "================================"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "CHANNEL CHANGED"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Channel ID: ${currentChannel.id}"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Channel Name: ${currentChannel.name}"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Channel Index: $currentIndex"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Total Channels: ${channels.size}"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Stream URL: ${currentChannel.streamUrl}"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "================================"
//        )
//
//        playbackError = null
//
//        isBuffering = false
//
//        isPlaying = false
//
//        val url =
//            currentChannel.streamUrl
//
//        if (!url.isNullOrBlank()) {
//
//            Log.d(
//                "StreamBoxPlayer",
//                "Starting channel playback"
//            )
//
//            isBuffering = true
//
//            streamPlayer.play(
//                url
//            )
//
//        } else {
//
//            Log.w(
//                "StreamBoxPlayer",
//                "Channel has no playable stream"
//            )
//
//            isBuffering = false
//        }
//    }
//
//    /*
//     * Previous channel.
//     */
//    fun playPreviousChannel() {
//
//        Log.d(
//            "StreamBoxPlayer",
//            "PREVIOUS CHANNEL CLICKED"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Current index: $currentIndex"
//        )
//
//        if (currentIndex <= 0) {
//
//            Log.d(
//                "StreamBoxPlayer",
//                "Already at first channel"
//            )
//
//            return
//        }
//
//        val previousIndex =
//            currentIndex - 1
//
//        val previousChannel =
//            channels[previousIndex]
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Switching to previous: " +
//                    previousChannel.name
//        )
//
//        currentChannelId =
//            previousChannel.id
//    }
//
//    /*
//     * Next channel.
//     */
//    fun playNextChannel() {
//
//        Log.d(
//            "StreamBoxPlayer",
//            "NEXT CHANNEL CLICKED"
//        )
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Current index: $currentIndex"
//        )
//
//        if (
//            currentIndex < 0 ||
//            currentIndex >= channels.lastIndex
//        ) {
//
//            Log.d(
//                "StreamBoxPlayer",
//                "Already at last channel"
//            )
//
//            return
//        }
//
//        val nextIndex =
//            currentIndex + 1
//
//        val nextChannel =
//            channels[nextIndex]
//
//        Log.d(
//            "StreamBoxPlayer",
//            "Switching to next: " +
//                    nextChannel.name
//        )
//
//        currentChannelId =
//            nextChannel.id
//    }
//
//    /*
//     * Release player ONLY when PlayerScreen is actually
//     * removed/destroyed.
//     *
//     * IMPORTANT:
//     *
//     * This effect is intentionally NOT tied to PiP state.
//     *
//     * Entering or exiting PiP must not release the player.
//     */
//    DisposableEffect(streamPlayer) {
//
//        onDispose {
//
//            Log.d(
//                "StreamBoxPlayer",
//                "PlayerScreen disposed -> releasing player"
//            )
//
//            try {
//
//                streamPlayer.stop()
//
//            } catch (exception: Exception) {
//
//                Log.w(
//                    "StreamBoxPlayer",
//                    "Player stop failed during cleanup",
//                    exception
//                )
//            }
//
//            try {
//
//                streamPlayer.release()
//
//            } catch (exception: Exception) {
//
//                Log.w(
//                    "StreamBoxPlayer",
//                    "Player release failed during cleanup",
//                    exception
//                )
//            }
//
//            Log.d(
//                "StreamBoxPlayer",
//                "Player released"
//            )
//        }
//    }
//
//    /*
//     * UI.
//     */
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//    ) {
//
//        /*
//         * Media3 Player.
//         */
//        AndroidView(
//            modifier = Modifier.fillMaxSize(),
//
//            factory = { viewContext ->
//
//                StreamBoxPlayerView(
//                    viewContext
//                ).apply {
//
//                    player =
//                        streamPlayer.player
//
//                    useController = false
//
//                    keepScreenOn = true
//
//                    isFocusable = true
//
//                    isFocusableInTouchMode = true
//
//                    onPreviousChannel = {
//
//                        Log.d(
//                            "StreamBoxPlayer",
//                            "PlayerView -> PREVIOUS"
//                        )
//
//                        playPreviousChannel()
//                    }
//
//                    onNextChannel = {
//
//                        Log.d(
//                            "StreamBoxPlayer",
//                            "PlayerView -> NEXT"
//                        )
//
//                        playNextChannel()
//                    }
//
//                    onBackPressed = {
//
//                        Log.d(
//                            "StreamBoxPlayer",
//                            "PlayerView -> BACK -> normal navigation"
//                        )
//
//                        onBack()
//                    }
//
//                    post {
//
//                        requestFocus()
//
//                        Log.d(
//                            "StreamBoxPlayer",
//                            "PlayerView focus: ${hasFocus()}"
//                        )
//                    }
//                }
//            },
//
//            update = { playerView ->
//
//                if (
//                    playerView.player !==
//                    streamPlayer.player
//                ) {
//
//                    playerView.player =
//                        streamPlayer.player
//                }
//
//                playerView.keepScreenOn = true
//
//                playerView.onPreviousChannel = {
//                    playPreviousChannel()
//                }
//
//                playerView.onNextChannel = {
//                    playNextChannel()
//                }
//
//                playerView.onBackPressed = {
//                    onBack()
//                }
//            }
//        )
//
//        /*
//         * Everything below this point is intentionally hidden
//         * while Android is displaying the activity in PiP.
//         */
//        if (!isInPipMode) {
//
//            /*
//             * Channel information.
//             */
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .align(Alignment.TopStart)
//                    .padding(
//                        horizontal = 24.dp,
//                        vertical = 20.dp
//                    )
//            ) {
//
//                Text(
//                    text = currentChannel.name,
//                    style =
//                        MaterialTheme.typography.titleLarge
//                )
//
//                Text(
//                    text = "● LIVE",
//                    style =
//                        MaterialTheme.typography.bodyMedium,
//                    modifier =
//                        Modifier.padding(
//                            top = 4.dp
//                        )
//                )
//            }
//
//            /*
//             * Buffering.
//             */
//            if (
//                isBuffering &&
//                playbackError == null
//            ) {
//
//                Box(
//                    modifier =
//                        Modifier.fillMaxSize(),
//                    contentAlignment =
//                        Alignment.Center
//                ) {
//
//                    Column(
//                        horizontalAlignment =
//                            Alignment.CenterHorizontally
//                    ) {
//
//                        CircularProgressIndicator(
//                            modifier =
//                                Modifier.size(56.dp)
//                        )
//
//                        Text(
//                            text = "Buffering...",
//                            style =
//                                MaterialTheme
//                                    .typography
//                                    .titleMedium,
//                            modifier =
//                                Modifier.padding(
//                                    top = 16.dp
//                                )
//                        )
//                    }
//                }
//            }
//
//            /*
//             * Error.
//             */
//            if (playbackError != null) {
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(
//                            Color.Black.copy(
//                                alpha = 0.90f
//                            )
//                        ),
//                    contentAlignment =
//                        Alignment.Center
//                ) {
//
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(48.dp),
//                        horizontalAlignment =
//                            Alignment.CenterHorizontally,
//                        verticalArrangement =
//                            Arrangement.Center
//                    ) {
//
//                        Text(
//                            text =
//                                "Unable to play stream",
//                            style =
//                                MaterialTheme
//                                    .typography
//                                    .headlineSmall
//                        )
//
//                        Text(
//                            text =
//                                "The stream may be temporarily unavailable.",
//                            style =
//                                MaterialTheme
//                                    .typography
//                                    .bodyLarge,
//                            modifier =
//                                Modifier.padding(
//                                    top = 12.dp
//                                ),
//                            textAlign =
//                                TextAlign.Center
//                        )
//
//                        Button(
//                            onClick = {
//
//                                Log.d(
//                                    "StreamBoxPlayer",
//                                    "RETRY clicked"
//                                )
//
//                                playbackError = null
//
//                                isBuffering = true
//
//                                isPlaying = false
//
//                                val url =
//                                    currentChannel.streamUrl
//
//                                if (
//                                    !url.isNullOrBlank()
//                                ) {
//
//                                    streamPlayer.play(
//                                        url
//                                    )
//
//                                } else {
//
//                                    isBuffering = false
//                                }
//                            },
//                            modifier =
//                                Modifier.padding(
//                                    top = 32.dp
//                                )
//                        ) {
//
//                            Text("RETRY")
//                        }
//
//                        Button(
//                            onClick = {
//
//                                Log.d(
//                                    "StreamBoxPlayer",
//                                    "ERROR MINIMIZE clicked"
//                                )
//
//                                enterPictureInPicture()
//                            },
//                            modifier =
//                                Modifier.padding(
//                                    top = 16.dp
//                                )
//                        ) {
//
//                            Text("MINIMIZE")
//                        }
//                    }
//                }
//            }
//
//            /*
//             * Netflix-style player controls.
//             *
//             * IMPORTANT:
//             *
//             * - One bottom overlay only.
//             * - Volume uses one level bar with - / +.
//             * - Brightness uses one level bar with - / +.
//             * - The panel is above the navigation area so controls
//             *   are never clipped at the bottom.
//             */
//            if (playbackError == null) {
//
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .navigationBarsPadding()
//                        .padding(
//                            start = 16.dp,
//                            end = 16.dp,
//                            bottom = 20.dp
//                        )
//                        .align(Alignment.BottomCenter),
//                    contentAlignment = Alignment.Center
//                ) {
//
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .widthIn(max = 1180.dp)
//                            .clip(
//                                RoundedCornerShape(20.dp)
//                            )
//                            .background(
//                                Color.Black.copy(
//                                    alpha = 0.72f
//                                )
//                            )
//                            .padding(
//                                horizontal = 12.dp,
//                                vertical = 8.dp
//                            ),
//                        horizontalArrangement =
//                            Arrangement.Center,
//                        verticalAlignment =
//                            Alignment.CenterVertically
//                    ) {
//
//                        /*
//                         * Channel / playback.
//                         */
//                        NetflixControlButton(
//                            text = "‹",
//                            label = "PREV",
//                            enabled = currentIndex > 0
//                        ) {
//                            playPreviousChannel()
//                        }
//
//                        NetflixControlButton(
//                            text = if (isPlaying) {
//                                "Ⅱ"
//                            } else {
//                                "▶"
//                            },
//                            label = if (isPlaying) {
//                                "PAUSE"
//                            } else {
//                                "PLAY"
//                            }
//                        ) {
//                            if (isPlaying) {
//                                streamPlayer.pause()
//                            } else {
//                                streamPlayer.resume()
//                            }
//                        }
//
//                        NetflixControlButton(
//                            text = "›",
//                            label = "NEXT",
//                            enabled =
//                                currentIndex >= 0 &&
//                                        currentIndex < channels.lastIndex
//                        ) {
//                            playNextChannel()
//                        }
//
//                        NetflixControlDivider()
//
//                        /*
//                         * Volume:
//                         *
//                         * - / progress bar / +
//                         */
//                        NetflixLevelBar(
//                            label = "VOLUME",
//                            valueText =
//                                "$currentVolume/$maxVolume",
//                            progress =
//                                if (maxVolume > 0) {
//                                    currentVolume.toFloat() /
//                                            maxVolume.toFloat()
//                                } else {
//                                    0f
//                                },
//                            decreaseEnabled =
//                                currentVolume > 0,
//                            increaseEnabled =
//                                currentVolume < maxVolume,
//                            onDecrease = {
//                                volumeDown()
//                            },
//                            onIncrease = {
//                                volumeUp()
//                            }
//                        )
//
//                        NetflixControlButton(
//                            text = if (isMuted) {
//                                "🔇"
//                            } else {
//                                "🔊"
//                            },
//                            label = if (isMuted) {
//                                "UNMUTE"
//                            } else {
//                                "MUTE"
//                            }
//                        ) {
//                            toggleMute()
//                        }
//
//                        NetflixControlDivider()
//
//                        /*
//                         * Brightness:
//                         *
//                         * - / progress bar / +
//                         */
//                        NetflixLevelBar(
//                            label = "BRIGHTNESS",
//                            valueText =
//                                "${(
//                                        currentBrightness * 100
//                                        ).toInt()}%",
//                            progress =
//                                currentBrightness.coerceIn(
//                                    0f,
//                                    1f
//                                ),
//                            decreaseEnabled =
//                                currentBrightness > 0.05f,
//                            increaseEnabled =
//                                currentBrightness < 1.0f,
//                            onDecrease = {
//                                brightnessDown()
//                            },
//                            onIncrease = {
//                                brightnessUp()
//                            }
//                        )
//
//                        NetflixControlDivider()
//
//                        /*
//                         * Explicit PiP button is still available.
//                         *
//                         * Home also enters PiP automatically.
//                         */
//                        NetflixControlButton(
//                            text = "⤢",
//                            label = "PIP"
//                        ) {
//                            enterPictureInPicture()
//                        }
//                    }
//                }
//            }
//        }
//    }
//}