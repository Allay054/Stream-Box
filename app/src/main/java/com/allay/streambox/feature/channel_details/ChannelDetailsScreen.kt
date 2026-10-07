package com.allay.streambox.feature.channel_details

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import com.allay.streambox.feature.channels.ChannelItem

@Composable
fun ChannelDetailsScreen(
    channel: ChannelItem,
    onWatchNow: () -> Unit,
    onBack: () -> Unit
) {
    val watchFocusRequester = remember {
        FocusRequester()
    }

    LaunchedEffect(channel.id) {
        Log.d(
            "StreamBoxNavigation",
            "Channel Details screen ready: ${channel.id}"
        )

        try {
            watchFocusRequester.requestFocus()

            Log.d(
                "StreamBoxNavigation",
                "Watch button focus requested: ${channel.id}"
            )
        } catch (exception: Exception) {
            Log.e(
                "StreamBoxNavigation",
                "Unable to request Watch button focus",
                exception
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 48.dp,
                vertical = 32.dp
            ),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = channel.name,
            style = MaterialTheme.typography.displaySmall
        )

        Text(
            text = if (channel.isLive) {
                "● LIVE"
            } else {
                "OFFLINE"
            },
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        Text(
            text = channel.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = if (channel.streamUrl != null) {
                "Stream available"
            } else {
                "No stream currently available"
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 12.dp)
        )

        /*
         * WATCH BUTTON
         */
        Button(
            onClick = {
                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "WATCH BUTTON CLICKED"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Channel ID: ${channel.id}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Channel Name: ${channel.name}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Stream URL: ${channel.streamUrl}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Stream Type: ${channel.streamType}"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "Calling onWatchNow()"
                )

                Log.d(
                    "StreamBoxNavigation",
                    "================================"
                )

                onWatchNow()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp)
                .focusRequester(watchFocusRequester)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = "Watch ${channel.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = if (channel.streamUrl != null) {
                        "Start watching this live channel"
                    } else {
                        "No playable stream is currently available"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        /*
         * BACK BUTTON
         */
        Button(
            onClick = {
                Log.d(
                    "StreamBoxNavigation",
                    "BACK BUTTON CLICKED: ${channel.id}"
                )

                onBack()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text(
                text = "BACK"
            )
        }
    }
}