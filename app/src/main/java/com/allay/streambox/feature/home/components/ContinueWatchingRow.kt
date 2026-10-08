package com.allay.streambox.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import com.allay.streambox.feature.home.ContinueWatchingItem

@Composable
fun ContinueWatchingRow(
    items: List<ContinueWatchingItem>,
    onItemClick: (ContinueWatchingItem) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = items,
            key = { it.channel.id }
        ) { item ->
            ContinueWatchingCard(
                item = item,
                onClick = {
                    onItemClick(item)
                }
            )
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    item: ContinueWatchingItem,
    onClick: () -> Unit
) {
    var isFocused by remember {
        mutableStateOf(false)
    }

    val progress =
        if (item.durationMs > 0L) {
            (item.positionMs.toFloat() /
                    item.durationMs.toFloat())
                .coerceIn(0f, 1f)
        } else {
            0f
        }

    Card(
        onClick = onClick,
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .focusable()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = item.channel.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1
            )

            Text(
                text = "Continue watching",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
                    .height(6.dp)
                    .background(
                        Color.Black.copy(alpha = 0.18f),
                        RoundedCornerShape(6.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(
                            if (isFocused) {
                                Color.White
                            } else {
                                Color.Black.copy(alpha = 0.65f)
                            },
                            RoundedCornerShape(6.dp)
                        )
                )
            }

            Row(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = formatDuration(item.positionMs),
                    style = MaterialTheme.typography.labelSmall
                )

                Text(
                    text = " / ${formatDuration(item.durationMs)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

private fun formatDuration(milliseconds: Long): String {
    val totalSeconds =
        (milliseconds.coerceAtLeast(0L) / 1000L)

    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
