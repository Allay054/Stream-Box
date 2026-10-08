package com.allay.streambox.feature.history

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import com.allay.streambox.feature.channels.ChannelItem

@Composable
fun WatchHistoryScreen(
    channels: List<ChannelItem>,
    historyChannelIds: List<String>,
    onChannelClick: (ChannelItem) -> Unit,
    onClearHistory: () -> Unit,
    onBack: () -> Unit
) {
    Log.d(
        "StreamBoxHistory",
        "WatchHistoryScreen: " +
                "historyIds=${historyChannelIds.size} " +
                "channels=${channels.size}"
    )

    val historyChannels = historyChannelIds.mapNotNull { historyId ->
        channels.firstOrNull { channel ->
            channel.id == historyId
        }
    }

    Log.d(
        "StreamBoxHistory",
        "Resolved history channels: " +
                historyChannels.map { it.id }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(
                horizontal = 32.dp,
                vertical = 24.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Watch History",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.width(16.dp)
            )

            Text(
                text = "${historyChannels.size} channels",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.65f)
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            if (historyChannels.isNotEmpty()) {

                HistoryClearButton(
                    onClick = {
                        Log.d(
                            "StreamBoxHistory",
                            "CLEAR HISTORY button clicked"
                        )

                        onClearHistory()
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        if (historyChannels.isEmpty()) {

            HistoryEmptyState(
                onBack = onBack
            )

        } else {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(
                    minSize = 180.dp
                ),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = 32.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                content = {

                    items(
                        items = historyChannels,
                        key = { channel ->
                            channel.id
                        }
                    ) { channel ->

                        HistoryChannelCard(
                            channel = channel,
                            onClick = {

                                Log.d(
                                    "StreamBoxHistory",
                                    "History channel selected: " +
                                            "${channel.id} ${channel.name}"
                                )

                                onChannelClick(channel)
                            }
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryClearButton(
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .width(150.dp)
            .height(46.dp)
            .background(
                color = if (isFocused) {
                    Color.White.copy(alpha = 0.18f)
                } else {
                    Color.Transparent
                },
                shape = shape
            )
            .border(
                width = if (isFocused) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (isFocused) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.35f)
                },
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused = focusState.isFocused

                if (focusState.isFocused) {
                    Log.d(
                        "StreamBoxHistory",
                        "CLEAR HISTORY focused"
                    )
                }
            }
            .focusable()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "CLEAR HISTORY",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun HistoryChannelCard(
    channel: ChannelItem,
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isFocused) {
                    Color.White.copy(alpha = 0.14f)
                } else {
                    Color.White.copy(alpha = 0.07f)
                }
            )
            .border(
                width = if (isFocused) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (isFocused) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.18f)
                },
                shape = shape
            )
            .onFocusChanged { focusState ->

                isFocused = focusState.isFocused

                if (focusState.isFocused) {
                    Log.d(
                        "StreamBoxHistory",
                        "History focused: ${channel.id}"
                    )
                }
            }
            .focusable()
            .clickable {
                onClick()
            }
            .padding(12.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .background(
                    Color.White.copy(alpha = 0.08f)
                ),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = channel.name
                    .take(1)
                    .uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = channel.name,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = if (channel.isLive) {
                "● LIVE"
            } else {
                "OFFLINE"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (channel.isLive) {
                Color.White.copy(alpha = 0.85f)
            } else {
                Color.White.copy(alpha = 0.5f)
            }
        )
    }
}

@Composable
private fun HistoryEmptyState(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "No watch history yet",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Channels you watch will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.65f)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        HistoryBackButton(
            onClick = {

                Log.d(
                    "StreamBoxHistory",
                    "History empty state BACK clicked"
                )

                onBack()
            }
        )
    }
}

@Composable
private fun HistoryBackButton(
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .width(110.dp)
            .height(44.dp)
            .background(
                color = if (isFocused) {
                    Color.White.copy(alpha = 0.18f)
                } else {
                    Color.Transparent
                },
                shape = shape
            )
            .border(
                width = if (isFocused) {
                    2.dp
                } else {
                    1.dp
                },
                color = if (isFocused) {
                    Color.White
                } else {
                    Color.White.copy(alpha = 0.35f)
                },
                shape = shape
            )
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusable()
            .clickable {
                onClick()
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "BACK",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}