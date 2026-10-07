package com.allay.streambox.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.allay.streambox.feature.home.HomeChannel

@Composable
fun ChannelRow(
    channels: List<HomeChannel>,
    onChannelClick: (HomeChannel) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = channels,
            key = { it.id }
        ) { channel ->

            Box(
                modifier = Modifier
                    .focusable()
                    .clickable {
                        onChannelClick(channel)
                    }
                    .background(
                        color = Color.LightGray
                    )
                    .padding(
                        horizontal = 20.dp,
                        vertical = 14.dp
                    )
            ) {

                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}