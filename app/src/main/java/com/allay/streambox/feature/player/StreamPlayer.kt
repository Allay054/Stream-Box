//package com.allay.streambox.feature.player
//
//import android.util.Log
//import androidx.media3.common.MediaItem
//import androidx.media3.common.PlaybackException
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//
//class StreamPlayer(
//    context: android.content.Context
//) {
//
//    companion object {
//        private const val TAG = "StreamBoxPlayer"
//    }
//
//    private val applicationContext =
//        context.applicationContext
//
//    val player: ExoPlayer =
//        ExoPlayer.Builder(applicationContext)
//            .build()
//            .apply {
//                playWhenReady = false
//            }
//
//    init {
//        player.addListener(
//            object : Player.Listener {
//
//                override fun onPlaybackStateChanged(
//                    playbackState: Int
//                ) {
//                    val state = when (playbackState) {
//                        Player.STATE_IDLE -> "IDLE"
//                        Player.STATE_BUFFERING -> "BUFFERING"
//                        Player.STATE_READY -> "READY"
//                        Player.STATE_ENDED -> "ENDED"
//                        else -> "UNKNOWN"
//                    }
//
//                    Log.d(
//                        TAG,
//                        "Playback state: $state"
//                    )
//                }
//
//                override fun onIsPlayingChanged(
//                    isPlaying: Boolean
//                ) {
//                    Log.d(
//                        TAG,
//                        "Is playing: $isPlaying"
//                    )
//                }
//
//                override fun onPlayerError(
//                    error: PlaybackException
//                ) {
//                    Log.e(
//                        TAG,
//                        "PLAYER ERROR"
//                    )
//
//                    Log.e(
//                        TAG,
//                        "Error code: ${error.errorCode}"
//                    )
//
//                    Log.e(
//                        TAG,
//                        "Error name: ${error.errorCodeName}"
//                    )
//
//                    Log.e(
//                        TAG,
//                        "Error message: ${error.message}"
//                    )
//
//                    Log.e(
//                        TAG,
//                        "Cause: ${error.cause}"
//                    )
//
//                    error.printStackTrace()
//                }
//            }
//        )
//    }
//
//    fun play(streamUrl: String) {
//
//        Log.d(
//            TAG,
//            "================================"
//        )
//
//        Log.d(
//            TAG,
//            "PLAY REQUEST"
//        )
//
//        Log.d(
//            TAG,
//            "Stream URL: $streamUrl"
//        )
//
//        Log.d(
//            TAG,
//            "================================"
//        )
//
//        val mediaItem =
//            MediaItem.fromUri(streamUrl)
//
//        Log.d(
//            TAG,
//            "MediaItem URI: ${mediaItem.localConfiguration?.uri}"
//        )
//
//        player.setMediaItem(mediaItem)
//
//        Log.d(
//            TAG,
//            "Media item set"
//        )
//
//        player.prepare()
//
//        Log.d(
//            TAG,
//            "Player prepared"
//        )
//
//        player.playWhenReady = true
//
//        Log.d(
//            TAG,
//            "playWhenReady = ${player.playWhenReady}"
//        )
//    }
//
//    fun pause() {
//        Log.d(TAG, "Pause requested")
//        player.pause()
//    }
//
//    fun resume() {
//        Log.d(TAG, "Resume requested")
//        player.play()
//    }
//
//    fun stop() {
//        Log.d(TAG, "Stop requested")
//        player.stop()
//        player.clearMediaItems()
//    }
//
//    fun release() {
//        Log.d(TAG, "Player released")
//        player.release()
//    }
//
//    fun addListener(listener: Player.Listener) {
//        player.addListener(listener)
//    }
//
//    fun removeListener(listener: Player.Listener) {
//        player.removeListener(listener)
//    }
//}

package com.allay.streambox.feature.player

import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

class StreamPlayer(
    context: android.content.Context
) {

    companion object {
        private const val TAG = "StreamBoxPlayer"
    }

    private val applicationContext =
        context.applicationContext

    val player: ExoPlayer =
        ExoPlayer.Builder(applicationContext)
            .build()
            .apply {
                playWhenReady = false
            }

    init {
        player.addListener(
            object : Player.Listener {

                override fun onPlaybackStateChanged(
                    playbackState: Int
                ) {
                    val state = when (playbackState) {
                        Player.STATE_IDLE -> "IDLE"
                        Player.STATE_BUFFERING -> "BUFFERING"
                        Player.STATE_READY -> "READY"
                        Player.STATE_ENDED -> "ENDED"
                        else -> "UNKNOWN"
                    }

                    Log.d(
                        TAG,
                        "Playback state: $state"
                    )
                }

                override fun onIsPlayingChanged(
                    isPlaying: Boolean
                ) {
                    Log.d(
                        TAG,
                        "Is playing: $isPlaying"
                    )
                }

                override fun onPlayerError(
                    error: PlaybackException
                ) {
                    Log.e(
                        TAG,
                        "PLAYER ERROR"
                    )

                    Log.e(
                        TAG,
                        "Error code: ${error.errorCode}"
                    )

                    Log.e(
                        TAG,
                        "Error name: ${error.errorCodeName}"
                    )

                    Log.e(
                        TAG,
                        "Error message: ${error.message}"
                    )

                    Log.e(
                        TAG,
                        "Cause: ${error.cause}"
                    )

                    error.printStackTrace()
                }
            }
        )
    }

    fun play(
        streamUrl: String,
        startPositionMs: Long = 0L
    ) {

        Log.d(
            TAG,
            "================================"
        )

        Log.d(
            TAG,
            "PLAY REQUEST"
        )

        Log.d(
            TAG,
            "Stream URL: $streamUrl"
        )

        Log.d(
            TAG,
            "================================"
        )

        val mediaItem =
            MediaItem.fromUri(streamUrl)

        Log.d(
            TAG,
            "MediaItem URI: ${mediaItem.localConfiguration?.uri}"
        )

        player.setMediaItem(mediaItem)

        Log.d(
            TAG,
            "Media item set"
        )

        player.prepare()

        if (startPositionMs > 0L) {
            Log.d(
                TAG,
                "Seeking to saved position: ${startPositionMs}ms"
            )

            player.seekTo(startPositionMs)
        }

        Log.d(
            TAG,
            "Player prepared"
        )

        player.playWhenReady = true

        Log.d(
            TAG,
            "playWhenReady = ${player.playWhenReady}"
        )
    }

    fun pause() {
        Log.d(TAG, "Pause requested")
        player.pause()
    }

    fun resume() {
        Log.d(TAG, "Resume requested")
        player.play()
    }

    fun stop() {
        Log.d(TAG, "Stop requested")
        player.stop()
        player.clearMediaItems()
    }

    fun release() {
        Log.d(TAG, "Player released")
        player.release()
    }

    fun addListener(listener: Player.Listener) {
        player.addListener(listener)
    }

    fun removeListener(listener: Player.Listener) {
        player.removeListener(listener)
    }
}