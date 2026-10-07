package com.allay.streambox

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.app.PictureInPictureParams
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.allay.streambox.navigation.StreamBoxNavGraph
import com.allay.streambox.ui.theme.StreamBoxTheme

class MainActivity : ComponentActivity() {

    /**
     * True while PlayerScreen is active.
     */
    var isPlayerActive by mutableStateOf(false)

    /**
     * True while Android is currently displaying this Activity
     * in Picture-in-Picture mode.
     */
    var isInPipMode by mutableStateOf(false)
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isInPipMode = isInPictureInPictureMode
        }

        setContent {
            StreamBoxTheme {
                StreamBoxNavGraph()
            }
        }
    }

    /**
     * Android calls this when the user leaves the Activity,
     * including pressing the Home button.
     *
     * StreamBox intentionally enters PiP here while the player
     * is active.
     *
     * Back is NOT handled here. Back is normal navigation.
     */
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        Log.d(
            "StreamBoxPiP",
            "onUserLeaveHint() - playerActive=$isPlayerActive"
        )

        if (!isPlayerActive) {
            Log.d(
                "StreamBoxPiP",
                "Player is not active -> no PiP"
            )
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(
                "StreamBoxPiP",
                "PiP requires Android 8.0+"
            )
            return
        }

        if (isInPictureInPictureMode) {
            Log.d(
                "StreamBoxPiP",
                "Already in PiP -> no action"
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

            val entered =
                enterPictureInPictureMode(params)

            Log.d(
                "StreamBoxPiP",
                "Home -> PiP requested. entered=$entered"
            )

        } catch (exception: Exception) {

            Log.e(
                "StreamBoxPiP",
                "Failed to enter PiP from Home",
                exception
            )
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(
            isInPictureInPictureMode,
            newConfig
        )

        isInPipMode = isInPictureInPictureMode

        Log.d(
            "StreamBoxPiP",
            "PiP state changed: $isInPictureInPictureMode"
        )
    }

    override fun onDestroy() {

        Log.d(
            "StreamBoxPiP",
            "MainActivity onDestroy()"
        )

        isPlayerActive = false
        isInPipMode = false

        super.onDestroy()
    }
}
